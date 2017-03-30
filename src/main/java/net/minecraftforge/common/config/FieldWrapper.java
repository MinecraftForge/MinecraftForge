package net.minecraftforge.common.config;

import java.awt.List;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Throwables;

public abstract class FieldWrapper implements IFieldWrapper
{
    protected String category, name;

    protected Field field;
    protected Object instance;
    
    public FieldWrapper(String category, Field field, Object instance) {
        this.instance = instance;
        this.field = field;
        this.category = category;
        this.name = field.getName();
        
        if(field.isAnnotationPresent(Config.Name.class))
            this.name = field.getAnnotation(Config.Name.class).value();
        
        this.field.setAccessible(true); //Just in case
    }

    private static IFieldWrapper getFieldAdapter(Object instance, Field field, String category)
    {
        if (ConfigManager.ADAPTERS.get(field.getType()) != null)
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
    
    private static class MapWrapper extends FieldWrapper
    {
        private Map<String, Object> theMap = null;
        private Type mType;
        
        @SuppressWarnings("unchecked")
        private MapWrapper(String category, Field field, Object instance)
        {
            super(category, field, instance);
            
            try {
                theMap = (Map<String, Object>) field.get(instance);
            }
            catch (ClassCastException cce)
            {
                throw new IllegalArgumentException(String.format("The map '%s' of class '%s' must have the key type String!", field.getName(), field.getDeclaringClass().getCanonicalName()), cce);
            }
            catch (Exception e)
            {
                Throwables.propagate(e);
            }
            
            ParameterizedType type = (ParameterizedType)field.getGenericType();
            mType = type.getActualTypeArguments()[1];
            
            if(ConfigManager.ADAPTERS.get(mType) == null && !Enum.class.isAssignableFrom((Class<?>)mType))
                throw new IllegalArgumentException(String.format("The map '%s' of class '%s' has target values which are neither primitive nor an enum!", field.getName(), field.getDeclaringClass().getCanonicalName()));
        }

        @Override
        public ITypeAdapter getTypeAdapter()
        {
            ITypeAdapter adapter = ConfigManager.ADAPTERS.get(mType);
            if (adapter == null && Enum.class.isAssignableFrom((Class<?>) mType))
                adapter = TypeAdapters.Str;
            return adapter;
        }

        @Override
        public String[] getEntries()
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
            return theMap.get(key);
        }

        @Override
        public void setEntry(String key, Object value)
        {
            theMap.put(key, value);
        }

        @Override
        public boolean hasEntry(String name)
        {
            return theMap.containsKey(name);
        }

        @Override
        public boolean handlesEntry(String name)
        {
            if(name == null)
                return false;
            return name.startsWith(category + "." + name + ".");
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
            if (!hasEntry(key))
                throw new IllegalArgumentException("Unsupported Key!");
            
            try
            {
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
        public void setEntry(String key, Object value)
        {
            if (!hasEntry(key))
                throw new IllegalArgumentException("Unsupported Key!");
            @SuppressWarnings("unchecked")
            Enum enu = Enum.valueOf((Class<? extends Enum>)field.getType(), (String) value);
            try
            {
                field.set(instance, enu);
            }
            catch (Exception e)
            {
                Throwables.propagate(e);
            }
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
            return ConfigManager.ADAPTERS.get(field.getType());
        }

        @Override
        public Object getValue(String key)
        {
            if (!hasEntry(key))
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
        public void setEntry(String key, Object value)
        {
            if (!hasEntry(key))
                throw new IllegalArgumentException("Unknown key!");
            try
            {
                field.set(instance, value);
            }
            catch (Exception e)
            {
                Throwables.propagate(e);
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
        public String[] getEntries()
        {
            return asArray(category + "." + name);
        }
        
        @Override
        public boolean hasEntry(String name)
        {
            return (this.category + "." + this.name).equals(name);
        }

        @Override
        public boolean handlesEntry(String name)
        {
            return hasEntry(name);
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
