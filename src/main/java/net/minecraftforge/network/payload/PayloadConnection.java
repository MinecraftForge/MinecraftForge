/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.payload;

import io.netty.util.AttributeKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.network.NetworkProtocol;
import net.minecraftforge.network.payload.handler.PayloadHandlerProtocol;
import net.minecraftforge.network.simple.handler.SimplePacket;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

public interface PayloadConnection<BASE extends CustomPacketPayload> {
    /**
     * Creates a builder grouping together all packets under the same protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    <NEWBUF extends FriendlyByteBuf, NEWBASE extends CustomPacketPayload> PayloadProtocol<NEWBUF, NEWBASE> protocol(@Nullable NetworkProtocol<NEWBUF> protocol);

    /**
     * Creates a builder grouping together all packets under the same protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    <NEWBUF extends FriendlyByteBuf, CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadHandlerProtocol<NEWBUF, NEWBASE> protocol(AttributeKey<CTX> context, @Nullable NetworkProtocol<NEWBUF> protocol);

    /**
     * Consumer version of {@link #protocol(NetworkProtocol)}. The Consumer will immediately be called with the created protocol.
     */
    default <BUF extends FriendlyByteBuf> PayloadConnection<BASE> protocol(@Nullable NetworkProtocol<BUF> protocol, Consumer<PayloadProtocol<BUF, BASE>> consumer) {
        var tmp = this.<BUF, BASE>protocol(protocol);
        consumer.accept(tmp);
        return this;
    }

    /**
     * Consumer version of {@link #protocol(AttributeKey,NetworkProtocol)}. The Consumer will immediately be called with the created protocol.
     */
    default <BUF extends FriendlyByteBuf, CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadConnection<BASE> protocol(AttributeKey<CTX> context, @Nullable NetworkProtocol<BUF> protocol, Consumer<PayloadHandlerProtocol<BUF, NEWBASE>> consumer) {
        var tmp = this.<BUF, CTX, NEWBASE>protocol(context, protocol);
        consumer.accept(tmp);
        return this;
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Configuration protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default PayloadProtocol<FriendlyByteBuf, BASE> configuration() {
        return protocol(NetworkProtocol.CONFIGURATION);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Configuration protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadHandlerProtocol<FriendlyByteBuf, NEWBASE> configuration(AttributeKey<CTX> context) {
        return protocol(context, NetworkProtocol.CONFIGURATION);
    }

    /**
     * Consumer version of {@link #configuration()}. The Consumer will immediately be called with the created protocol.
     */
    default PayloadConnection<BASE> configuration(Consumer<PayloadProtocol<FriendlyByteBuf, BASE>> consumer) {
        return protocol(NetworkProtocol.CONFIGURATION, consumer);
    }

    /**
     * Consumer version of {@link #configuration(AttributeKey)}. The Consumer will immediately be called with the created protocol.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadConnection<BASE> configuration(AttributeKey<CTX> context, Consumer<PayloadHandlerProtocol<FriendlyByteBuf, NEWBASE>> consumer) {
        return protocol(context, NetworkProtocol.CONFIGURATION, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Login protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default PayloadProtocol<FriendlyByteBuf, BASE> login() {
        return protocol(NetworkProtocol.LOGIN);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Login protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadHandlerProtocol<FriendlyByteBuf, NEWBASE> login(AttributeKey<CTX> context) {
        return protocol(context, NetworkProtocol.LOGIN);
    }

    /**
     * Consumer version of {@link #login()}. The Consumer will immediately be called with the created protocol.
     */
    default PayloadConnection<BASE> login(Consumer<PayloadProtocol<FriendlyByteBuf, BASE>> consumer) {
        return protocol(NetworkProtocol.LOGIN, consumer);
    }

    /**
     * Consumer version of {@link #login(AttributeKey)}. The Consumer will immediately be called with the created protocol.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadConnection<BASE> login(AttributeKey<CTX> context, Consumer<PayloadHandlerProtocol<FriendlyByteBuf, NEWBASE>> consumer) {
        return protocol(context, NetworkProtocol.LOGIN, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Play protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default PayloadProtocol<RegistryFriendlyByteBuf, BASE> play() {
        return protocol(NetworkProtocol.PLAY);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Login protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadHandlerProtocol<RegistryFriendlyByteBuf, NEWBASE> play(AttributeKey<CTX> context) {
        return protocol(context, NetworkProtocol.PLAY);
    }

    /**
     * Consumer version of {@link #play()}. The Consumer will immediately be called with the created protocol.
     */
    default PayloadConnection<BASE> play(Consumer<PayloadProtocol<RegistryFriendlyByteBuf, BASE>> consumer) {
        return protocol(NetworkProtocol.PLAY, consumer);
    }

    /**
     * Consumer version of {@link #play(AttributeKey)}. The Consumer will immediately be called with the created protocol.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadConnection<BASE> play(AttributeKey<CTX> context, Consumer<PayloadHandlerProtocol<RegistryFriendlyByteBuf, NEWBASE>> consumer) {
        return protocol(context, NetworkProtocol.PLAY, consumer);
    }

    /**
     * Creates a builder grouping together packets that are valid under any protocol.
     * It is not recommended to do this, instead you should use one of the other methods in this class to make sure your packets have basic validation.
     */
    default PayloadProtocol<FriendlyByteBuf, BASE> any() {
        return protocol(null);
    }

    /**
     * Creates a builder grouping together packets that are valid under any protocol.
     * It is not recommended to do this, instead you should use one of the other methods in this class to make sure your packets have basic validation.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadHandlerProtocol<FriendlyByteBuf, NEWBASE> any(AttributeKey<CTX> context) {
        return protocol(context, null);
    }

    /**
     * Consumer version of {@link #any()}. The Consumer will immediately be called with the created protocol.
     * It is not recommended to do this, instead you should use one of the other methods in this class to make sure your packets have basic validation.
     */
    default PayloadConnection<BASE> any(Consumer<PayloadProtocol<FriendlyByteBuf, BASE>> consumer) {
        return protocol(null, consumer);
    }

    /**
     * Consumer version of {@link #any(AttributeKey)}. The Consumer will immediately be called with the created protocol.
     * It is not recommended to do this, instead you should use one of the other methods in this class to make sure your packets have basic validation.
     */
    default <CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadConnection<BASE> any(AttributeKey<CTX> context, Consumer<PayloadHandlerProtocol<FriendlyByteBuf, NEWBASE>> consumer) {
        return protocol(context, null, consumer);
    }
}
