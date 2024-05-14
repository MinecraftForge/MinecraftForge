/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record NetworkDirection<B extends FriendlyByteBuf>(NetworkProtocol<B> protocol, PacketFlow direction) {
    public static final NetworkDirection<RegistryFriendlyByteBuf> PLAY_TO_CLIENT = new NetworkDirection<>(NetworkProtocol.PLAY, PacketFlow.CLIENTBOUND);
    public static final NetworkDirection<RegistryFriendlyByteBuf> PLAY_TO_SERVER = new NetworkDirection<>(NetworkProtocol.PLAY, PacketFlow.SERVERBOUND);
    public static final NetworkDirection<FriendlyByteBuf> LOGIN_TO_CLIENT = new NetworkDirection<>(NetworkProtocol.LOGIN, PacketFlow.CLIENTBOUND);
    public static final NetworkDirection<FriendlyByteBuf> LOGIN_TO_SERVER = new NetworkDirection<>(NetworkProtocol.LOGIN, PacketFlow.SERVERBOUND);
    public static final NetworkDirection<FriendlyByteBuf> CONFIGURATION_TO_CLIENT = new NetworkDirection<>(NetworkProtocol.CONFIGURATION, PacketFlow.CLIENTBOUND);
    public static final NetworkDirection<FriendlyByteBuf> CONFIGURATION_TO_SERVER = new NetworkDirection<>(NetworkProtocol.CONFIGURATION, PacketFlow.SERVERBOUND);

    public <T extends PacketListener, MSG> Packet<T> buildPacket(Channel<MSG> channel, MSG packet) {
        return this.protocol().buildPacket(this.direction(), channel, packet);
    }
}
