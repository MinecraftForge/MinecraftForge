package cpw.mods.fml.common;

import java.lang.reflect.Field;

public interface ILanguageAdapter {
    public Object getNewInstance(FMLModContainer container, Class<?> objectClass, ClassLoader classLoader) throws Exception;
    public boolean supportsStatics();
    public void setProxy(Field target, Class<?> proxyTarget, Object proxy) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException;

    public static class ScalaAdapter implements ILanguageAdapter {
        @Override
        public Object getNewInstance(FMLModContainer container, Class<?> scalaObjectClass, ClassLoader classLoader) throws Exception
        {
            Class<?> sObjectClass = Class.forName(scalaObjectClass.getName()+"$",true,classLoader);
            return sObjectClass.getField("MODULE$").get(null);
        }

        @Override
        public boolean supportsStatics()
        {
            return false;
        }

        @Override
        public void setProxy(Field target, Class<?> proxyTarget, Object proxy) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
        {
            Field field = proxyTarget.getField("INSTANCE");
            Object scalaObject = field.get(null);
            target.set(scalaObject, proxy);
        }
    }
    public static class JavaAdapter implements ILanguageAdapter {
        @Override
        public Object getNewInstance(FMLModContainer container, Class<?> objectClass, ClassLoader classLoader) throws Exception
        {
            return objectClass.newInstance();
        }

        @Override
        public boolean supportsStatics()
        {
            return true;
        }

        @Override
        public void setProxy(Field target, Class<?> proxyTarget, Object proxy) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
                SecurityException
        {
            target.set(null, proxy);
        }
    }
}