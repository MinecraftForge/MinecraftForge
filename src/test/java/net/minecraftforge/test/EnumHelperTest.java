package net.minecraftforge.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;

import org.junit.Assert;
import org.junit.Test;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EnumHelperTest
{
    private boolean failed = false;

    @Test
    public void testEnumHelperMethodsMatchEnumConstructors()
    {
        //Quick note to point out that Enum constructors start with (string name, int ordinal).
        //  EnumHelper methods by convention start with (string name), and the ordinal is calculated.
        for (Method method : EnumHelper.class.getDeclaredMethods())
        {
            String name = method.getName();
            if (!name.equals("addEnum") && name.startsWith("add"))
            {
                System.out.println("  " + method);

                Class<?> returnType = method.getReturnType();
                Constructor<?>[] declaredConstructors = returnType.getDeclaredConstructors();
                Class<?>[] actualParameters = method.getParameterTypes();

                Assert.assertTrue(actualParameters.length >= 1);
                Assert.assertEquals("First parameter of add method should be String (name)",
                        String.class,
                        actualParameters[0]);
                Assert.assertEquals(String.class, actualParameters[0]);

                for (Constructor<?> declaredConstructor : declaredConstructors)
                {
                    System.out.println("    " + declaredConstructor.toString());

                    Class<?>[] expectedParameters = declaredConstructor.getParameterTypes();

                    Assert.assertTrue(expectedParameters.length >= 2);
                    Assert.assertEquals("First parameter of enum constructor should be String (name)",
                            String.class,
                            expectedParameters[0]);
                    Assert.assertEquals("Second parameter of enum should be int (ordinal)",
                            int.class,
                            expectedParameters[1]);

                    for (int i = 1, j = 2; i < actualParameters.length && j < expectedParameters.length; i++, j++)
                    {
                        Assert.assertEquals(
                                "method Parameter " + i + " (" + actualParameters[i].getName() + ") " +
                                        "should match constructor parameter" + j + " (" + expectedParameters[j] + ")",
                                expectedParameters[j],
                                actualParameters[i]);
                    }

                    Assert.assertEquals("Length of parameters incorrect",
                            (expectedParameters.length - 2),
                            (actualParameters.length - 1));
                }
            }
        }
    }

    @Test
    public void testEnumHelperTypes()
    {
        System.out.println("Testing EnumHelper:");
        Object[][] types = (Object[][])ReflectionHelper.getPrivateValue(EnumHelper.class, null, "commonTypes");
        for (Object[] o : types)
            testType(o);

        System.out.println("Testing EnumHelperClient:");
        types = (Object[][])ReflectionHelper.getPrivateValue(EnumHelperClient.class, null, "clientTypes");
        for (Object[] o : types)
            testType(o);

        if (failed)
            throw new RuntimeException("Enum Helper test failed!");
        Assert.assertEquals(BiomeDictionary.Type.BEACH, BiomeDictionary.Type.getType("BEACH"));
        Assert.assertEquals(BiomeDictionary.Type.getType("NEWTYPE"), BiomeDictionary.Type.getType("NEWTYPE"));
    }

    private void testType(Object[] info)
    {
        Class<?> cls = (Class<?>)info[0];
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
