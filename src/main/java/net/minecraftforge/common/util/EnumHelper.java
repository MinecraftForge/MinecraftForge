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

package net.minecraftforge.common.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiPredicate;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.EnhancedRuntimeException;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

public class EnumHelper
{
     /*
     * Everything below this is found at the site below, and updated to be able to compile in Eclipse/Java 1.6+
     * Also modified for use in decompiled code.
     * Found at: http://niceideas.ch/roller2/badtrash/entry/java_create_enum_instances_dynamically
     */

    private static < T extends Enum<? >> T makeEnum(Class<T> enumClass, @Nullable String value, int ordinal, Class<?>[] additionalTypes, @Nullable Object[] additionalValues) throws Exception
    {
        //new Enum(name, values.size() + 1, params)
    }

    private static void blankField(Class<?> enumClass, String fieldName) throws Exception
    {
        for (Field field : Class.class.getDeclaredFields())
        {
            if (field.getName().contains(fieldName))
            {
                field.setAccessible(true);
                field.set(enumClass, null);
                break;
            }
        }
    }

    //Class holds caches of the enum constants used for Enum.valueOf() and reflection magic. So we need to invalidate those caches when we add something new. They will automtically be re-built.
    public static void cleanEnumCache(Class<?> enumClass) throws Exception
    {
        blankField(enumClass, "enumConstantDirectory");
        blankField(enumClass, "enumConstants");
    }

    @SuppressWarnings({ "unchecked", "serial" })
    @Nullable
    private static <T extends Enum<?> & IExpandableEnum<?>> T addEnum(boolean test, final Class<T> enumType, T newValue)
    {
        try
        {
            T[] previousValues = enumType.getEnumConstants();
            T newValue = makeEnum(enumType, enumName, previousValues.length, paramTypes, paramValues);
            newValue.__set_values__(ArrayUtils.add(previousValues, newValue));
            cleanEnumCache(enumType);

            return newValue;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
