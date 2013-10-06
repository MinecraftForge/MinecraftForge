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
package net.minecraft.src;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author cpw
 *
 */
@Retention(value = RUNTIME)
@Target(value = FIELD)
@Deprecated
public @interface MLProp
{
    /**
     * Adds additional help to top of configuration file.
     */
    @Deprecated
    String info() default "";

    /**
     * Maximum value allowed if field is a number.
     */
    @Deprecated
    double max() default Double.MAX_VALUE;

    /**
     * Minimum value allowed if field is a number.
     */
    @Deprecated
    double min() default Double.MIN_VALUE;

    /**
     * Overrides the field name for property key.
     */
    @Deprecated
    String name() default "";

}
