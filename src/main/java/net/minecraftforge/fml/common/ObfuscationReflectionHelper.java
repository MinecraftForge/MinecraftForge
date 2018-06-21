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

package net.minecraftforge.fml.common;

import java.util.Arrays;

import net.minecraftforge.fml.common.asm.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

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
            FMLLog.log.error("There was a problem getting field index {} from {}", classToAccess.getName(), e);
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
            FMLLog.log.error("Unable to locate any field {} on type {}", Arrays.toString(fieldNames), classToAccess.getName(), e);
            throw e;
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log.error("Unable to access any field {} on type {}", classToAccess.getName(), e);
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
            FMLLog.log.error("There was a problem setting field index {} on type {}", classToAccess.getName(), e);
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
            FMLLog.log.error("Unable to locate any field {} on type {}", classToAccess.getName(), e);
            throw e;
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log.error("Unable to set any field {} on type {}", classToAccess.getName(), e);
            throw e;
        }
    }
}
