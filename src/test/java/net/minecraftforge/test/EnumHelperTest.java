/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.test;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.init.Bootstrap;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.EnumHelper;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnumHelperTest
{
    private static final Joiner COMMA = Joiner.on(",");

    private static void setBootstrap()
    {
        try
        {
            for (Field f : Bootstrap.class.getDeclaredFields())
            {
                if (Modifier.isPrivate(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && f.getType() == boolean.class)
                {
                    f.setAccessible(true);
                    f.set(null, true);
                }
            }
        }
        catch (Exception e)
        {
            Throwables.propagate(e);
        }
    }

    @Test
    public void testEnumHelperMethodsMatchEnumConstructors()
    {
        setBootstrap();

        boolean failed = false;
        Set<Constructor<?>> seenCtrs = new HashSet<Constructor<?>>();
        Set<Constructor<?>> matchedCtrs = new HashSet<Constructor<?>>();

        //Quick note to point out that Enum constructors start with (string name, int ordinal).
        //  EnumHelper methods by convention start with (string name), and the ordinal is calculated.
        List<Method> mtds = new ArrayList<Method>();
        mtds.addAll(Arrays.asList(EnumHelper.class.getDeclaredMethods()));
        mtds.addAll(Arrays.asList(EnumHelperClient.class.getDeclaredMethods()));

        for (Method method : mtds)
        {
            String name = method.getName();
            if (!name.equals("addEnum") && name.startsWith("add") && Modifier.isPublic(method.getModifiers()))
            {
                //System.out.println("  " + method);

                Class<?> returnType = method.getReturnType();
                Constructor<?>[] declaredConstructors = returnType.getDeclaredConstructors();
                Class<?>[] actualParameters = method.getParameterTypes();

                Assert.assertTrue(actualParameters.length >= 1);
                Assert.assertEquals("First parameter of add method should be String (name)",
                        String.class,
                        actualParameters[0]);

                actualParameters = Arrays.copyOfRange(actualParameters, 1, actualParameters.length); //Trim off name
                String info =
                        "  Method: " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "\n" +
                                "  Class: " + returnType.getName() + "\n" +
                                "  Actual:   " + COMMA.join(actualParameters);

                boolean found = false; // There can sometimes be multiple constructors.

                for (Constructor<?> declaredConstructor : declaredConstructors)
                {
                    boolean filter = declaredConstructor.isSynthetic();

                    if (returnType == EnumEnchantmentType.class && declaredConstructor.getParameterTypes().length == 2)
                    {
                        filter = true; //We don't want people using this method.
                    }
                    if (returnType == EntityLiving.SpawnPlacementType.class && declaredConstructor.getParameterTypes().length == 2)
                    {
                        filter = true; //We don't want people using this method.
                    }
                    if (returnType == HorseArmorType.class && (declaredConstructor.getParameterTypes().length == 3 || declaredConstructor.getParameterTypes()[2] == int.class))
                    {
                       filter = true; //We don't want people using either of these methods.
                    }

                    if (!filter)
                    {
                        seenCtrs.add(declaredConstructor);
                    }
                    //System.out.println("    " + declaredConstructor.toString());

                    Class<?>[] expectedParameters = declaredConstructor.getParameterTypes();

                    Assert.assertTrue(expectedParameters.length >= 2);
                    Assert.assertEquals("First parameter of enum constructor should be String (name)",
                            String.class,
                            expectedParameters[0]);
                    Assert.assertEquals("Second parameter of enum should be int (ordinal)",
                            int.class,
                            expectedParameters[1]);

                    expectedParameters = Arrays.copyOfRange(expectedParameters, 2, expectedParameters.length);

                    info += "\n  Expected: " + COMMA.join(expectedParameters);

                    if (Arrays.equals(actualParameters, expectedParameters))
                    {
                        matchedCtrs.add(declaredConstructor);
                        found = true;
                    }
                }

                if (!found)
                {
                    System.out.println("Parameters did not Match:");
                    System.out.println(info);
                    failed = true;
                }
            }
        }

        seenCtrs.removeAll(matchedCtrs);
        if (!seenCtrs.isEmpty())
        {
            for (Constructor<?> ctr : seenCtrs)
            {
                System.out.println("Missing accessor for Enum:");
                System.out.println("  Class: " + ctr.getName());
                System.out.println("  Params: " + COMMA.join(Arrays.copyOfRange(ctr.getParameterTypes(), 2, ctr.getParameterTypes().length)));
            }
            failed = true;
        }

        Assert.assertFalse(failed);
    }

    @Test
    public void testEnumHelperTypes()
    {
        setBootstrap();
        Assert.assertEquals(BiomeDictionary.Type.BEACH, BiomeDictionary.Type.getType("BEACH"));
        // This works, it's been tested, find a way to make this a unit test...
        //Assert.assertEquals(BiomeDictionary.Type.getType("NEWTYPE"), BiomeDictionary.Type.getType("NEWTYPE"));
    }
}
