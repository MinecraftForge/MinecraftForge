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
