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

package net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sided proxies are loaded based on the specific environment they find themselves loaded into.
 * They are used to ensure that client-specific code (such as GUIs) is only loaded into the game
 * on the client side.
 * It is applied to static fields of a class, anywhere in your mod code. FML will scan
 * and load any classes with this annotation at mod construction time.
 *
 * <p>
 * This example will load a CommonProxy on the server side, and a ClientProxy on the client side.
 *
 * <pre>{@code
 *  public class MySidedProxyHolder {
 *      {@literal @}SidedProxy(modId="MyModId",clientSide="mymod.ClientProxy", serverSide="mymod.CommonProxy")
 *      public static CommonProxy proxy;
 *  }
 *
 *  public class CommonProxy {
 *      // Common or server stuff here that needs to be overridden on the client
 *  }
 *
 *  public class ClientProxy extends CommonProxy {
 *      // Override common stuff with client specific stuff here
 *  }
 * }
 * </pre>
 * @author cpw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SidedProxy
{
    /**
     * The full name of the client side class to load and populate.
     * Defaults to the nested class named "ClientProxy" in the current class.
     */
    String clientSide() default "";

    /**
     * The full name of the server side class to load and populate.
     * Defaults to the nested class named "ServerProxy" in the current class.
     */
    String serverSide() default "";

    /**
     * The (optional) name of a mod to load this proxy for. This will help ensure correct behaviour when loading a combined
     * scala/java mod package. It is almost never going to be required, unless you ship both Scala and Java {@link Mod} content
     * in a single jar.
     */
    String modId() default "";
}
