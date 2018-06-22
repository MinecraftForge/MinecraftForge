/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Supplier;

public class NetworkEvent extends Event
{
    private final PacketBuffer payload;
    private final Supplier<Context> source;

    private NetworkEvent(PacketBuffer payload, Supplier<Context> source)
    {
        this.payload = payload;
        this.source = source;
    }

    public PacketBuffer getPayload()
    {
        return payload;
    }

    public Supplier<Context> getSource()
    {
        return source;
    }

    public static class ServerCustomPayloadEvent extends NetworkEvent
    {
        ServerCustomPayloadEvent(final PacketBuffer payload, final Supplier<Context> source) {
            super(payload, source);
        }
    }
    public static class ClientCustomPayloadEvent extends NetworkEvent
    {
        ClientCustomPayloadEvent(final PacketBuffer payload, final Supplier<Context> source) {
            super(payload, source);
        }
    }

    /**
     * Context for {@link NetworkEvent}
     */
    public static class Context
    {
        /**
         * The {@link INetHandler} for this message. It could be a client or server handler, depending
         * on the {@link #side} received.
         */
        private final INetHandler netHandler;

        /**
         * The {@link Network} this message has been received on
         */
        private final Network side;

        Context(NetworkManager netHandler, Network side)
        {
            this.netHandler = netHandler.getNetHandler();
            this.side = side;
        }

        public Network getSide() {
            return side;
        }

        public NetHandlerPlayServer getServerHandler()
        {
            return (NetHandlerPlayServer) netHandler;
        }

        public NetHandlerPlayClient getClientHandler()
        {
            return (NetHandlerPlayClient) netHandler;
        }
    }
}
