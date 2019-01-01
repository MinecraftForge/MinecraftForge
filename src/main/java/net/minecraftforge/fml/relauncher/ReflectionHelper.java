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

package net.minecraftforge.fml.relauncher;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 * Some reflection helper code.
 *
 * This is in FML's coremod land.
 * It's split in two because the two classes need to exist in separate classloaders.
 * Modders shouldn't be using this class anyways.
 *
 * Use {@link net.minecraftforge.fml.common.ObfuscationReflectionHelper}
 * when dealing with obfuscated fields or methods.
 *
 * @author cpw
 * @deprecated not for external use
 */
@Deprecated // TODO: remove/relocate/replace
public class ReflectionHelper
{
    public static class UnableToFindMethodException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        @Deprecated // TODO: remove
        public UnableToFindMethodException(String[] methodNames, Exception failed)
        {
            super(failed);
        }

        public UnableToFindMethodException(Throwable failed)
        {
            super(failed);
        }
    }

    public static class UnableToFindClassException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        public UnableToFindClassException(String[] classNames, @Nullable Exception err)
        {
            super(err);
        }
    }

    public static class UnableToAccessFieldException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        @Deprecated // TODO: remove
        public UnableToAccessFieldException(String[] fieldNames, Exception e)
        {
            super(e);
        }

        public UnableToAccessFieldException(Exception e)
        {
            super(e);
        }
    }

    public static class UnableToFindFieldException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        @Deprecated // TODO: remove
        public UnableToFindFieldException(String[] fieldNameList, Exception e)
        {
            super(e);
        }

        public UnableToFindFieldException(Exception e)
        {
            super(e);
        }
    }

    public static class UnknownConstructorException extends RuntimeException
    {
        public UnknownConstructorException(final String message)
        {
            super(message);
        }
    }

    /** @deprecated use {@link #findField(Class, String, String)} */
    @Deprecated // TODO: remove
    public static Field findField(Class<?> clazz, String... fieldNames)
    {
        Exception failed = null;
        for (String fieldName : fieldNames)
        {
            try
            {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            }
            catch (Exception e)
            {
                failed = e;
            }
        }
        throw new UnableToFindFieldException(fieldNames, failed);
    }

    /**
     * Finds a field with the specified name in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the field is not found.
     *
     * @param clazz        The class to find the field on.
     * @param fieldName    The name of the field to find (used in developer environments, i.e. "maxStackSize").
     * @param fieldObfName The obfuscated name of the field to find (used in obfuscated environments, i.e. "field_77777_bU").
     *                     If the name you are looking for is on a class that is never obfuscated, this should be null.
     *
     * @return The field with the specified name in the given class.
     */
    @Nonnull
    public static Field findField(@Nonnull Class<?> clazz, @Nonnull String fieldName, @Nullable String fieldObfName)
    {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkArgument(StringUtils.isNotEmpty(fieldName), "Field name cannot be empty");

        String nameToFind = FMLLaunchHandler.isDeobfuscatedEnvironment() ? fieldName : MoreObjects.firstNonNull(fieldObfName, fieldName);

        try
        {
            Field f = clazz.getDeclaredField(nameToFind);
            f.setAccessible(true);
            return f;
        }
        catch (Exception e)
        {
            throw new UnableToFindFieldException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Deprecated // TODO: remove
    public static <T, E> T getPrivateValue(Class <? super E > classToAccess, @Nullable E instance, int fieldIndex)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            return (T) f.get(instance);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(e);
        }
    }

    /** @deprecated use {@link #getPrivateValue(Class, Object, String, String )} */
    @SuppressWarnings("unchecked")
    @Deprecated // TODO: remove
    public static <T, E> T getPrivateValue(Class <? super E > classToAccess, E instance, String... fieldNames)
    {
        try
        {
            return (T) findField(classToAccess, fieldNames).get(instance);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, @Nullable E instance, String fieldName, @Nullable String fieldObfName)
    {
        try
        {
            return (T) findField(classToAccess, fieldName, fieldObfName).get(instance);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(e);
        }
    }

    @Deprecated // TODO: remove
    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, E value, int fieldIndex)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(e);
        }
    }

    /** @deprecated use {@link #setPrivateValue(Class, Object, Object, String, String)} */
    @Deprecated // TODO: remove
    public static <T, E> void setPrivateValue(Class <? super T > classToAccess, T instance, E value, String... fieldNames)
    {
        try
        {
            findField(classToAccess, fieldNames).set(instance, value);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, @Nullable T instance, @Nullable E value, String fieldName, @Nullable String fieldObfName)
    {
        try
        {
            findField(classToAccess, fieldName, fieldObfName).set(instance, value);
        }
        catch (Exception e)
        {
            throw new UnableToAccessFieldException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? super Object> getClass(ClassLoader loader, String... classNames)
    {
        Exception err = null;
        for (String className : classNames)
        {
            try
            {
                return (Class<? super Object>) Class.forName(className, false, loader);
            }
            catch (Exception e)
            {
                err = e;
            }
        }

        throw new UnableToFindClassException(classNames, err);
    }

    /**
     * Finds a method with the specified name and parameters in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the method is not found.
     *
     * @param clazz          The class to find the method on.
     * @param methodName     The name of the method to find (used in developer environments, i.e. "getWorldTime").
     * @param methodObfName  The obfuscated name of the method to find (used in obfuscated environments, i.e. "func_72820_D").
     *                       If the name you are looking for is on a class that is never obfuscated, this should be null.
     * @param parameterTypes The parameter types of the method to find.
     * @return The method with the specified name and parameters in the given class.
     */
    @Nonnull
    public static Method findMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nullable String methodObfName, Class<?>... parameterTypes)
    {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkArgument(StringUtils.isNotEmpty(methodName), "Method name cannot be empty");

        String nameToFind = FMLLaunchHandler.isDeobfuscatedEnvironment() ? methodName : MoreObjects.firstNonNull(methodObfName, methodName);

        try
        {
            Method m = clazz.getDeclaredMethod(nameToFind, parameterTypes);
            m.setAccessible(true);
            return m;
        }
        catch (Exception e)
        {
            throw new UnableToFindMethodException(e);
        }
    }

    /**
     * Finds a constructor in the specified class that has matching parameter types.
     *
     * @param klass The class to find the constructor in
     * @param parameterTypes The parameter types of the constructor.
     * @param <T> The type
     * @return The constructor
     * @throws NullPointerException if {@code klass} is null
     * @throws NullPointerException if {@code parameterTypes} is null
     * @throws UnknownConstructorException if the constructor could not be found
     */
    @Nonnull
    public static <T> Constructor<T> findConstructor(@Nonnull final Class<T> klass, @Nonnull final Class<?>... parameterTypes)
    {
        Preconditions.checkNotNull(klass, "class");
        Preconditions.checkNotNull(parameterTypes, "parameter types");

        try
        {
            Constructor<T> constructor = klass.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (final NoSuchMethodException e)
        {
            final StringBuilder desc = new StringBuilder();
            desc.append(klass.getSimpleName());

            StringJoiner joiner = new StringJoiner(", ", "(", ")");
            for (Class<?> type : parameterTypes)
            {
                joiner.add(type.getSimpleName());
            }
            desc.append(joiner);

            throw new UnknownConstructorException("Could not find constructor '" + desc.toString() + "' in " + klass);
        }
    }
}
