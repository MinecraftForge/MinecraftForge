/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import io.netty.util.AttributeKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.network.NetworkProtocol;
import net.minecraftforge.network.simple.handler.SimpleHandlerProtocol;
import net.minecraftforge.network.simple.handler.SimplePacket;

import java.util.function.Consumer;

public interface SimpleConnection<BASE> {
    /**
     * Creates a builder grouping together all packets under the same protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    <NEWBUF extends FriendlyByteBuf, NEWBASE> SimpleProtocol<NEWBUF, NEWBASE> protocol(NetworkProtocol<NEWBUF> protocol);

    /**
     * Creates a builder grouping together all packets under the same protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    <NEWBUF extends FriendlyByteBuf, CTX, NEWBASE extends SimplePacket<CTX>> SimpleHandlerProtocol<NEWBUF, NEWBASE> protocol(AttributeKey<CTX> context, NetworkProtocol<NEWBUF> protocol);

    /**
     * Consumer version of {@link #protocol(NetworkProtocol)}. The Consumer will immediately be called with the created protocol.
     */
    default <BUF extends FriendlyByteBuf> SimpleConnection<BASE> protocol(NetworkProtocol<BUF> protocol, Consumer<SimpleProtocol<BUF, BASE>> consumer) {
        var tmp = this.<BUF, BASE>protocol(protocol);
        consumer.accept(tmp);
        return this;
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Configuration protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<FriendlyByteBuf, BASE> configuration() {
        return protocol(NetworkProtocol.CONFIGURATION);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Configuration protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX>> SimpleHandlerProtocol<FriendlyByteBuf, NEWBASE> configuration(AttributeKey<CTX> context) {
        return protocol(context, NetworkProtocol.CONFIGURATION);
    }

    /**
     * Consumer version of {@link #configuration()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleConnection<BASE> configuration(Consumer<SimpleProtocol<FriendlyByteBuf, BASE>> consumer) {
        return protocol(NetworkProtocol.CONFIGURATION, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Login protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<FriendlyByteBuf, BASE> login() {
        return protocol(NetworkProtocol.LOGIN);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Login protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX>> SimpleHandlerProtocol<FriendlyByteBuf, NEWBASE> login(AttributeKey<CTX> context) {
        return protocol(context, NetworkProtocol.LOGIN);
    }

    /**
     * Consumer version of {@link #login()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleConnection<BASE> login(Consumer<SimpleProtocol<FriendlyByteBuf, BASE>> consumer) {
        return protocol(NetworkProtocol.LOGIN, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Play protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<RegistryFriendlyByteBuf, BASE> play() {
        return protocol(NetworkProtocol.PLAY);
    }

    /**
     * Consumer version of {@link #play()}. The Consumer will immediately be called with the created protocol.
     *
     * @param consumer
     * @return
     */
    default SimpleConnection<BASE> play(Consumer<SimpleProtocol<RegistryFriendlyByteBuf, BASE>> consumer) {
        return protocol(NetworkProtocol.PLAY, consumer);
    }
}
