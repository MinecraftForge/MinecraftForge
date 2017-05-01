/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation.EnumHolder;

public class ConfigManager
{
    private static Map<String, Multimap<Config.Type, ASMData>> asm_data = Maps.newHashMap();
    static Map<Class<?>, ITypeAdapter> ADAPTERS = Maps.newHashMap();
    static Map<Class<?>, Class<?>> ARRAY_REMAP = Maps.newHashMap();
    private static Map<String, Configuration> CONFIGS = Maps.newHashMap();
    private static Map<String, Set<Class<?>>> MOD_CONFIG_CLASSES = Maps.newHashMap();

    static
    {
        register(boolean.class,     TypeAdapters.bool);
        register(boolean[].class,   TypeAdapters.boolA);
        register(Boolean.class,     TypeAdapters.Bool);
        register(Boolean[].class,   TypeAdapters.BoolA);
        register(float.class,       TypeAdapters.flt);
        register(float[].class,     TypeAdapters.fltA);
        register(Float.class,       TypeAdapters.Flt);
        register(Float[].class,     TypeAdapters.FltA);
        register(double.class,      TypeAdapters.dbl);
        register(double[].class,    TypeAdapters.dblA);
        register(Double.class,      TypeAdapters.Dbl);
        register(Double[].class,    TypeAdapters.DblA);
        register(byte.class,        TypeAdapters.byt);
        register(byte[].class,      TypeAdapters.bytA);
        register(Byte.class,        TypeAdapters.Byt);
        register(Byte[].class,      TypeAdapters.BytA);
        register(char.class,        TypeAdapters.chr);
        register(char[].class,      TypeAdapters.chrA);
        register(Character.class,   TypeAdapters.Chr);
        register(Character[].class, TypeAdapters.ChrA);
        register(short.class,       TypeAdapters.shrt);
        register(short[].class,     TypeAdapters.shrtA);
        register(Short.class,       TypeAdapters.Shrt);
        register(Short[].class,     TypeAdapters.ShrtA);
        register(int.class,         TypeAdapters.int_);
        register(int[].class,       TypeAdapters.intA);
        register(Integer.class,     TypeAdapters.Int);
        register(Integer[].class,   TypeAdapters.IntA);
        register(String.class,      TypeAdapters.Str);
        register(String[].class,    TypeAdapters.StrA);


        ARRAY_REMAP.put(Boolean.class,   Boolean[].class  );
        ARRAY_REMAP.put(Float.class,     Float[].class    );
        ARRAY_REMAP.put(Double.class,    Double[].class   );
        ARRAY_REMAP.put(Byte.class,      Byte[].class     );
        ARRAY_REMAP.put(Character.class, Character[].class);
        ARRAY_REMAP.put(Short.class,     Short[].class    );
        ARRAY_REMAP.put(Integer.class,   Integer[].class  );
        ARRAY_REMAP.put(String.class,    String[].class   );
    }
    private static void register(Class<?> cls, ITypeAdapter adpt)
    {
        ADAPTERS.put(cls, adpt);
    }

    public static void loadData(ASMDataTable data)
    {
        FMLLog.fine("Loading @Config anotation data");
        for (ASMData target : data.getAll(Config.class.getName()))
        {
            String modid = (String)target.getAnnotationInfo().get("modid");
            Multimap<Config.Type, ASMData> map = asm_data.get(modid);
            if (map == null)
            {
                map = ArrayListMultimap.create();
                asm_data.put(modid, map);
            }

            EnumHolder tholder = (EnumHolder)target.getAnnotationInfo().get("type");
            Config.Type type = tholder == null ? Config.Type.INSTANCE : Config.Type.valueOf(tholder.getValue());

            map.put(type, target);
        }
    }

    /**
     * Bounces to sync().
     * TODO: remove
     */
    public static void load(String modid, Config.Type type)
    {
        sync(modid, type);
    }

    /**
     * Synchronizes configuration data between the file on disk, the {@code Configuration} object and the annotated
     * mod classes containing the configuration variables.
     *
     * When first called, this method will try to load the configuration from disk. If this fails, because the file
     * does not exist, it will be created with default values derived from the mods config classes variable default values
     * and comments and ranges, as well as configuration names based on the appropriate annotations found in {@code @Config}.
     *
     * Note, that this method is being called by the {@link FMLModContaier}, so the mod needn't call it in init().
     *
     * If this method is called after the initial load, it will check whether the values in the Configuration object differ
     * from the values in the corresponding variables. If they differ, it will either overwrite the variables if the Configuration
     * object is marked as changed (e.g. if it was changed with the ConfigGui) or otherwise overwrite the Configuration object's values.
     * It then proceeds to saving the changes to disk.
     * @param modid the mod's ID for which the configuration shall be loaded
     * @param type the configuration type, currently always {@code Config.Type.INSTANCE}
     */
    public static void sync(String modid, Config.Type type)
    {
        FMLLog.fine("Attempting to inject @Config classes into %s for type %s", modid, type);
        ClassLoader mcl = Loader.instance().getModClassLoader();
        File configDir = Loader.instance().getConfigDir();
        Multimap<Config.Type, ASMData> map = asm_data.get(modid);

        if (map == null)
            return;

        for (ASMData targ : map.get(type))
        {
            try
            {
                Class<?> cls = Class.forName(targ.getClassName(), true, mcl);

                if (MOD_CONFIG_CLASSES.get(modid) == null)
                    MOD_CONFIG_CLASSES.put(modid, Sets.<Class<?>>newHashSet());
                MOD_CONFIG_CLASSES.get(modid).add(cls);

                String name = (String)targ.getAnnotationInfo().get("name");
                if (name == null)
                    name = modid;
                String category = (String)targ.getAnnotationInfo().get("category");
                if (category == null)
                    category = "general";

                File file = new File(configDir, name + ".cfg");

                boolean loading = false;
                Configuration cfg = CONFIGS.get(file.getAbsolutePath());
                if (cfg == null)
                {
                    cfg = new Configuration(file);
                    cfg.load();
                    CONFIGS.put(file.getAbsolutePath(), cfg);
                    loading = true;
                }

                sync(cfg, cls, modid, category, loading, null);

                cfg.save();

            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "An error occurred trying to load a config for %s into %s", modid, targ.getClassName());
                throw new LoaderException(e);
            }
        }
    }

    public static Class<?>[] getModConfigClasses(String modid)
    {
        return (MOD_CONFIG_CLASSES.containsKey(modid) ? MOD_CONFIG_CLASSES.get(modid).toArray(new Class<?>[0]) : new Class<?>[0]);
    }

    public static boolean hasConfigForMod(String modid)
    {
        return asm_data.containsKey(modid);
    }

    // =======================================================
    //                    INTERNAL
    // =======================================================
    static Configuration getConfiguration(String modid, String name) {
        if (Strings.isNullOrEmpty(name))
            name = modid;
        File configDir = Loader.instance().getConfigDir();
        File configFile = new File(configDir, name + ".cfg");
        return CONFIGS.get(configFile.getAbsolutePath());
    }

    private static void sync(Configuration cfg, Class<?> cls, String modid, String category, boolean loading, Object instance)
    {
        for (Field f : cls.getDeclaredFields())
        {
            if (!Modifier.isPublic(f.getModifiers()))
                continue;
            if (Modifier.isStatic(f.getModifiers()) != (instance == null))
                continue;

            String comment = null;
            Comment ca = f.getAnnotation(Comment.class);
            if (ca != null)
                comment = NEW_LINE.join(ca.value());

            String langKey = modid + "." + (category.isEmpty() ? "" : category + ".") + f.getName().toLowerCase(Locale.ENGLISH);
            LangKey la = f.getAnnotation(LangKey.class);
            if (la != null)
                langKey = la.value();

            boolean requiresMcRestart = f.isAnnotationPresent(Config.RequiresMcRestart.class);
            boolean requiresWorldRestart = f.isAnnotationPresent(Config.RequiresWorldRestart.class);

            if (FieldWrapper.hasWrapperFor(f)) //Access the field
            {
                if (Strings.isNullOrEmpty(category))
                    throw new RuntimeException("An empty category may not contain anything but objects representing categories!");
                try
                {
                    IFieldWrapper wrapper = FieldWrapper.get(instance, f, category);
                    ITypeAdapter adapt = wrapper.getTypeAdapter();
                    Property.Type propType = adapt.getType();

                    for (String key : wrapper.getKeys())
                    {
                        String suffix = key.replaceFirst(wrapper.getCategory() + ".", "");

                        boolean existed = exists(cfg, wrapper.getCategory(), suffix);
                        if (!existed || loading) //Creates keys in category specified by the wrapper if new ones are programaticaly added
                        {
                            Property property = property(cfg, wrapper.getCategory(), suffix, propType, adapt.isArrayAdapter());

                            adapt.setDefaultValue(property, wrapper.getValue(key));
                            if (!existed)
                                adapt.setValue(property, wrapper.getValue(key));
                            else
                                wrapper.setValue(key, adapt.getValue(property));
                        }
                        else //If the key is not new, sync according to shoudlReadFromVar()
                        {
                            Property property = property(cfg, wrapper.getCategory(), suffix, propType, adapt.isArrayAdapter());
                            Object propVal = adapt.getValue(property);
                            Object mapVal = wrapper.getValue(key);
                            if (shouldReadFromVar(property, propVal, mapVal))
                                adapt.setValue(property, mapVal);
                            else
                                wrapper.setValue(key, propVal);
                        }
                    }


                    ConfigCategory confCat = cfg.getCategory(wrapper.getCategory());

                    for (Property property : confCat.getOrderedValues())//Are new keys in the Configuration object?
                    {
                        if (!wrapper.handlesKey(property.getName()))
                            continue;

                        if (loading || !wrapper.hasKey(property.getName()))
                        {
                            Object value = wrapper.getTypeAdapter().getValue(property);
                            wrapper.setValue(confCat.getName() + "." + property.getName(), value);
                        }
                    }

                    if (loading) //Doing this after the loops. The wrapper should set cosmetic stuff.
                        wrapper.setupConfiguration(cfg, comment, langKey, requiresMcRestart, requiresWorldRestart);

                }
                catch (Exception e) //If anything goes wrong, add the errored field and class.
                {
                    String format = "Error syncing field '%s' of class '%s'!";
                    String error = String.format(format, f.getName(), cls.getName());
                    throw new RuntimeException(error, e);
                }
            }
            else if (f.getType().getSuperclass() != null && f.getType().getSuperclass().equals(Object.class)) //Descend the object tree
            {
                Object newInstance = null;
                try
                {
                    newInstance = f.get(instance);
                }
                catch (Exception e)
                {
                    //This should never happen. Previous checks should eliminate this.
                    Throwables.propagate(e);
                }

                String sub = (category.isEmpty() ? "" : category + ".") + getName(f).toLowerCase(Locale.ENGLISH);
                ConfigCategory confCat = cfg.getCategory(sub);
                confCat.setComment(comment);
                confCat.setLanguageKey(langKey);
                confCat.setRequiresMcRestart(requiresMcRestart);
                confCat.setRequiresWorldRestart(requiresWorldRestart);

                sync(cfg, f.getType(), modid, sub, loading, newInstance);
            }
            else
            {
                String format = "Can't handle field '%s' of class '%s': Unknown type.";
                String error = String.format(format, f.getName(), cls.getCanonicalName());
                throw new RuntimeException(error);
            }
        }
    }

    static final Joiner NEW_LINE = Joiner.on('\n');
    static final Joiner PIPE = Joiner.on('|');

    private static Property property(Configuration cfg, String category, String property, Property.Type type, boolean isList)
    {
        Property prop = cfg.getCategory(category).get(property);
        if (prop == null)
        {
            if (isList)
                prop = new Property(property, new String[0], type);
            else
                prop = new Property(property, (String)null, type);
            cfg.getCategory(category).put(property, prop);
        }
        return prop;
    }

    private static boolean exists(Configuration cfg, String category, String property)
    {
        return cfg.hasCategory(category) && cfg.getCategory(category).containsKey(property);
    }

    private static boolean shouldReadFromVar(Property property, Object propValue, Object fieldValue)
    {
        if (!propValue.equals(fieldValue))
        {
            if (property.hasChanged())
                return false;
            else
                return true;
        }
        return false;
    }

    private static String getName(Field f)
    {
        if (f.isAnnotationPresent(Name.class))
            return f.getAnnotation(Name.class).value();
        return f.getName();
    }

}
