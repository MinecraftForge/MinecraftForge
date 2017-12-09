package net.minecraftforge.debug;

import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.ILanguageAdapter;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

//Just copy of the java adapter

public class TestAdapterService implements ILanguageAdapter
{

    @Override
    public Object getNewInstance(FMLModContainer container, Class<?> objectClass, ClassLoader classLoader, Method factoryMarkedMethod) throws Exception
    {
        if (factoryMarkedMethod != null)
        {
            return factoryMarkedMethod.invoke(null);
        }
        else
        {
            return objectClass.newInstance();
        }
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

    @Override
    public void setInternalProxies(ModContainer mod, Side side, ClassLoader loader)
    {
        // Nothing to do here.
    }

    @Override
    public String provides()
    {
        return "test";
    }
}