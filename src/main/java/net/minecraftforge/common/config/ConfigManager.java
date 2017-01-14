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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
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
    private static Map<Class<?>, ITypeAdapter.Map> MAP_ADAPTERS = Maps.newHashMap();
    private static Map<String, Configuration> CONFIGS = Maps.newHashMap();

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
        if (adpt instanceof ITypeAdapter.Map)
            MAP_ADAPTERS.put(cls, (ITypeAdapter.Map)adpt);
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

    public static void load(String modid, Config.Type type)
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
                String name = (String)targ.getAnnotationInfo().get("name");
                if (name == null)
                    name = modid;
                File file = new File(configDir, name + ".cfg");

                Configuration cfg = CONFIGS.get(file.getAbsolutePath());
                if (cfg == null)
                {
                    cfg = new Configuration(file);
                    cfg.load();
                    CONFIGS.put(file.getAbsolutePath(), cfg);
                }

                createConfig(cfg, cls, modid, type == Config.Type.INSTANCE);

                cfg.save();

            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "An error occurred trying to load a config for %s into %s", modid, targ.getClassName());
                throw new LoaderException(e);
            }
        }
    }

    // =======================================================
    //                    INTERNAL
    // =======================================================
    private static void createConfig(Configuration cfg, Class<?> cls, String modid, boolean isStatic)
    {
        String category = "general";
        for (Field f : cls.getDeclaredFields())
        {
            if (!Modifier.isPublic(f.getModifiers()))
                continue;
            if (Modifier.isStatic(f.getModifiers()) != isStatic)
                continue;

            createConfig(modid, category, cfg, f.getType(), f, null);
        }
    }

    private static final Joiner NEW_LINE = Joiner.on('\n');
    private static final Joiner PIPE = Joiner.on('|');

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void createConfig(String modid, String category, Configuration cfg, Class<?> ftype, Field f, Object instance)
    {
        Property prop = null;

        String comment = null;
        Comment ca = f.getAnnotation(Comment.class);
        if (ca != null)
            comment = NEW_LINE.join(ca.value());

        String langKey = modid + "." + category + "." + f.getName().toLowerCase(Locale.ENGLISH);
        LangKey la = f.getAnnotation(LangKey.class);
        if (la != null)
            langKey = la.value();

        ITypeAdapter adapter = ADAPTERS.get(ftype);

        if (adapter != null)
        {
            prop = adapter.getProp(cfg, category, f, instance, comment);
            set(instance, f, adapter.getValue(prop));
        }
        else if (ftype.getSuperclass() == Enum.class)
        {
            Enum enu = (Enum)get(instance, f);
            prop = cfg.get(category, f.getName(), enu.name(), comment);
            prop.setValidationPattern(makePattern((Class<? extends Enum>)ftype));
            set(instance, f, Enum.valueOf((Class<? extends Enum>)ftype, prop.getString()));
        }
        else if (ftype == Map.class)
        {
            String sub = category + "." + f.getName().toLowerCase(Locale.ENGLISH);
            Map<String, Object> m = (Map<String, Object>)get(instance, f);
            ParameterizedType type = (ParameterizedType)f.getGenericType();
            Type mtype = type.getActualTypeArguments()[1];

            cfg.getCategory(sub).setComment(comment);

            for (Entry<String, Object> e : m.entrySet())
            {
                ITypeAdapter.Map adpt = MAP_ADAPTERS.get(mtype);

                if (adpt != null)
                {
                    prop = adpt.getProp(cfg, sub, e.getKey(), e.getValue());
                }
                else if (mtype instanceof Class && ((Class<?>)mtype).getSuperclass() == Enum.class)
                {
                    prop = TypeAdapters.Str.getProp(cfg, sub, e.getKey(), ((Enum)e.getValue()).name());
                    prop.setValidationPattern(makePattern((Class<? extends Enum>)mtype));
                }
                else
                    throw new RuntimeException("Unknown type in map! " + f.getDeclaringClass() + "/" + f.getName() + " " + mtype);

                prop.setLanguageKey(langKey + "." + e.getKey().toLowerCase(Locale.ENGLISH));

            }
            prop = null;
        }
        else if (ftype.getSuperclass() == Object.class) //Only support classes that are one level below Object.
        {
            String sub = category + "." + f.getName().toLowerCase(Locale.ENGLISH);
            cfg.getCategory(sub).setComment(comment);

            Object sinst = get(instance, f);
            for (Field sf : ftype.getDeclaredFields())
            {
                if (!Modifier.isPublic(sf.getModifiers()))
                    continue;

                createConfig(modid, sub, cfg, sf.getType(), sf, sinst);
            }
        }
        // TODO Lists ? other stuff
        else
            throw new RuntimeException("Unknown type in config! " + f.getDeclaringClass() + "/" + f.getName() + " " + ftype);


        if (prop != null)
        {
            prop.setLanguageKey(langKey);
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

}
