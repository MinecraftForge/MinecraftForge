/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * Some reflection helper code.
 * 
 * @author cpw
 *
 */
public class ReflectionHelper
{
    public static boolean obfuscation;

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class <? super E > classToAccess, E instance, int fieldIndex)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            return (T) f.get(instance);
        }
        catch (Exception e)
        {
            FMLCommonHandler.instance().getFMLLogger().severe(String.format("There was a problem getting field %d from %s", fieldIndex, classToAccess.getName()));
            FMLCommonHandler.instance().getFMLLogger().throwing("ReflectionHelper", "getPrivateValue", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class <? super E > classToAccess, E instance, String fieldName)
    {
        try
        {
            Field f = classToAccess.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (T) f.get(instance);
        }
        catch (Exception e)
        {
            if ((fieldName.length() > 3 && !obfuscation) || (fieldName.length() <= 3 && obfuscation)) {
                FMLCommonHandler.instance().getFMLLogger().severe(String.format("There was a problem getting field %s from %s", fieldName, classToAccess.getName()));
                FMLCommonHandler.instance().getFMLLogger().throwing("ReflectionHelper", "getPrivateValue", e);
            }
            throw new RuntimeException(e);
        }
    }

    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, int fieldIndex, E value)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (Exception e)
        {
            FMLCommonHandler.instance().getFMLLogger().severe(String.format("There was a problem setting field %d from %s", fieldIndex, classToAccess.getName()));
            FMLCommonHandler.instance().getFMLLogger().throwing("ReflectionHelper", "getPrivateValue", e);
            throw new RuntimeException(e);
        }
    }

    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, String fieldName, E value)
    {
        try
        {
            Field f = classToAccess.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (Exception e)
        {
            if ((fieldName.length() > 3 && !obfuscation) || (fieldName.length() <= 3 && obfuscation)) {
                FMLCommonHandler.instance().getFMLLogger().severe(String.format("There was a problem setting field %s from %s", fieldName, classToAccess.getName()));
                FMLCommonHandler.instance().getFMLLogger().throwing("ReflectionHelper", "getPrivateValue", e);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     */
    public static void detectObfuscation(Class<?> clazz)
    {
        obfuscation=!clazz.getSimpleName().equals("World");
    }

}
