/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.network;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.common.util.LogicalSidedProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

// TODO make this not an event, and instead move it to a callback in the Channel itself.
// But also expose it as a generic listener event for anyone who cares about it but is outside out channel control system.
public class CustomPayloadEvent extends Event {
    private final ResourceLocation channel;
    private final FriendlyByteBuf payload;
    private final Context source;
    private final int loginIndex;

    public CustomPayloadEvent(ResourceLocation channel, FriendlyByteBuf payload, Context source, int loginIndex) {
        this.channel = channel;
        this.payload = payload;
        this.source = source;
        this.loginIndex = loginIndex;
    }

    public CustomPayloadEvent(ICustomPacket<?> payload, Context source) {
        this(payload.getName(), payload.getInternalData(), source, payload.getIndex());
    }

    public ResourceLocation getChannel() {
        return this.channel;
    }

    public FriendlyByteBuf getPayload() {
        return payload;
    }

    public int getLoginIndex() {
        return loginIndex;
    }

    public Context getSource() {
        return source;
    }

    /**
     * Context for {@link CustomPayloadEvent}
     */
    public static class Context {
        /**
         * The {@link Connection} for this message.
         */
        private final Connection connection;

        /**
         * The {@link NetworkDirection} this message has been received on.
         */
        private final NetworkDirection networkDirection;

        private boolean packetHandled;

        public Context(Connection connection, NetworkDirection networkDirection) {
            this.connection = connection;
            this.networkDirection = networkDirection;
        }

        public boolean isClientSide() {
            return getConnection().getReceiving() == PacketFlow.CLIENTBOUND;
        }

        public boolean isServerSide() {
            return !isClientSide();
        }

        public NetworkDirection getDirection() {
            return networkDirection;
        }

        public Connection getConnection() {
            return connection;
        }

        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return connection.channel().attr(key);
        }

        public void setPacketHandled(boolean packetHandled) {
            this.packetHandled = packetHandled;
        }

        public boolean getPacketHandled() {
            return packetHandled;
        }

        public CompletableFuture<Void> enqueueWork(Runnable runnable) {
            BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(getDirection().getReceptionSide());
            // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
            // Same logic as ThreadTaskExecutor#runImmediately without the join
            if (!executor.isSameThread())
                return executor.submitAsync(runnable); // Use the internal method so thread check isn't done twice
            else {
                runnable.run();
                return CompletableFuture.completedFuture(null);
            }
        }

        /**
         * When available, gets the sender for packets that are sent from a client to the server.
         */
        @Nullable
        public ServerPlayer getSender() {
            PacketListener netHandler = connection.getPacketListener();
            if (netHandler instanceof ServerGamePacketListenerImpl handler)
                return handler.player;
            return null;
        }
    }
}
