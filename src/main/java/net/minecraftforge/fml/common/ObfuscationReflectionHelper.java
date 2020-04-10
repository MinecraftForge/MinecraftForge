/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;

/**
 * Some reflection helper code.
 *
 * @author cpw
 *
 */
public class ObfuscationReflectionHelper
{
    @Deprecated // TODO: remove
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, int fieldIndex)
    {
        try
        {
            return ReflectionHelper.getPrivateValue(classToAccess, instance, fieldIndex);
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log.error("There was a problem getting field index {} from {}", fieldIndex, classToAccess.getName(), e);
            throw e;
        }
    }

    @Deprecated // TODO: remove
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

    private static String remapFieldName(Class<?> clazz, String fieldName)
    {
        String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(Type.getInternalName(clazz));
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(internalClassName, fieldName, null);
    }

    private static String remapMethodName(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... parameterTypes)
    {
        String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(Type.getInternalName(clazz));
        Type[] params = Arrays.stream(parameterTypes).map(Type::getType).toArray(Type[]::new);
        String desc = Type.getMethodDescriptor(Type.getType(returnType), params);
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(internalClassName, methodName, desc);
    }

    /** @deprecated use {@link #getPrivateValue(Class, Object, String)} */
    @Deprecated // TODO: remove
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
            FMLLog.log.error("Unable to access any field {} on type {}", Arrays.toString(fieldNames), classToAccess.getName(), e);
            throw e;
        }
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, @Nullable E instance, String srgName)
    {
        return ReflectionHelper.getPrivateValue(classToAccess, instance, remapFieldName(classToAccess, srgName), null);
    }

    @Deprecated // TODO: remove
    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex)
    {
        try
        {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldIndex);
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log.error("There was a problem setting field index {} on type {}", fieldIndex, classToAccess.getName(), e);
            throw e;
        }
    }

    /** @deprecated use {@link #setPrivateValue(Class, Object, Object, String)} */
    @Deprecated // TODO: remove
    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames)
    {
        try
        {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, remapFieldNames(classToAccess.getName(), fieldNames));
        }
        catch (UnableToFindFieldException e)
        {
            FMLLog.log.error("Unable to locate any field {} on type {}", Arrays.toString(fieldNames), classToAccess.getName(), e);
            throw e;
        }
        catch (UnableToAccessFieldException e)
        {
            FMLLog.log.error("Unable to set any field {} on type {}", Arrays.toString(fieldNames), classToAccess.getName(), e);
            throw e;
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, @Nullable T instance, @Nullable E value, String srgName)
    {
        ReflectionHelper.setPrivateValue(classToAccess, instance, value, remapFieldName(classToAccess, srgName), null);
    }

    /**
     * Finds a field with the specified name in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the field is not found.
     *
     * @param clazz   The class to find the field on.
     * @param srgName The obfuscated name of the field to find.
     *
     * @return The field with the specified name in the given class.
     */
    public static Field findField(Class<?> clazz, String srgName)
    {
        return ReflectionHelper.findField(clazz, remapFieldName(clazz, srgName), null);
    }

    /**
     * Finds a method with the specified name and parameters in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the method is not found.
     *
     * @param clazz          The class to find the method on.
     * @param srgName        The obfuscated name of the method to find.
     * @param returnType     The return type of the method to find.
     * @param parameterTypes The parameter types of the method to find.
     *
     * @return The method with the specified name and type signature in the given class.
     */
    public static Method findMethod(Class<?> clazz, String srgName, Class<?> returnType, Class<?>... parameterTypes)
    {
        String mappedName = remapMethodName(clazz, srgName, returnType, parameterTypes);
        return ReflectionHelper.findMethod(clazz, mappedName, null, parameterTypes);
    }

    /**
     * Finds a constructor in the specified class that has matching parameter types.
     *
     * @param klass The class to find the constructor in
     * @param parameterTypes The parameter types of the constructor.
     * @param <T> The type
     *
     * @return The constructor
     */
    public static <T> Constructor<T> findConstructor(Class<T> klass, Class<?>... parameterTypes)
    {
        return ReflectionHelper.findConstructor(klass, parameterTypes);
    }
}
