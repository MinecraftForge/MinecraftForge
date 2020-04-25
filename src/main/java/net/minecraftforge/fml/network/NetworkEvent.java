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

package net.minecraftforge.fml.network;

import javax.annotation.Nullable;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class NetworkEvent extends Event
{
    private final PacketBuffer payload;
    private final Supplier<Context> source;
    private final int loginIndex;

    private NetworkEvent(final ICustomPacket<?> payload, final Supplier<Context> source)
    {
        this.payload = payload.getInternalData();
        this.source = source;
        this.loginIndex = payload.getIndex();
    }

    private NetworkEvent(final PacketBuffer payload, final Supplier<Context> source, final int loginIndex)
    {
        this.payload = payload;
        this.source = source;
        this.loginIndex = loginIndex;
    }

    public NetworkEvent(final Supplier<Context> source) {
        this.source = source;
        this.payload = null;
        this.loginIndex = -1;
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
        private final boolean isLocal;

        public GatherLoginPayloadsEvent(final List<NetworkRegistry.LoginPayload> loginPayloadList, boolean isLocal) {
            this.collected = loginPayloadList;
            this.isLocal = isLocal;
        }

        public void add(PacketBuffer buffer, ResourceLocation channelName, String context) {
            collected.add(new NetworkRegistry.LoginPayload(buffer, channelName, context));
        }

        public boolean isLocal() {
            return isLocal;
        }
    }

    public static class LoginPayloadEvent extends NetworkEvent {
        LoginPayloadEvent(final PacketBuffer payload, final Supplier<Context> source, final int loginIndex) {
            super(payload, source, loginIndex);
        }
    }

    public enum RegistrationChangeType {
        REGISTER, UNREGISTER;
    }

    /**
     * Fired when the channel registration (see minecraft custom channel documentation) changes. Note the payload
     * is not exposed. This fires to the resource location that owns the channel, when it's registration changes state.
     *
     * It seems plausible that this will fire multiple times for the same state, depending on what the server is doing.
     * It just directly dispatches upon receipt.
     */
    public static class ChannelRegistrationChangeEvent extends NetworkEvent {
        private final RegistrationChangeType changeType;

        ChannelRegistrationChangeEvent(final Supplier<Context> source, RegistrationChangeType changeType) {
            super(source);
            this.changeType = changeType;
        }

        public RegistrationChangeType getRegistrationChangeType() {
            return this.changeType;
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
        private final NetworkDirection networkDirection;

        /**
         * The packet dispatcher for this event. Sends back to the origin.
         */
        private final PacketDispatcher packetDispatcher;
        private boolean packetHandled;

        Context(NetworkManager netHandler, NetworkDirection networkDirection, int index)
        {
            this(netHandler, networkDirection, new PacketDispatcher.NetworkManagerDispatcher(netHandler, index, networkDirection.reply()::buildPacket));
        }

        Context(NetworkManager networkManager, NetworkDirection networkDirection, PacketDispatcher dispatcher) {
            this.networkManager = networkManager;
            this.networkDirection = networkDirection;
            this.packetDispatcher = dispatcher;
        }

        public NetworkDirection getDirection() {
            return networkDirection;
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

        public CompletableFuture<Void> enqueueWork(Runnable runnable) {
            ThreadTaskExecutor<?> executor = LogicalSidedProvider.WORKQUEUE.get(getDirection().getReceptionSide());
            // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
            // Same logic as ThreadTaskExecutor#runImmediately without the join
            if (!executor.isOnExecutionThread()) {
                return executor.deferTask(runnable); // Use the internal method so thread check isn't done twice
            } else {
                runnable.run();
                return CompletableFuture.completedFuture(null);
            }
        }

        /**
         * When available, gets the sender for packets that are sent from a client to the server.
         */
        @Nullable
        public ServerPlayerEntity getSender()
        {
            INetHandler netHandler = networkManager.getNetHandler();
            if (netHandler instanceof ServerPlayNetHandler)
            {
                ServerPlayNetHandler netHandlerPlayServer = (ServerPlayNetHandler) netHandler;
                return netHandlerPlayServer.player;
            }
            return null;
        }

        public NetworkManager getNetworkManager() {
            return networkManager;
        }
    }
}
