/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.Nullable;

public interface SimpleProtocol<BUF extends FriendlyByteBuf, BASE> extends SimpleConnection<Object> {
    /**
     * Creates a builder that validates both current protocol, and packet sending direction.
     * @param flow The direction that following packets are valid for. Null for bidirectional
     */
    SimpleFlow<BUF, BASE> flow(@Nullable PacketFlow flow);

    /**
     * Consumer version of {@link #flow(PacketFlow)}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleProtocol<BUF, BASE> flow(@Nullable PacketFlow flow, Consumer<SimpleFlow<BUF, BASE>> consumer) {
        consumer.accept(flow(flow));
        return this;
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Server to Client
     */
    default SimpleFlow<BUF, BASE> clientbound() {
        return flow(PacketFlow.CLIENTBOUND);
    }

    /**
     * Consumer version of {@link #clientbound()}. The Consumer will immediately be called with the created flow.
     */
    default SimpleProtocol<BUF, BASE> clientbound(Consumer<SimpleFlow<BUF, BASE>> consumer) {
        return flow(PacketFlow.CLIENTBOUND, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Server to Client
     */
    default <MSG extends BASE> SimpleProtocol<BUF, BASE> clientbound(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler) {
        var flow = flow(PacketFlow.CLIENTBOUND);
        flow.add(type, codec, handler);
        return this;
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Client to Server
     */
    default SimpleFlow<BUF, BASE> serverbound() {
        return flow(PacketFlow.SERVERBOUND);
    }

    /**
     * Consumer version of {@link #serverbound()}. The Consumer will immediately be called with the created flow.
     */
    default SimpleProtocol<BUF, BASE> serverbound(Consumer<SimpleFlow<BUF, BASE>> consumer) {
        return flow(PacketFlow.SERVERBOUND, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Client to Server
     */
    default <MSG extends BASE> SimpleProtocol<BUF, BASE> serverbound(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler) {
        var flow = flow(PacketFlow.SERVERBOUND);
        flow.add(type, codec, handler);
        return this;
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from either flow
     */
    default SimpleFlow<BUF, BASE> bidirectional() {
        return flow(null);
    }

    /**
     * Consumer version of {@link #bidirectional()}. The Consumer will immediately be called with the created flow.
     */
    default SimpleProtocol<BUF, BASE> bidirectional(Consumer<SimpleFlow<BUF, BASE>> consumer) {
        return flow(null, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Client to Server
     */
    default <MSG extends BASE> SimpleProtocol<BUF, BASE> bidirectional(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler) {
        var flow = flow(null);
        flow.add(type, codec, handler);
        return this;
    }
}
