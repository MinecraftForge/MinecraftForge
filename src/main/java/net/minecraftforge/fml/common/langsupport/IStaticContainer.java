package net.minecraftforge.fml.common.langsupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public interface IStaticContainer
{
    @Nonnull
    Object getInstance();

    @Nonnull
    Class<?> getType();

    @Nullable
    IPropertyWrapper getProperty(String name);

    @Nonnull
    Iterable<IPropertyWrapper> getProperties();

    class ClassStaticContainer implements IStaticContainer
    {
        private final Class<?> clazz;
        private Map<String, IPropertyWrapper> properties;

        public ClassStaticContainer(Class<?> clazz)
        {
            this.clazz = clazz;
            this.properties = new HashMap<>();
        }

        @Nonnull
        @Override
        public Object getInstance()
        {
            return clazz;
        }

        @Nonnull
        @Override
        public Class<?> getType()
        {
            return clazz;
        }

        @Nullable
        @Override
        public IPropertyWrapper getProperty(String name)
        {
            buildPropertyMap();
            return properties.get(name);
        }

        @Nonnull
        @Override
        public Iterable<IPropertyWrapper> getProperties()
        {
            buildPropertyMap();
            return properties.values();
        }

        protected void buildPropertyMap()
        {
            if (properties == null)
            {
                for (Field field : clazz.getDeclaredFields())
                {
                    if (isValidField(field))
                    {
                        properties.put(field.getName(), new IPropertyWrapper.FieldWrapper(field, getFieldHolder()));
                    }
                }
            }
        }

        protected Object getFieldHolder()
        {
            return null;
        }

        protected boolean isValidField(Field field)
        {
            return Modifier.isStatic(field.getModifiers());
        }
    }

    class SingletonStaticContainer extends ClassStaticContainer
    {
        private final Object singleton;

        public SingletonStaticContainer(Object singleton)
        {
            super(singleton.getClass());
            this.singleton = singleton;
        }

        @Nonnull
        @Override
        public Object getInstance()
        {
            return singleton;
        }

        @Override
        protected Object getFieldHolder()
        {
            return singleton;
        }

        @Override
        protected boolean isValidField(Field field)
        {
            return !Modifier.isStatic(field.getModifiers());
        }
    }
}
