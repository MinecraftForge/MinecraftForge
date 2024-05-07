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

public interface SimpleProtocol<BUF extends FriendlyByteBuf, BASE, PARENT> extends SimpleBuildable<PARENT> {
    /**
     * Creates a builder that validates both current protocol, and packet sending direction.
     * @param flow The direction that following packets are valid for
     */
    SimpleFlow<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>> flow(PacketFlow flow);

    /**
     * Consumer version of {@link #flow(PacketFlow)}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleProtocol<BUF, BASE, PARENT> flow(PacketFlow flow, Consumer<SimpleFlow<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        var tmp = flow(flow);
        consumer.accept(tmp);
        return tmp.build();
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Server to Client
     */
    default SimpleFlow<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>> clientbound() {
        return flow(PacketFlow.CLIENTBOUND);
    }

    /**
     * Consumer version of {@link #clientbound()}. The Consumer will immediately be called with the created flow.
     */
    default SimpleProtocol<BUF, BASE, PARENT> clientbound(Consumer<SimpleFlow<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        return flow(PacketFlow.CLIENTBOUND, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Client to Server
     */
    default SimpleFlow<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>> serverbound() {
        return flow(PacketFlow.SERVERBOUND);
    }

    /**
     * Consumer version of {@link #serverbound()}. The Consumer will immediately be called with the created flow.
     */
    default SimpleProtocol<BUF, BASE, PARENT> serverbound(Consumer<SimpleFlow<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        return flow(PacketFlow.SERVERBOUND, consumer);
    }

    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * The handler is called on the network thread, and so should not interact with most game state by default.
     * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
     * client thread. Alternatively one can use {@link #addMain(Class,StreamCodec)} to run the handler on the
     * main thread.
     */
    <MSG extends BASE> SimpleProtocol<BUF, BASE, PARENT> add(Class<MSG> type, StreamCodec<BUF, MSG> codec);

    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * Unlike {@link #add(Class,StreamCodec)}, the consumer is called on the main thread, and so can
     * interact with most game state by default.
     */
    <MSG extends BASE> SimpleProtocol<BUF, BASE, PARENT> addMain(Class<MSG> type, StreamCodec<BUF, MSG> codec);

    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * The handler is called on the network thread, and so should not interact with most game state by default.
     * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
     * client thread.
     */
    <MSG extends BASE> SimpleProtocol<BUF, BASE, PARENT> add(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler);
}
