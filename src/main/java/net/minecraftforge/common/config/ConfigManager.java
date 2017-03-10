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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation.EnumHolder;

public class ConfigManager
{
    private static Map<String, Multimap<Config.Type, ASMData>> asm_data = Maps.newHashMap();
    private static Map<Class<?>, ITypeAdapter> ADAPTERS = Maps.newHashMap();
    private static Map<String, Configuration> CONFIGS = Maps.newHashMap();
    private static Multimap<String, Class<?>> MOD_CONFIG_CLASSES = ArrayListMultimap.create();

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
                MOD_CONFIG_CLASSES.put(modid, cls);
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

                sync(cfg, cls, modid, type == Config.Type.INSTANCE, category, loading);

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
        return MOD_CONFIG_CLASSES.get(modid).toArray(new Class<?>[0]);
    }

    // =======================================================
    //                    INTERNAL
    // =======================================================
    static Configuration getConfiguration(String modid, String name) {
        if(Strings.isNullOrEmpty(name))
            name = modid;
        File configDir = Loader.instance().getConfigDir();
        File configFile = new File(configDir, name + ".cfg");
        return CONFIGS.get(configFile.getAbsolutePath());
    }
    
    private static void sync(Configuration cfg, Class<?> cls, String modid, boolean isStatic, String category, boolean loading)
    {
        for (Field f : cls.getDeclaredFields())
        {
            if (!Modifier.isPublic(f.getModifiers()))
                continue;
            if (Modifier.isStatic(f.getModifiers()) != isStatic)
                continue;

            syncField(modid, category, cfg, f.getType(), f, null, loading);
        }
    }

    private static final Joiner NEW_LINE = Joiner.on('\n');
    private static final Joiner PIPE = Joiner.on('|');

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void syncField(String modid, String category, Configuration cfg, Class<?> ftype, Field f, Object instance, boolean loading)
    {
        Property prop = null;

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

        ITypeAdapter adapter = ADAPTERS.get(ftype);

        if (adapter != null) //Has a type adapter for it
        {
            if (category.isEmpty())
                throw new RuntimeException("Can not specify a primitive field when the category is empty: " + f.getDeclaringClass() +"/" + f.getName());
            boolean exists = exists(cfg, category, getName(f));
            prop = property(cfg, category, getName(f), adapter.getType(), ftype.isArray());
            if (loading)
            {
                initializeProperty(prop, langKey, comment, requiresMcRestart, requiresWorldRestart);
                Object fieldValue = get(instance, f);
                adapter.setDefaultValue(prop, fieldValue);
                if(!exists)
                    adapter.setValue(prop, fieldValue);
                else
                    set(instance, f, adapter.getValue(prop));
            }
            else 
            {
                Object fieldValue = get(instance, f);
                Object propValue = adapter.getValue(prop);
                if (shouldReadFromVar(prop, propValue, fieldValue)) 
                {
                    adapter.setValue(prop, fieldValue);
                } 
                else
                {
                    set(instance, f, propValue);
                }
            }
        }
        else if (ftype.getSuperclass() == Enum.class) //Is a enum and write as String
        {
            if (category.isEmpty())
                throw new RuntimeException("Can not specify a primitive field when the category is empty: " + f.getDeclaringClass() +"/" + f.getName());
            Enum enu = (Enum)get(instance, f);
            boolean exists = exists(cfg, category, getName(f));
            prop = property(cfg, category, getName(f), TypeAdapters.Str.getType(), false);
            if(loading)
            {
                initializeProperty(prop, langKey, comment, requiresMcRestart, requiresWorldRestart);
                TypeAdapters.Str.setDefaultValue(prop, enu.name());
                if(!exists)
                    TypeAdapters.Str.setValue(prop, enu.name());
                else
                    set(instance, f, Enum.valueOf((Class<? extends Enum>)ftype, prop.getString()));
                prop.setValidationPattern(makePattern((Class<? extends Enum>)ftype));
            }
            else
            {    
                String propValue = prop.getString();
                
                if(shouldReadFromVar(prop, propValue, enu.name()))
                    TypeAdapters.Str.setValue(prop, enu.name());
                else
                    set(instance, f, Enum.valueOf((Class<? extends Enum>)ftype, propValue));
            }
        }
        else if (ftype == Map.class) //Syncs a map between config file and object aswell as config class.
        {
            if (category.isEmpty())
                throw new RuntimeException("Can not specify a primitive field when the category is empty: " + f.getDeclaringClass() +"/" + f.getName());
           
            String sub = category + "." + getName(f).toLowerCase(Locale.ENGLISH);
            Map<String, Object> m = (Map<String, Object>)get(instance, f);
            ParameterizedType type = (ParameterizedType)f.getGenericType();
            Type mtype = type.getActualTypeArguments()[1];
            ITypeAdapter adpt = ADAPTERS.get(mtype);
            boolean mapsToArrays = ((Class)mtype).isArray();

            ConfigCategory confCat = cfg.getCategory(sub);
            if(loading)
            {
                //Init category
                confCat.setComment(comment);
                confCat.setLanguageKey(langKey);
                confCat.setRequiresMcRestart(requiresMcRestart);
                confCat.setRequiresWorldRestart(requiresWorldRestart);
            }
            
            for(Property property : confCat.getOrderedValues())//Are new keys in the Configuration object?
            {
                if(loading || !m.containsKey(property.getName()))
                {
                    String propLangKey = langKey + "." + property.getName();
                    initializeProperty(property, propLangKey, null, requiresMcRestart, requiresWorldRestart);
                    
                    if(adpt != null)
                    {
                        if(!m.containsKey(property.getName()))
                            adpt.setDefaultValue(property, adpt.getValue(property));
                        else
                            adpt.setDefaultValue(property, m.get(property.getName()));
                        m.put(property.getName(), adpt.getValue(property));
                    }
                    else if(mtype instanceof Class && ((Class<?>)mtype).getSuperclass() == Enum.class)
                    {
                        String propValue = property.getString();
                        Enum val = Enum.valueOf((Class<? extends Enum>)mtype, propValue);
                        if(!m.containsKey(property.getName()))
                            TypeAdapters.Str.setDefaultValue(property, TypeAdapters.Str.getValue(property));
                        else
                            TypeAdapters.Str.setDefaultValue(property, m.get(property.getName()));
                        m.put(property.getName(), val);
                    }
                }
            }
            
            for(Entry<String, Object> e : m.entrySet())
            {
                if(!exists(cfg, sub, e.getKey())) //Are new programmatically added keys available?
                {
                    Property.Type propType;
                    if (adpt != null)
                        propType = adpt.getType();
                    else if (mtype instanceof Class && ((Class<?>)mtype).getSuperclass() == Enum.class)
                        propType = TypeAdapters.Str.getType();
                    else
                        throw new RuntimeException(String.format("A mod tried to pack a '%s' in a map!", mtype.getTypeName()));
                    
                    Property property = property(cfg, sub, e.getKey(), propType, mapsToArrays);
                    String propLangKey = langKey + "." + property.getName();
                    initializeProperty(property, propLangKey, null, requiresMcRestart, requiresWorldRestart);
                    
                    if (adpt != null)
                    {
                        adpt.setDefaultValue(property, e.getValue());
                        adpt.setValue(property, e.getValue());
                    }
                    else if (mtype instanceof Class && ((Class<?>)mtype).getSuperclass() == Enum.class)
                    {
                        String defaultValue = ((Enum)e.getValue()).name();
                        TypeAdapters.Str.setDefaultValue(property, defaultValue);
                        TypeAdapters.Str.setValue(property, defaultValue);
                    }
                }
                else //If the key is not new, sync according to shoudlReadFromVar()
                {
                    Property.Type propType;
                    if (adpt != null)
                        propType = adpt.getType();
                    else if (mtype instanceof Class && ((Class<?>)mtype).getSuperclass() == Enum.class)
                        propType = TypeAdapters.Str.getType();
                    else
                        throw new RuntimeException(String.format("A mod tried to pack a '%s' in a map!", mtype.getTypeName()));
                    
                    Property property = property(cfg, sub, e.getKey(), propType, mapsToArrays);
                    if (adpt != null)
                    {
                        Object propVal = adpt.getValue(property);
                        Object mapVal = e.getValue();
                        if (shouldReadFromVar(property, propVal, mapVal))
                            adpt.setValue(property, mapVal);
                        else
                            e.setValue(propVal);
                    }
                    else if (mtype instanceof Class && ((Class<?>)mtype).getSuperclass() == Enum.class)
                    {
                        String propVal = property.getString();
                        String mapVal = ((Enum)e.getValue()).name();
                        if(shouldReadFromVar(property, propVal, mapVal))
                            TypeAdapters.Str.setValue(property, mapVal);
                        else
                            e.setValue(Enum.valueOf((Class<? extends Enum>)ftype, propVal));
                    }
                }
            }
            prop = null;
        }
        else if (ftype.getSuperclass() == Object.class) //Only support classes that are one level below Object.
        {
            String sub = (category.isEmpty() ? "" : category + ".") + getName(f).toLowerCase(Locale.ENGLISH);
            ConfigCategory confCat = cfg.getCategory(sub);
            confCat.setComment(comment);
            confCat.setLanguageKey(langKey);
            confCat.setRequiresMcRestart(requiresMcRestart);
            confCat.setRequiresWorldRestart(requiresWorldRestart);
            
            Object sinst = get(instance, f);
            for (Field sf : ftype.getDeclaredFields())
            {
                if (!Modifier.isPublic(sf.getModifiers()))
                    continue;

                syncField(modid, sub, cfg, sf.getType(), sf, sinst, loading);
            }
        }
        // TODO Lists ? other stuff
        else
            throw new RuntimeException("Unknown type in config! " + f.getDeclaringClass() + "/" + f.getName() + " " + ftype);


        if (prop != null && loading)
        {
            RangeInt ia = f.getAnnotation(RangeInt.class);
            if (ia != null)
            {
                prop.setMinValue(ia.min());
                prop.setMaxValue(ia.max());
                if (comment != null)
                    prop.setComment(NEW_LINE.join(new String[]{comment, "Min: " + ia.min(), "Max: " + ia.max()}));
                else
                    prop.setComment(NEW_LINE.join(new String[]{"Min: " + ia.min(), "Max: " + ia.max()}));
            }
            RangeDouble da = f.getAnnotation(RangeDouble.class);
            if (da != null)
            {
                prop.setMinValue(da.min());
                prop.setMaxValue(da.max());
                if (comment != null)
                    prop.setComment(NEW_LINE.join(new String[]{comment, "Min: " + da.min(), "Max: " + da.max()}));
                else
                    prop.setComment(NEW_LINE.join(new String[]{"Min: " + da.min(), "Max: " + da.max()}));
            }

            //TODO List length values
        }
    }
    
    private static void initializeProperty(Property prop, String langKey, String comment, boolean requiresMcRestart, boolean requiresWorldRestart)
    {
        prop.setComment(comment);
        prop.setLanguageKey(langKey);
        prop.setRequiresMcRestart(requiresMcRestart);
        prop.setRequiresWorldRestart(requiresWorldRestart);
    }
    
    private static Property property(Configuration cfg, String category, String property, Property.Type type, boolean isList)
    {
        Property prop = cfg.getCategory(category).get(property);
        if(prop == null)
        {
            if(isList)
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
        if(!propValue.equals(fieldValue))
        {
            if(property.hasChanged())
                return false;
            else
                return true;
        }
        return false;
    }

    private static void set(Object instance, Field f, Object v)
    {
        try {
            f.set(instance, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Object get(Object instance, Field f)
    {
        try {
            return f.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @SuppressWarnings("rawtypes")
    private static Pattern makePattern(Class<? extends Enum> cls)
    {
        List<String> lst = Lists.newArrayList();
        for (Enum e : cls.getEnumConstants())
            lst.add(e.name());
        return Pattern.compile(PIPE.join(lst));
    }

    private static String getName(Field f)
    {
        if (f.isAnnotationPresent(Name.class))
            return f.getAnnotation(Name.class).value();
        return f.getName();
    }

}
