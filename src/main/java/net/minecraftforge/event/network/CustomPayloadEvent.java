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
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.ForgePayload;
import net.minecraftforge.common.util.LogicalSidedProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

// TODO make this not an event, and instead move it to a callback in the Channel itself.
// But also expose it as a generic listener event for anyone who cares about it but is outside out channel control system.
public class CustomPayloadEvent extends Event {
    private final ResourceLocation channel;
    private final Object payload;
    private final FriendlyByteBuf data;
    private final Context source;
    private final int loginIndex;

    public CustomPayloadEvent(ResourceLocation channel, Object payload, Context source, int loginIndex) {
        this.channel = channel;
        this.payload = payload;
        this.data = payload instanceof ForgePayload forge ? forge.data() : null;
        this.source = source;
        this.loginIndex = loginIndex;
    }

    public ResourceLocation getChannel() {
        return this.channel;
    }

    @Nullable
    public FriendlyByteBuf getPayload() {
        return data;
    }

    public Object getPayloadObject() {
        return this.payload;
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
        private final boolean client;

        private boolean packetHandled;

        public Context(Connection connection) {
            this.connection = connection;
            this.client = connection.getReceiving() == PacketFlow.CLIENTBOUND;
        }

        public boolean isClientSide() {
            return client;
        }

        public boolean isServerSide() {
            return !isClientSide();
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
            var executor = LogicalSidedProvider.WORKQUEUE.get(isClientSide());

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
