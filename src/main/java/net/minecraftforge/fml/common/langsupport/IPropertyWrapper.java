package net.minecraftforge.fml.common.langsupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface IPropertyWrapper
{
    String getName();

    Class<?> getType();

    void set(Object value, boolean ignoreAccess) throws IllegalAccessException;

    void get() throws IllegalAccessException;

    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

    class FieldWrapper implements IPropertyWrapper
    {
        private Object instance;
        private final Field field;

        public FieldWrapper(Field field, Object instance)
        {
            this.field = field;
            this.instance = instance;
        }

        @Override
        public String getName()
        {
            return field.getName();
        }

        @Override
        public Class<?> getType()
        {
            return field.getType();
        }

        @Override
        public void set(Object value, boolean ignoreAccess) throws IllegalAccessException
        {
            if (ignoreAccess)
                field.setAccessible(true);
            field.set(instance, value);
        }

        @Override
        public void get() throws IllegalAccessException
        {
            field.get(instance);
        }

        @Override
        public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)
        {
            return field.isAnnotationPresent(annotationClass);
        }
    }
}
