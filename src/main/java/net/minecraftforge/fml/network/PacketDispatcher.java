/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Dispatcher for sending packets in response to a received packet. Abstracts out the difference between wrapped packets
 * and unwrapped packets.
 */
public class PacketDispatcher {
    BiConsumer<ResourceLocation, PacketBuffer> packetSink;

    PacketDispatcher(final BiConsumer<ResourceLocation, PacketBuffer> packetSink) {
        this.packetSink = packetSink;
    }

    private PacketDispatcher() {

    }

    public void sendPacket(ResourceLocation resourceLocation, PacketBuffer buffer) {
        packetSink.accept(resourceLocation, buffer);
    }

    static class NetworkManagerDispatcher extends PacketDispatcher {
        private final NetworkManager manager;
        private final int packetIndex;
        private final BiFunction<Pair<PacketBuffer, Integer>, ResourceLocation, ICustomPacket<?>> customPacketSupplier;

        NetworkManagerDispatcher(NetworkManager manager, int packetIndex, BiFunction<Pair<PacketBuffer, Integer>, ResourceLocation, ICustomPacket<?>> customPacketSupplier) {
            super();
            this.packetSink = this::dispatchPacket;
            this.manager = manager;
            this.packetIndex = packetIndex;
            this.customPacketSupplier = customPacketSupplier;
        }

        private void dispatchPacket(final ResourceLocation resourceLocation, final PacketBuffer buffer) {
            final ICustomPacket<?> packet = this.customPacketSupplier.apply(Pair.of(buffer, packetIndex), resourceLocation);
            this.manager.sendPacket(packet.getThis());
        }
    }
}
