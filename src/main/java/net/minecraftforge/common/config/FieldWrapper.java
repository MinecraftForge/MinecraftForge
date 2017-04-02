package net.minecraftforge.common.config;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.Set;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;

import static net.minecraftforge.common.config.ConfigManager.*;

public abstract class FieldWrapper implements IFieldWrapper
{
    protected String category, name;

    protected Field field;
    protected Object instance;

    public FieldWrapper(String category, Field field, Object instance)
    {
        this.instance = instance;
        this.field = field;
        this.category = category;
        this.name = field.getName();

        if (field.isAnnotationPresent(Config.Name.class))
            this.name = field.getAnnotation(Config.Name.class).value();

        this.field.setAccessible(true); // Just in case
    }

    public static IFieldWrapper get(Object instance, Field field, String category)
    {
        if (ADAPTERS.get(field.getType()) != null)
            return new PrimitiveWrapper(category, field, instance);
        else if (Enum.class.isAssignableFrom(field.getType()))
            return new EnumWrapper(category, field, instance);
        else if (Map.class.isAssignableFrom(field.getType()))
            return new MapWrapper(category, field, instance);
        else if (field.getType().getSuperclass().equals(Object.class))
            throw new RuntimeException("Objects should not be handled by field wrappers");
        else
            throw new IllegalArgumentException(String.format("Fields of type '%s' are not supported!", field.getType().getCanonicalName()));
    }

    public static boolean hasWrapperFor(Field field)
    {
        if (ADAPTERS.get(field.getType()) != null)
            return true;
        else if (Enum.class.isAssignableFrom(field.getType()))
            return true;
        else if (Map.class.isAssignableFrom(field.getType()))
            return true;
        return false;
    }

    private static class MapWrapper extends FieldWrapper
    {
        private Map<String, Object> theMap = null;
        private Type mType;

        @SuppressWarnings("unchecked")
        private MapWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);

            try
            {
                theMap = (Map<String, Object>) field.get(instance);
            }
            catch (ClassCastException cce)
            {
                throw new IllegalArgumentException(String.format("The map '%s' of class '%s' must have the key type String!", field.getName(),
                        field.getDeclaringClass().getCanonicalName()), cce);
            }
            catch (Exception e)
            {
                Throwables.propagate(e);
            }

            ParameterizedType type = (ParameterizedType) field.getGenericType();
            mType = type.getActualTypeArguments()[1];

            if (ADAPTERS.get(mType) == null && !Enum.class.isAssignableFrom((Class<?>) mType))
                throw new IllegalArgumentException(String.format("The map '%s' of class '%s' has target values which are neither primitive nor an enum!",
                        field.getName(), field.getDeclaringClass().getCanonicalName()));

        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            ITypeAdapter adapter = ADAPTERS.get(mType);
            if (adapter == null && Enum.class.isAssignableFrom((Class<?>) mType))
                adapter = TypeAdapters.Str;
            return adapter;
        }

        @Override
        public String[] getKeys()
        {
            Set<String> keys = theMap.keySet();
            String[] keyArray = new String[keys.size()];

            Iterator<String> it = keys.iterator();
            for (int i = 0; i < keyArray.length; i++)
            {
                keyArray[i] = category + "." + name + "." + it.next();
            }

            return keyArray;
        }

        @Override
        public Object getValue(String key)
        {
            return theMap.get(key.replaceFirst(category + "." + name + ".", ""));
        }

        @Override
        public void setValue(String key, Object value)
        {
            String suffix = key.replaceFirst(category + "." + name + ".", "");
            theMap.put(suffix, value);
        }

        @Override
        public boolean hasKey(String name)
        {
            return theMap.containsKey(name);
        }

        @Override
        public boolean handlesKey(String name)
        {
            if (name == null)
                return false;
            return name.startsWith(category + "." + name + ".");
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart)
        {
            ConfigCategory confCat = cfg.getCategory(category);
            confCat.setComment(desc);
            confCat.setLanguageKey(langKey);
            confCat.setRequiresMcRestart(reqMCRestart);
            confCat.setRequiresWorldRestart(reqWorldRestart);
        }

        @Override
        public String getCategory()
        {
            return category + "." + name;
        }

    }

    private static class EnumWrapper extends SingleValueFieldWrapper
    {

        private EnumWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            return TypeAdapters.Str;
        }

        @Override
        public Object getValue(String key)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unsupported Key!");

            try
            {
                @SuppressWarnings("rawtypes")
                Enum enu = (Enum) field.get(instance);
                return enu.name();
            }
            catch (Exception e)
            {
                Throwables.propagate(e);
            }
            return null;
        }

        @Override
        public void setValue(String key, Object value)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unsupported Key!");
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Enum enu = Enum.valueOf((Class<? extends Enum>) field.getType(), (String) value);
            try
            {
                field.set(instance, enu);
            }
            catch (Exception e)
            {
                Throwables.propagate(e);
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart)
        {
            super.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart);

            Property prop = cfg.getCategory(this.category).get(this.name); // Will be setup in general by ConfigManager

            List<String> lst = Lists.newArrayList();
            for (Enum e : ((Class<? extends Enum>) field.getType()).getEnumConstants())
                lst.add(e.name());

            prop.setValidationPattern(Pattern.compile(PIPE.join(lst)));
            prop.setValidValues(lst.toArray(new String[0]));

            String validValues = NEW_LINE.join(lst);

            if (desc != null)
                prop.setComment(NEW_LINE.join(new String[] { desc, "Valid values:" }) + "\n" + validValues);
            else
                prop.setComment("Valid values:" + "\n" + validValues);
        }
    }

    private static class PrimitiveWrapper extends SingleValueFieldWrapper
    {

        private PrimitiveWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            return ADAPTERS.get(field.getType());
        }

        @Override
        public Object getValue(String key)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unknown key!");
            try
            {
                return field.get(instance);
            }
            catch (Exception e)
            {
                Throwables.propagate(e);
            }
            return null;
        }

        @Override
        public void setValue(String key, Object value)
        {
            if (!hasKey(key))
                throw new IllegalArgumentException("Unknown key: " + key);
            try
            {
                field.set(instance, value);
            }
            catch (Exception e)
            {
                Throwables.propagate(e);
            }
        }

        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart)
        {
            super.setupConfiguration(cfg, desc, langKey, reqMCRestart, reqWorldRestart);

            Property prop = cfg.getCategory(this.category).get(this.name);

            RangeInt ia = field.getAnnotation(RangeInt.class);
            if (ia != null)
            {
                prop.setMinValue(ia.min());
                prop.setMaxValue(ia.max());
                if (desc != null)
                    prop.setComment(NEW_LINE.join(new String[] { desc, "Min: " + ia.min(), "Max: " + ia.max() }));
                else
                    prop.setComment(NEW_LINE.join(new String[] { "Min: " + ia.min(), "Max: " + ia.max() }));
            }

            RangeDouble da = field.getAnnotation(RangeDouble.class);
            if (da != null)
            {
                prop.setMinValue(da.min());
                prop.setMaxValue(da.max());
                if (desc != null)
                    prop.setComment(NEW_LINE.join(new String[] { desc, "Min: " + da.min(), "Max: " + da.max() }));
                else
                    prop.setComment(NEW_LINE.join(new String[] { "Min: " + da.min(), "Max: " + da.max() }));
            }
        }
    }

    private static abstract class SingleValueFieldWrapper extends FieldWrapper
    {
        private SingleValueFieldWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);
        }

        @Override
        public String[] getKeys()
        {
            return asArray(this.category + "." + this.name);
        }

        @Override
        public boolean hasKey(String name)
        {
            return (this.category + "." + this.name).equals(name);
        }

        @Override
        public boolean handlesKey(String name)
        {
            return hasKey(name);
        }

        @Override
        public void setupConfiguration(Configuration cfg, String desc, String langKey, boolean reqMCRestart, boolean reqWorldRestart)
        {
            Property prop = cfg.getCategory(this.category).get(this.name); // Will be setup in general by ConfigManager

            prop.setComment(desc);
            prop.setLanguageKey(langKey);
            prop.setRequiresMcRestart(reqMCRestart);
            prop.setRequiresWorldRestart(reqWorldRestart);
        }

        @Override
        public String getCategory()
        {
            return category;
        }

    }

    private static <T> T[] asArray(T... in)
    {
        return in;
    }

    public static class BeanEntry<K, V> implements Entry<K, V>
    {
        private K key;
        private V value;

        public BeanEntry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey()
        {
            return key;
        }

        @Override
        public V getValue()
        {
            return value;
        }

        @Override
        public V setValue(V value)
        {
            throw new UnsupportedOperationException("This is a static bean.");
        }

    }
}
