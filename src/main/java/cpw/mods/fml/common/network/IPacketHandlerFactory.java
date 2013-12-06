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

import cpw.mods.fml.common.network.NetworkSide.ClientSide;
import cpw.mods.fml.common.network.NetworkSide.ServerSide;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;

/**
 * Whenever a network connection is constructed, the appropriate factory method is called, once for each channel.
 * Return null if you don't want a handler for a specific channel on a specific side.
 *
 * @author cpw
 *
 */
public interface IPacketHandlerFactory
{
    /**
     * Return the list of channels this packet handler factory wants to claim
     * @return a list of channels
     */
    public String[] channels();
    /**
     * Return a client side packet handler for the specified channel or null if it is not handled on this side
     *
     * @param mod
     * @param manager
     * @param clientPlayHandler
     * @param channel
     * @return
     */
    public IPacketHandler<ClientSide> makeClientPacketHandler(NetworkManager manager, INetHandler clientPlayHandler, String channel);
    /**
     * Return a server side packet handler for the specified channel or null if it is not handled on this side
     * @param mod
     * @param manager
     * @param serverPlayHandler
     * @return
     */
    public IPacketHandler<ServerSide> makeServerPacketHandler(NetworkManager manager, INetHandler serverPlayHandler, String channel);
}
