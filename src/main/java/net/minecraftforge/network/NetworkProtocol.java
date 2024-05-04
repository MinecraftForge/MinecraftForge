/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;

import java.util.function.Consumer;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.DiscardedQueryAnswerPayload;
import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;

public class NetworkProtocol<B extends FriendlyByteBuf> {
    public static final NetworkProtocol<RegistryFriendlyByteBuf> PLAY = new NetworkProtocol<>(ConnectionProtocol.PLAY);
    public static final NetworkProtocol<FriendlyByteBuf> LOGIN = new NetworkProtocol<>(ConnectionProtocol.LOGIN);
    public static final NetworkProtocol<FriendlyByteBuf> CONFIG = new NetworkProtocol<>(ConnectionProtocol.CONFIGURATION);

    private final ConnectionProtocol protocol;

    private NetworkProtocol(ConnectionProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return this.protocol.name();
    }

    public ConnectionProtocol toVanilla() {
        return this.protocol;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet<?>, MSG> ICustomPacket<T> buildPacket(PacketFlow direction, Channel<MSG> channel, MSG packet) {
        var name = channel.getName();
        var encoder = (Consumer<FriendlyByteBuf>)(buf -> channel.encode(buf, packet));

        switch (this.protocol) {
            case PLAY, CONFIGURATION:
                if (direction == PacketFlow.CLIENTBOUND)
                    return (ICustomPacket<T>)new ClientboundCustomPayloadPacket(new DiscardedPayload(name, null, encoder));
                else
                    return (ICustomPacket<T>)new ServerboundCustomPayloadPacket(new DiscardedPayload(name, null, encoder));

            case LOGIN:
                if (direction == PacketFlow.CLIENTBOUND)
                    return (ICustomPacket<T>)new ClientboundCustomQueryPacket(0, new DiscardedQueryPayload(name, null, encoder));
                else
                    return (ICustomPacket<T>)new ServerboundCustomQueryAnswerPacket(0, new DiscardedQueryAnswerPayload(null, encoder));

            default:
                throw new IllegalArgumentException("Invalid protocol, shouldn't be possible as this is a private class..");
        }
    }
}
