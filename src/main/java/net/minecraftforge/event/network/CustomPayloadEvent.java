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
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.network.ForgePayload;
import net.minecraftforge.common.util.LogicalSidedProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * This is fired when a CustomPayload packet is received. It will first be offered to any Channels registered for its name.
 * If they do not handle it, then it is fired on its BUS.
 */
public record CustomPayloadEvent(
    ResourceLocation getChannel,
    Object getPayloadObject,
    @Nullable FriendlyByteBuf getPayload,
    Context getSource,
    int getLoginIndex
) implements RecordEvent {
    public static final EventBus<CustomPayloadEvent> BUS = EventBus.create(CustomPayloadEvent.class);

    public CustomPayloadEvent(ResourceLocation channel, Object payload, Context source, int loginIndex) {
        this(channel, payload, payload instanceof ForgePayload forge ? forge.data() : null, source, loginIndex);
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
