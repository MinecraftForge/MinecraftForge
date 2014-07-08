package net.minecraftforge.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;

import org.junit.Assert;
import org.junit.Test;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class EnumHelperTest
{
    private boolean failed = false;

    @Test
    public void test()
    {
        System.out.println("Testing EnumHelper:");
        Object[][] types = (Object[][])ReflectionHelper.getPrivateValue(EnumHelper.class, null, "commonTypes");
        for (Object[] o : types)
            testType(o);

        System.out.println("Testing EnumHelperClient:");
        types = (Object[][])ReflectionHelper.getPrivateValue(EnumHelperClient.class, null, "clentTypes");
        for (Object[] o : types)
            testType(o);

        if (failed)
            throw new RuntimeException("Enum Helper test failed!");
        Assert.assertEquals(BiomeDictionary.Type.BEACH, BiomeDictionary.Type.getType("BEACH"));
        Assert.assertEquals(BiomeDictionary.Type.getType("NEWTYPE"), BiomeDictionary.Type.getType("NEWTYPE"));
    }
    private void testType(Object[] info)
    {
        Class<?> cls = (Class)info[0];
        Class<?>[] parameterTypes = new Class[info.length + 2 - 1];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        System.arraycopy(info, 1, parameterTypes, 2, info.length - 1);
        try
        {
            cls.getDeclaredConstructor(parameterTypes);
            System.out.println("  " + cls.getName() + ": Success");
        }
        catch (Exception e)
        {
            System.out.println("  " + cls.getName() + ": Failed");
            for (Constructor<?> c : cls.getDeclaredConstructors())
            {
                System.out.println("      " + c.toString());
            }
            failed = true;
        }
    }
}
