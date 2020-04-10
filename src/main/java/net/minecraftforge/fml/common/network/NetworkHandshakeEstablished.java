/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.network;

import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This message is sent through all channels affected by a currently occurring handshake. It is guaranteed to
 * be able to send a custom payload packet, however, interaction with minecraft and world state is NOT assured
 * as it is likely this is fired on a netty handler thread, not a world processing thread.
 *
 * If you wish to send an outbound message through your channel, bind the {@link FMLOutboundHandler#FML_MESSAGETARGET}
 * property of your channel to the supplied dispatcher.
 * @author cpw
 *
 */
public class NetworkHandshakeEstablished {
    public final NetworkDispatcher dispatcher;
    public final Side side;
    public final INetHandler netHandler;

    public NetworkHandshakeEstablished(NetworkDispatcher dispatcher, INetHandler netHandler, Side origin)
    {
        this.netHandler = netHandler;
        this.dispatcher = dispatcher;
        this.side = origin;
    }
}
