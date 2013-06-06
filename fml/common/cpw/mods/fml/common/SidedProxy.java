/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cpw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SidedProxy
{
    /**
     * The name of the client side class to load and populate
     */
    String clientSide() default "";

    /**
     * The name of the server side class to load and populate
     */
    String serverSide() default "";

    @Deprecated
    /**
     * Not implemented
     * The name of a special bukkit plugin class to load and populate
     */
    String bukkitSide() default "";

    /**
     * The (optional) name of a mod to load this proxy for. This will help ensure correct behaviour when loading a combined
     * scala/java mod package. It is almost never going to be required, unless you ship both Scala and Java {@link Mod} content
     * in a single jar.
     */
    String modId() default "";
}
