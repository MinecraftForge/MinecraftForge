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

package cpw.mods.fml.common.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cpw.mods.fml.common.Mod;

/**
 * {@link Mod} classes also adorned with this annotation are considered network capable mods.
 * They require a {@link IPacketHandlerFactory} that will generate instances of packet handlers.
 * They are version checked for compatibility between client and server. The default mechanism
 * matches the {@link Mod#version()} on client and server. It can be overridden with either a
 * {@link #versionBounds()} or a more complex {@link VersionCheckHandler}.
 *
 * @author cpw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NetworkMod
{
    /**
     * A packet handler implementation for channels registered through this annotation
     * - this packet handler will be universal and handle both client and server
     * requests.
     */
    Class<? extends IPacketHandlerFactory> packetHandlerFactory();
    /**
     * An optional range check for client to server communication version compatibility.
     */
    String versionBounds() default "";
    /**
     * A marker for a method that will be offered the client's version string
     * if more sophisticated version rejection handling is required:
     * The method should accept a "String" (the version) and return a boolean true
     * if the version can be accepted.
     * It can only be applied to the {@link NetworkMod} annotated class.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface VersionCheckHandler { }
}
