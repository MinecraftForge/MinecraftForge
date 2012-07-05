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
import java.util.Arrays;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
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
            return cpw.mods.fml.relauncher.ReflectionHelper.getPrivateValue(classToAccess, instance, fieldIndex);
        }
        catch (UnableToAccessFieldException e)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("There was a problem getting field index %d from %s", fieldIndex, classToAccess.getName()), e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class <? super E > classToAccess, E instance, String... fieldNames)
    {
        try
        {
            return cpw.mods.fml.relauncher.ReflectionHelper.getPrivateValue(classToAccess, instance, fieldNames);
        }
        catch (UnableToFindFieldException e)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName()), e);
            throw e;
        }
        catch (UnableToAccessFieldException e)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("Unable to access any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName()), e);
            throw e;
        }
    }

    @Deprecated
    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, int fieldIndex, E value)
    {
        setPrivateValue(classToAccess, instance, value, fieldIndex);
    }

    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, E value, int fieldIndex)
    {
        try
        {
            cpw.mods.fml.relauncher.ReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldIndex);
        }
        catch (UnableToAccessFieldException e)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("There was a problem setting field index %d on type %s", fieldIndex, classToAccess.getName()));
            throw e;
        }
    }

    @Deprecated
    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, String fieldName, E value)
    {
        setPrivateValue(classToAccess, instance, value, fieldName);
    }

    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, E value, String... fieldNames)
    {
        try
        {
            cpw.mods.fml.relauncher.ReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldNames);
        }
        catch (UnableToFindFieldException e)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName()), e);
            throw e;
        }
        catch (UnableToAccessFieldException e)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("Unable to set any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName()), e);
            throw e;
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
