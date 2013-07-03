package net.minecraftforge.common.configuration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.configuration.Annotations.BlockID;
import net.minecraftforge.common.configuration.Annotations.ConfigurationClass;
import net.minecraftforge.common.configuration.Annotations.ConfigurationComment;
import net.minecraftforge.common.configuration.Annotations.ConfigurationData;
import net.minecraftforge.common.configuration.Annotations.ConfigurationOption;
import net.minecraftforge.common.configuration.Annotations.ItemID;
import cpw.mods.fml.common.FMLLog;

public final class Parser {

    /**
     * Parse the passed class for annotated fields with the
     * <i>@ConfigurationOption</i> or a class with <i>@ConfigurationClass</i>
     * 
     * @author Cazzar
     * @param instance
     *            the instance of the configurations option if it is non static
     * @param config
     *            The configuration file to base the parsing off.
     */
    public static void parse(Object instance, Configuration config)
    {
        Class clazz = instance.getClass();
        if (clazz.isAnnotationPresent(ConfigurationClass.class))
        {
            parseClass(instance, config);
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields)
        {
            if (!field.isAccessible())
            {
                field.setAccessible(true);
            }

            ConfigurationOption annotation = field.getAnnotation(ConfigurationOption.class);
            if (annotation == null)
            {
                continue;
            }

            String category = annotation.category();
            String comment = annotation.comment();
            String key = annotation.key();

            try
            {
                parseField(field, instance, category, key, comment, config);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void parseField(Field field, Object instance, String category, String key, String comment, Configuration config)
            throws IllegalArgumentException, IllegalAccessException
    {
        Class<?> type = field.getType();
        if (comment.trim().isEmpty())
        {
            comment = null;
        }

        ConfigurationData data = field.getAnnotation(ConfigurationData.class);

        if (type == boolean.class || type == Boolean.class)
        {
            Object def = field.get(instance);

            Object value;
            value = config.get(category, key, (Boolean) def, comment).getBoolean((Boolean) def);

            field.set(instance, value);
        }
        else if (type == double.class || type == Double.class)
        {
            Object def = field.get(instance);

            Object value;

            if (data != null)
            {
                String s1 = generateComment(data.min(), data.max(), (Double) def);
                comment = (comment == null) ? s1 : comment + " " + s1;
            }

            value = config.get(category, key, (Double) def, comment).getDouble((Double) def);

            if (data != null)
            {
                if (!inRange(data.min(), data.max(), (Double) value))
                {
                    FMLLog.severe("A value is out of range for %s.%s. Using default", category, key);
                    value = def;
                }
            }

            field.set(instance, value);
        }
        else if (type == String.class)
        {
            Object def = field.get(instance);

            Object value;
            value = config.get(category, key, (String) def, comment).getString();

            field.set(instance, value);
        }
        else if (type == Integer.class || type == int.class)
        {
            if (field.isAnnotationPresent(BlockID.class))
            {
                BlockID id = field.getAnnotation(BlockID.class);
                if (!id.comment().trim().isEmpty()) comment = id.comment();

                Object def = field.get(instance);

                Object value;
                value = config.getItem(category, key, (Integer) def, comment).getInt();

                field.set(instance, value);
                return;
            }
            if (field.isAnnotationPresent(ItemID.class))
            {
                ItemID id = field.getAnnotation(ItemID.class);
                if (!id.comment().trim().isEmpty()) comment = id.comment();

                Object def = field.get(instance);

                Object value;
                value = config.getBlock(category, key, (Integer) def, comment).getInt();

                field.set(instance, value);
                return;
            }

            Object def = field.get(instance);

            if (data != null)
            {
                String s1 = generateComment(data.min(), data.max(), (double) ((Integer) def).intValue());
                comment = (comment == null) ? s1 : comment + " " + s1;
            }

            Object value;
            value = config.get(category, key, (Integer) def, comment).getInt((Integer) def);

            if (data != null)
            {
                if (!inRange(data.min(), data.max(), (double) ((Integer) def).intValue()))
                {
                    FMLLog.severe("A value is out of range for %s.%s. Using default", category, key);
                    value = def;
                }
            }

            field.set(instance, value);
        }
        else if (type == float.class || type == Float.class)
        {
            Object def = field.get(instance);

            if (data != null)
            {
                String s1 = generateComment(data.min(), data.max(), (Float) def);
                comment = (comment == null) ? s1 : comment + " " + s1;
            }

            String value;
            value = config.get(category, key, String.valueOf(def), comment).getString();

            Float actual = Float.valueOf(value);

            if (data != null)
            {
                if (!inRange(data.min(), data.max(), (float) actual))
                {
                    FMLLog.severe("A value is out of range for %s.%s. Using default", category, key);
                    actual = (Float) def;
                }
            }

            field.set(instance, actual);
        }
        else if (type == String[].class)
        {
            Object def = field.get(instance);

            Object value;
            value = config.get(category, key, (String[]) def, comment).getStringList();

            field.set(instance, value);
        }
        else if (type == int[].class)
        {
            Object def = field.get(instance);

            if (data != null)
            {
                double[] arr = new double[((int[]) def).length];
                int[] val = (int[]) def;

                for (int i = 0; i < val.length; i++)
                {
                    arr[i] = (double) val[i];
                }

                String s1 = generateComment(data.min(), data.max(), arr);
                comment = (comment == null) ? s1 : comment + " " + s1;
            }

            Object value;
            value = config.get(category, key, (int[]) def, comment).getIntList();

            if (data != null)
            {
                // Darn java and not being able to cast java.lang.Integer to
                // java.lang.Double
                // It is one of my pet hates with java

                double[] arr = new double[((int[]) value).length];
                int[] val = (int[]) value;

                for (int i = 0; i < val.length; i++)
                {
                    arr[i] = (double) val[i];
                }

                if (!inRange(data.min(), data.max(), arr))
                {
                    FMLLog.severe("A value is out of range for %s.%s. Using default", category, key);
                    value = def;
                }
            }

            field.set(instance, value);
        }
        else if (type == double[].class)
        {
            Object def = field.get(instance);

            if (data != null)
            {
                String s1 = generateComment(data.min(), data.max(), (double[]) def);
                comment = (comment == null) ? s1 : comment + " " + s1;
            }

            Object value;
            value = config.get(category, key, (double[]) def, comment).getDoubleList();

            if (data != null)
            {
                if (!inRange(data.min(), data.max(), (double[]) value))
                {
                    FMLLog.severe("A value is out of range for %s.%s. Using default", category, key);
                    value = def;
                }
            }

            field.set(instance, value);
        }
        else if (type == boolean[].class)
        {
            Object def = field.get(instance);

            Object value;
            value = config.get(category, key, (boolean[]) def, comment).getBooleanList();

            field.set(instance, value);
        }
        else
        {
        }
    }

    private static void parseClass(Object instance, Configuration config)
    {
        Class<? extends Object> clazz = instance.getClass();

        ConfigurationClass annotation = clazz.getAnnotation(ConfigurationClass.class);
        if (annotation == null)
        {
            return;
        }

        String category = annotation.category().isEmpty() ? clazz.getSimpleName() : annotation.category();

        if (clazz.isAnnotationPresent(ConfigurationComment.class))
        {
            config.getCategory(category).setComment(clazz.getAnnotation(ConfigurationComment.class).value());
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields)
        {
            if (!field.isAccessible())
            {
                field.setAccessible(true);
            }
            ConfigurationComment commentAnnotation = field.getAnnotation(ConfigurationComment.class);
            String comment = commentAnnotation == null ? "" : commentAnnotation.value();
            String key = field.getName();

            try
            {
                parseField(field, instance, category, key, comment, config);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static boolean inRange(double min, double max, double... numbers)
    {
        for (double d : numbers)
        {
            if (min <= max && (!(d >= min && d <= max)))
            {
                return false;
            }
            // This is just if the dev gives something like min=10, max=1
            else if (min >= max && (!(d <= min && d >= max)))
            {
                return false;
            }
        }

        return true;
    }

    private static String generateComment(double min, double max, double... numbers)
    {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < numbers.length; i++)
        {
            sb.append(numbers[i]);
            if (i < numbers.length - 1)
            {
                sb.append(", ");
            }
        }
        sb.append("}");
        return String.format("[Min: %s, Max: %s, default(s): %s]", min, max, sb.toString());
    }
}
