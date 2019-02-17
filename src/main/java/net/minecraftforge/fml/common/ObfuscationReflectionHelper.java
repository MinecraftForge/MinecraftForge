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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.base.Preconditions;

/**
 * Some reflection helper code.
 * This may not work properly in Java9 with their new more restrictive reflection management.
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



    public static String[] remapNames(String... names)
    {
        loadMappings();
        if (map.isEmpty())
            return names;

        String[] mappedNames = new String[names.length];
        int i = 0;
        for (String name : names)
            mappedNames[i++] = map.getOrDefault(name, name);
        return mappedNames;
    }

    public static String remapName(String name)
    {
        loadMappings();
        if (map.isEmpty())
            return name;
        return map.getOrDefault(name, name);
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
            LOGGER.error(REFLECTION, "There was a problem getting field index {} from {}", classToAccess.getName(), e);
            throw new UnableToAccessFieldException(e);
        }
    }

    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String fieldName)
    {
        try
        {
            return (T)findField(classToAccess, remapName(fieldName)).get(instance);
        }
        catch (UnableToFindFieldException e)
        {
            LOGGER.error("Unable to locate field {} ({}) on type {}", fieldName, remapName(fieldName), classToAccess.getName(), e);
            throw e;
        }
        catch (IllegalAccessException e)
        {
            LOGGER.error("Unable to access field {} ({}) on type {}", fieldName, remapName(fieldName), classToAccess.getName(), e);
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
            LOGGER.error("There was a problem setting field index {} on type {}", classToAccess.getName(), e);
            throw new UnableToAccessFieldException(e);
        }
    }

    public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String fieldName)
    {
        try
        {
            findField(classToAccess, remapName(fieldName)).set(instance, value);
        }
        catch (UnableToFindFieldException e)
        {
            LOGGER.error("Unable to locate any field {} on type {}", classToAccess.getName(), e);
            throw e;
        }
        catch (IllegalAccessException e)
        {
            LOGGER.error("Unable to set any field {} on type {}", classToAccess.getName(), e);
            throw new UnableToAccessFieldException(e);
        }
    }

    /**
     * Finds a method with the specified name and parameters in the given class and makes it accessible.
     * Note: for performance, store the returned value and avoid calling this repeatedly.
     * <p>
     * Throws an exception if the method is not found.
     *
     * @param clazz          The class to find the method on.
     * @param methodName     The MCP (obfuscated) name of the method to find(e.g. "func_72820_D").
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
            Method m = clazz.getDeclaredMethod(remapName(methodName), parameterTypes);
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

    private static void loadMappings()
    {
        if (loaded)
            return;

        synchronized(map) //Just in case?
        {
            if (loaded) //Incase something else loaded while we were here, jump out
                return;
            for (String file  : new String[]{"fields.csv", "methods.csv"})
            {
                URL path = ClassLoader.getSystemResource(file); //We EXPLICITLY go throught the SystemClassLoader here because this is dev-time only. And will be on the root classpath.
                if (path == null)
                    continue;

                int count = map.size();
                LOGGER.info(REFLECTION, "Loading Mappings: {}", path);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(path.openStream())))
                {
                    reader.lines().skip(1).map(e -> e.split(",")).forEach(e -> map.put(e[0], e[1]));
                }
                catch (IOException e1)
                {
                    LOGGER.error(REFLECTION, "Error reading mappings", e1);
                }
                LOGGER.info(REFLECTION, "Loaded {} entries", map.size() - count);
            }
            loaded = true;
        }
    }

    //Add SRG names to these exception?
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
