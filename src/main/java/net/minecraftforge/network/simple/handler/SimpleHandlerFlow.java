/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple.handler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.simple.SimpleBuildable;

import java.util.function.BiConsumer;

public interface SimpleHandlerFlow<BUF extends FriendlyByteBuf, BASE> extends SimpleHandlerProtocol<BUF, BASE>, SimpleBuildable {
    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * The handler is called on the network thread, and so should not interact with most game state by default.
     * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
     * client thread. Alternatively one can use {@link #addMain(Class, StreamCodec)} to run the handler on the
     * main thread.
     */
    <MSG extends BASE> SimpleHandlerFlow<BUF, BASE> add(Class<MSG> type, StreamCodec<BUF, MSG> codec);

    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * Unlike {@link #add(Class,StreamCodec)}, the consumer is called on the main thread, and so can
     * interact with most game state by default.
     */
    <MSG extends BASE> SimpleHandlerFlow<BUF, BASE> addMain(Class<MSG> type, StreamCodec<BUF, MSG> codec);

    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * The handler is called on the network thread, and so should not interact with most game state by default.
     * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
     * client thread.
     */
    <MSG extends BASE> SimpleHandlerFlow<BUF, BASE> add(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler);
}
