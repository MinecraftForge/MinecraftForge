/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.base.Preconditions;

/**
 * Some reflection helper code.
 * This may not work properly in Java 9 with its new, more restrictive, reflection management.
 * As such, if issues are encountered, please report them and we can see what we can do to expand
 * the compatibility.
 *
 * In other cases, AccessTransformers may be used.
 *
 * All field names should be passed in as SRG names, and this will automatically resolve if MCP mappings are detected.
 *
 */
@SuppressWarnings({"serial", "unchecked"})
public class ObfuscationReflectionHelper
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker REFLECTION = MarkerManager.getMarker("REFLECTION");
    private static final Map<String, String> map = new HashMap<>();
    private static boolean loaded = false;



    public static String remapName(INameMappingService.Domain domain, String name)
    {
        return FMLLoader.getNameFunction("srg").map(f->f.apply(domain, name)).orElse(name);
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, int fieldIndex)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            return (T)f.get(instance);
        }
        catch (Exception e)
        {
            LOGGER.error(REFLECTION, "There was a problem getting field index {} from {}", fieldIndex, classToAccess.getName(), e);
            throw new UnableToAccessFieldException(e);
        }
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String fieldName)
    {
        try
        {
            return (T)findField(classToAccess, remapName(INameMappingService.Domain.FIELD, fieldName)).get(instance);
        }
        catch (UnableToFindFieldException e)
        {
            LOGGER.error(REFLECTION,"Unable to locate field {} ({}) on type {}", fieldName, remapName(INameMappingService.Domain.FIELD, fieldName), classToAccess.getName(), e);
            throw e;
        }
        catch (IllegalAccessException e)
        {
            LOGGER.error(REFLECTION,"Unable to access field {} ({}) on type {}", fieldName, remapName(INameMappingService.Domain.FIELD, fieldName), classToAccess.getName(), e);
            throw new UnableToAccessFieldException(e);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex)
    {
        try
        {
            Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (IllegalAccessException e)
        {
            LOGGER.error("There was a problem setting field index {} on type {}", fieldIndex, classToAccess.getName(), e);
            throw new UnableToAccessFieldException(e);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String fieldName)
    {
        try
        {
            findField(classToAccess, remapName(INameMappingService.Domain.FIELD, fieldName)).set(instance, value);
        }
        catch (UnableToFindFieldException e)
        {
            LOGGER.error("Unable to locate any field {} on type {}", fieldName, classToAccess.getName(), e);
            throw e;
        }
        catch (IllegalAccessException e)
        {
            LOGGER.error("Unable to set any field {} on type {}", fieldName, classToAccess.getName(), e);
            throw new UnableToAccessFieldException(e);
        }
    }

    /**
     * Finds a method with the specified name and parameters in the given class and makes it accessible.
     * Note: For performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the method is not found.
     *
     * @param clazz          The class to find the method on.
     * @param methodName     The SRG (obfuscated) name of the method to find(e.g. "func_12820_D").
     * @param parameterTypes The parameter types of the method to find.
     * @return The method with the specified name and parameters in the given class.
     */
    @Nonnull
    public static Method findMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, Class<?>... parameterTypes)
    {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(methodName);
        Preconditions.checkArgument(!methodName.isEmpty(), "Method name cannot be empty");

        try
        {
            Method m = clazz.getDeclaredMethod(remapName(INameMappingService.Domain.METHOD, methodName), parameterTypes);
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

    private static Field findField(Class<?> clazz, String name)
    {
        try
        {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        }
        catch (Exception e)
        {
            throw new UnableToFindFieldException(e);
        }
    }

    public static class UnableToAccessFieldException extends RuntimeException
    {
        private UnableToAccessFieldException(Exception e)
        {
            super(e);
        }
    }

    public static class UnableToFindFieldException extends RuntimeException
    {
        private UnableToFindFieldException(Exception e)
        {
            super(e);
        }
    }

    public static class UnableToFindMethodException extends RuntimeException
    {
        public UnableToFindMethodException(Throwable failed)
        {
            super(failed);
        }
    }

    public static class UnknownConstructorException extends RuntimeException
    {
        public UnknownConstructorException(final String message)
        {
            super(message);
        }
    }
}
