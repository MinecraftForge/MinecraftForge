/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy.network;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Dispatcher for sending packets in response to a received packet. Abstracts out the difference between wrapped packets
 * and unwrapped packets.
 */
public class PacketDispatcher {
    BiConsumer<ResourceLocation, FriendlyByteBuf> packetSink;

    PacketDispatcher(final BiConsumer<ResourceLocation, FriendlyByteBuf> packetSink) {
        this.packetSink = packetSink;
    }

    private PacketDispatcher() {

    }

    public void sendPacket(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
        packetSink.accept(resourceLocation, buffer);
    }

    static class NetworkManagerDispatcher extends PacketDispatcher {
        private final Connection manager;
        private final int packetIndex;
        private final BiFunction<Pair<FriendlyByteBuf, Integer>, ResourceLocation, ICustomPacket<?>> customPacketSupplier;

        NetworkManagerDispatcher(Connection manager, int packetIndex, BiFunction<Pair<FriendlyByteBuf, Integer>, ResourceLocation, ICustomPacket<?>> customPacketSupplier) {
            super();
            this.packetSink = this::dispatchPacket;
            this.manager = manager;
            this.packetIndex = packetIndex;
            this.customPacketSupplier = customPacketSupplier;
        }

        private void dispatchPacket(final ResourceLocation resourceLocation, final FriendlyByteBuf buffer) {
            final ICustomPacket<?> packet = this.customPacketSupplier.apply(Pair.of(buffer, packetIndex), resourceLocation);
            this.manager.send(packet.getThis());
        }
    }
}
