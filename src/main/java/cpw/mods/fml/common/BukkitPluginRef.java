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
 * Declare a variable to be populated by a Bukkit Plugin proxy instance if the bukkit coremod
 * is available. It can only be applied to field typed as {@link BukkitProxy}
 * Generally it should be used in conjunction with {@link Mod#bukkitPlugin()} specifying the
 * plugin to load.
 *
 * @author cpw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BukkitPluginRef
{
    /**
     * A reference (possibly version specific) to a Bukkit Plugin by name, using the name@versionbound
     * specification. If this is a bukkit enabled environment the field annotated by this
     * will be populated with a {@link BukkitProxy} instance if possible. This proxy will be gotten by
     * reflectively calling the "getModProxy" method on the bukkit plugin instance.
     * @return The name of the plugin which we will inject into this field
     */
    String value();
}