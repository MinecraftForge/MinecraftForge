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
package net.minecraftforge.fml.common;

import java.util.Arrays;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

import org.apache.logging.log4j.Level;

/**
 * Some reflection helper code.
 *
 * @author cpw
 *
 */
public class ObfuscationReflectionHelper
{
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, int fieldIndex)
    {
        try
        {
            return ReflectionHelper.getPrivateValue(classToAccess, instance, fieldIndex);
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log(Level.ERROR, e, "There was a problem getting field index %d from %s", fieldIndex, classToAccess.getName());
            throw e;
        }
    }

    public static String[] remapFieldNames(String className, String... fieldNames)
    {
        String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        String[] mappedNames = new String[fieldNames.length];
        int i = 0;
        for (String fName : fieldNames)
        {
            mappedNames[i++] = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(internalClassName, fName, null);
        }
        return mappedNames;
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String... fieldNames)
    {
        try
        {
            return ReflectionHelper.getPrivateValue(classToAccess, instance, remapFieldNames(classToAccess.getName(),fieldNames));
        }
        catch (UnableToFindFieldException e)
        {
            FMLLog.log(Level.ERROR,e,"Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log(Level.ERROR, e, "Unable to access any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex)
    {
        try
        {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldIndex);
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log(Level.ERROR, e, "There was a problem setting field index %d on type %s", fieldIndex, classToAccess.getName());
            throw e;
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames)
    {
        try
        {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, remapFieldNames(classToAccess.getName(), fieldNames));
        }
        catch (UnableToFindFieldException e)
        {
            FMLLog.log(Level.ERROR, e, "Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log(Level.ERROR, e, "Unable to set any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
    }
}
