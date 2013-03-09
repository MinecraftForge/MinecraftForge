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

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NetworkMod
{
    /**
     * Does this mod require the client side to be present when installed on a server?
     */
    boolean clientSideRequired() default false;
    /**
     * Does this mod require the server side to be present when installed on a client?
     */
    boolean serverSideRequired() default false;
    /**
     * A list of Packet250 network channels to register for this mod - these channels
     * will be universal and will require a universal packethandler to handle them
     */
    String[] channels() default {};
    /**
     * An optional range check for client to server communication version compatibility
     */
    String versionBounds() default "";

    /**
     * A packet handler implementation for channels registered through this annotation
     * - this packet handler will be universal and handle both client and server
     * requests.
     */
    Class<? extends IPacketHandler> packetHandler() default NULL.class;

    /**
     * A tiny packet handler implementation based on {@link net.minecraft.network.packet.Packet131MapData} for "small"
     * data packet loads.
     */
    Class<? extends ITinyPacketHandler> tinyPacketHandler() default NULL.class;
    /**
     * A connection handler implementation for this network mod
     */
    Class<? extends IConnectionHandler> connectionHandler() default NULL.class;
    /**
     * A packet handler and channels to register for the client side
     */
    SidedPacketHandler clientPacketHandlerSpec() default @SidedPacketHandler(channels = {}, packetHandler = NULL.class );

    /**
     * A packet handler and channels to register for the server side
     */
    SidedPacketHandler serverPacketHandlerSpec() default @SidedPacketHandler(channels = {}, packetHandler = NULL.class );

    /**
     * Special dummy class for handling stupid annotation default values
     * @author cpw
     *
     */
    static interface NULL extends IPacketHandler, IConnectionHandler, ITinyPacketHandler {};

    /**
     * A marker for a method that will be offered the client's version string
     * if more sophisticated version rejection handling is required:
     * The method should accept a "String", a "NetworkManager" and return a boolean true
     * if the version can be accepted.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface VersionCheckHandler { }

    /**
     * Bundles together a packet handler and it's associated channels for the sided packet handlers
     * @author cpw
     *
     */
    public @interface SidedPacketHandler {
        String[] channels();
        Class<? extends IPacketHandler> packetHandler();
    }

}
