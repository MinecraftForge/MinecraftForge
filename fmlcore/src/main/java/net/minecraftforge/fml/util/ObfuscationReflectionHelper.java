/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.util;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringJoiner;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Some reflection helper code.
 * This may not work properly in Java 9 with its new, more restrictive, reflection management.
 * As such, if issues are encountered, please report them and we can see what we can do to expand
 * the compatibility.
 *
 * In other cases, AccessTransformers may be used.
 *
 * All field and method names should be passed in as SRG names, and this will automatically resolve if MCP mappings are detected.
 *
 */
@SuppressWarnings({"serial", "unchecked", "unused", "WeakerAccess"})
public class ObfuscationReflectionHelper
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker REFLECTION = MarkerManager.getMarker("REFLECTION");

    /**
     * Remaps a name using the SRG naming function
     * @param domain The {@link INameMappingService.Domain} to use to remap the name.
     * @param name   The name to try and remap.
     * @return The remapped name, or the original name if it couldn't be remapped.
     */
    @NotNull
    public static String remapName(INameMappingService.Domain domain, String name)
    {
        return FMLLoader.getNameFunction("srg").map(f->f.apply(domain, name)).orElse(name);
    }

    /**
     * Gets the value a field with the specified name in the given class.
     * Note: For performance, use {@link #findField(Class, String)} if you are getting the value more than once.
     * <p>
     * Throws an exception if the field is not found or the value of the field cannot be gotten.
     *
     * @param classToAccess The class to find the field on.
     * @param instance      The instance of the {@code classToAccess}.
     * @param fieldName     The SRG (unmapped) name of the field to find (e.g. "f_46443_").
     * @param <T>           The type of the value.
     * @param <E>           The type of the {@code classToAccess}.
     * @return The value of the field with the specified name in the {@code classToAccess}.
     * @throws UnableToAccessFieldException If there was a problem getting the field.
     * @throws UnableToAccessFieldException If there was a problem getting the value.
     */
    @Nullable
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String fieldName)
    {
        try
        {
            return (T) findField(classToAccess, fieldName).get(instance);
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

    /**
     * Sets the value a field with the specified name in the given class.
     * Note: For performance, use {@link #findField(Class, String)} if you are setting the value more than once.
     * <p>
     * Throws an exception if the field is not found or the value of the field cannot be set.
     *
     * @param classToAccess The class to find the field on.
     * @param instance      The instance of the {@code classToAccess}.
     * @param value         The new value for the field
     * @param fieldName     The name of the field in the {@code classToAccess}.
     * @param <T>           The type of the value.
     * @param <E>           The type of the {@code classToAccess}.
     * @throws UnableToFindFieldException   If there was a problem getting the field.
     * @throws UnableToAccessFieldException If there was a problem setting the value of the field.
     */
    public static <T, E> void setPrivateValue(@NotNull final Class<? super T> classToAccess, @NotNull final T instance, @Nullable final E value, @NotNull final String fieldName)
    {
        try
        {
            findField(classToAccess, fieldName).set(instance, value);
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
     * @param methodName     The SRG (unmapped) name of the method to find (e.g. "m_5776_").
     * @param parameterTypes The parameter types of the method to find.
     * @return The method with the specified name and parameters in the given class.
     * @throws NullPointerException        If {@code clazz} is null.
     * @throws NullPointerException        If {@code methodName} is null.
     * @throws IllegalArgumentException    If {@code methodName} is empty.
     * @throws NullPointerException        If {@code parameterTypes} is null.
     * @throws UnableToFindMethodException If the method could not be found.
     */
    @NotNull
    public static Method findMethod(@NotNull final Class<?> clazz, @NotNull final String methodName, @NotNull final Class<?>... parameterTypes)
    {
        Preconditions.checkNotNull(clazz, "Class to find method on cannot be null.");
        Preconditions.checkNotNull(methodName, "Name of method to find cannot be null.");
        Preconditions.checkArgument(!methodName.isEmpty(), "Name of method to find cannot be empty.");
        Preconditions.checkNotNull(parameterTypes, "Parameter types of method to find cannot be null.");

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
     * Finds a constructor with the specified parameter types in the given class and makes it accessible.
     * Note: For performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the constructor is not found.
     *
     * @param clazz          The class to find the constructor in.
     * @param parameterTypes The parameter types of the constructor.
     * @param <T>            The type.
     * @return The constructor with the specified parameters in the given class.
     * @throws NullPointerException        If {@code clazz} is null.
     * @throws NullPointerException        If {@code parameterTypes} is null.
     * @throws UnknownConstructorException If the constructor could not be found.
     */
    @NotNull
    public static <T> Constructor<T> findConstructor(@NotNull final Class<T> clazz, @NotNull final Class<?>... parameterTypes)
    {
        Preconditions.checkNotNull(clazz, "Class to find constructor on cannot be null.");
        Preconditions.checkNotNull(parameterTypes, "Parameter types of constructor to find cannot be null.");

        try
        {
            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (final NoSuchMethodException e)
        {
            final StringBuilder desc = new StringBuilder();
            desc.append(clazz.getSimpleName());

            StringJoiner joiner = new StringJoiner(", ", "(", ")");
            for (Class<?> type : parameterTypes)
            {
                joiner.add(type.getSimpleName());
            }
            desc.append(joiner);

            throw new UnknownConstructorException("Could not find constructor '" + desc.toString() + "' in " + clazz);
        }
    }

    /**
     * Finds a field with the specified name in the given class and makes it accessible.
     * Note: For performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the field is not found.
     *
     * @param clazz     The class to find the field on.
     * @param fieldName The SRG (unmapped) name of the field to find (e.g. "f_46443_").
     * @param <T>       The type.
     * @return The constructor with the specified parameters in the given class.
     * @throws NullPointerException       If {@code clazz} is null.
     * @throws NullPointerException       If {@code fieldName} is null.
     * @throws IllegalArgumentException   If {@code fieldName} is empty.
     * @throws UnableToFindFieldException If the field could not be found.
     */
    @NotNull
    public static <T> Field findField(@NotNull final Class<? super T> clazz, @NotNull final String fieldName)
    {
        Preconditions.checkNotNull(clazz, "Class to find field on cannot be null.");
        Preconditions.checkNotNull(fieldName, "Name of field to find cannot be null.");
        Preconditions.checkArgument(!fieldName.isEmpty(), "Name of field to find cannot be empty.");

        try
        {
            Field f = clazz.getDeclaredField(remapName(INameMappingService.Domain.FIELD, fieldName));
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
