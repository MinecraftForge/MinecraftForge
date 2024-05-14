/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import java.util.function.BiConsumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.event.network.CustomPayloadEvent;

public interface SimpleFlow<BUF extends FriendlyByteBuf, BASE> extends SimpleProtocol<BUF, BASE>, SimpleBuildable {
    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * The handler is called on the network thread, and so should not interact with most game state by default.
     * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
     * client thread.
     */
    <MSG extends BASE> SimpleFlow<BUF, BASE> add(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler);

    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * Unlike {@link #add(Class,StreamCodec,BiConsumer)}, the consumer is called on the main thread, and so can
     * interact with most game state by default.
     */
    default <MSG extends BASE> SimpleFlow<BUF, BASE> addMain(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler) {
        return add(type, codec, (msg, ctx) -> {
            ctx.enqueueWork(() -> handler.accept(msg, ctx));
            ctx.setPacketHandled(true);
        });
    }
}
