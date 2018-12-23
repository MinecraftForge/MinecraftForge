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

package net.minecraftforge.fml.network;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.ListenableFuture;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSidedProvider;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NetworkEvent extends Event
{
    private final PacketBuffer payload;
    private final Supplier<Context> source;
    private final int loginIndex;

    private NetworkEvent(final ICustomPacket<?> payload, final Supplier<Context> source)
    {
        this.payload = payload.getData();
        this.source = source;
        this.loginIndex = payload.getIndex();
    }

    private NetworkEvent(final PacketBuffer payload, final Supplier<Context> source, final int loginIndex)
    {
        this.payload = payload;
        this.source = source;
        this.loginIndex = loginIndex;
    }

    public PacketBuffer getPayload()
    {
        return payload;
    }

    public Supplier<Context> getSource()
    {
        return source;
    }

    public int getLoginIndex()
    {
        return loginIndex;
    }

    public static class ServerCustomPayloadEvent extends NetworkEvent
    {
        ServerCustomPayloadEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
            super(payload, source);
        }
    }
    public static class ClientCustomPayloadEvent extends NetworkEvent
    {
        ClientCustomPayloadEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
            super(payload, source);
        }
    }
    public static class ServerCustomPayloadLoginEvent extends ServerCustomPayloadEvent {
        ServerCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source)
        {
            super(payload, source);
        }
    }

    public static class ClientCustomPayloadLoginEvent extends ClientCustomPayloadEvent {
        ClientCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source)
        {
            super(payload, source);
        }
    }

    public static class GatherLoginPayloadsEvent extends Event {
        private final List<NetworkRegistry.LoginPayload> collected;

        public GatherLoginPayloadsEvent(final List<NetworkRegistry.LoginPayload> loginPayloadList) {
            this.collected = loginPayloadList;
        }

        public void add(PacketBuffer buffer, ResourceLocation channelName, String context) {
            collected.add(new NetworkRegistry.LoginPayload(buffer, channelName, context));
        }
    }

    public static class LoginPayloadEvent extends NetworkEvent {
        LoginPayloadEvent(final PacketBuffer payload, final Supplier<Context> source, final int loginIndex) {
            super(payload, source, loginIndex);
        }
    }
    /**
     * Context for {@link NetworkEvent}
     */
    public static class Context
    {
        /**
         * The {@link NetworkManager} for this message.
         */
        private final NetworkManager networkManager;

        /**
         * The {@link NetworkDirection} this message has been received on.
         */
        private final NetworkDirection side;

        /**
         * The packet dispatcher for this event. Sends back to the origin.
         */
        private final PacketDispatcher packetDispatcher;
        private boolean packetHandled;

        Context(NetworkManager netHandler, NetworkDirection side, int index)
        {
            this(netHandler, side, new PacketDispatcher.NetworkManagerDispatcher(netHandler, index, side.reply()::buildPacket));
        }

        Context(NetworkManager networkManager, NetworkDirection side, PacketDispatcher dispatcher) {
            this.networkManager = networkManager;
            this.side = side;
            this.packetDispatcher = dispatcher;
        }

        public NetworkDirection getDirection() {
            return side;
        }

        public PacketDispatcher getPacketDispatcher() {
            return packetDispatcher;
        }

        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return networkManager.channel().attr(key);
        }

        public void setPacketHandled(boolean packetHandled) {
            this.packetHandled = packetHandled;
        }

        public boolean getPacketHandled()
        {
            return packetHandled;
        }

        @SuppressWarnings("unchecked")
        public <V> ListenableFuture<V> enqueueWork(Runnable runnable) {
            return (ListenableFuture<V>)LogicalSidedProvider.WORKQUEUE.<IThreadListener>get(getDirection().getLogicalSide()).addScheduledTask(runnable);
        }

        /**
         * When available, gets the sender for packets that are sent from a client to the server.
         */
        @Nullable
        public EntityPlayerMP getSender()
        {
            INetHandler netHandler = networkManager.getNetHandler();
            if (netHandler instanceof NetHandlerPlayServer)
            {
                NetHandlerPlayServer netHandlerPlayServer = (NetHandlerPlayServer) netHandler;
                return netHandlerPlayServer.player;
            }
            return null;
        }

        NetworkManager getNetworkManager() {
            return networkManager;
        }
    }
}
