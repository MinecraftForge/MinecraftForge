package net.minecraftforge.fml.common.langsupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface IPropertyWrapper
{
    String getName();

    Class<?> getType();

    void set(Object value, boolean ignoreAccess, boolean ignoreFinal) throws ReflectiveOperationException;

    Object get(boolean ignoreAccess) throws ReflectiveOperationException;

    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

    class FieldWrapper implements IPropertyWrapper
    {
        private static Field modifiersField;
        private static Object reflectionFactory;
        private static Method newFieldAccessor;
        private static Method fieldAccessorSet;
        private Object instance;
        private final Field field;
        private boolean removedAccessRestrictions;
        private boolean removedFinal;

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
        public void set(Object value, boolean ignoreAccess, boolean ignoreFinal) throws ReflectiveOperationException
        {
            if (ignoreAccess && !removedAccessRestrictions)
            {
                field.setAccessible(true);
                removedAccessRestrictions = true;
            }
            if (ignoreFinal)
            {
                removeFinal();
            }
            if (reflectionFactory == null)
            {
                Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
                reflectionFactory = getReflectionFactory.invoke(null);
                newFieldAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, boolean.class);
                fieldAccessorSet = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
            }
            Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
            fieldAccessorSet.invoke(fieldAccessor, instance, value);
        }

        @Override
        public Object get(boolean ignoreAccess) throws ReflectiveOperationException
        {
            if (ignoreAccess && !removedAccessRestrictions)
            {
                field.setAccessible(true);
                removedAccessRestrictions = true;
            }
            return field.get(instance);
        }

        @Override
        public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)
        {
            return field.isAnnotationPresent(annotationClass);
        }

        private void removeFinal() throws ReflectiveOperationException {
            if (removedFinal)
                return;
            if (modifiersField == null)
            {
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
            }
            removedFinal = true;
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }
    }
}
