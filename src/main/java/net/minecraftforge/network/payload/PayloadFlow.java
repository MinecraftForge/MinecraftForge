/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.payload;

import java.util.function.BiConsumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.network.ChannelBuildable;

public interface PayloadFlow<BUF extends FriendlyByteBuf, BASE extends CustomPacketPayload> extends PayloadProtocol<BUF, BASE>, ChannelBuildable<CustomPacketPayload> {
    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * The handler is called on the network thread, and so should not interact with most game state by default.
     * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
     * client thread.
     */
    <MSG extends BASE> PayloadFlow<BUF, BASE> add(Type<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, Context> handler);

    /**
     * Adds a packet to this channel that has it's protocol validated whenever sent or received.
     * <p>
     * Unlike {@link #add(Class,StreamCodec,BiConsumer)}, the consumer is called on the main thread, and so can
     * interact with most game state by default.
     */
    default <MSG extends BASE> PayloadFlow<BUF, BASE> addMain(Type<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, Context> handler) {
        return add(type, codec, (msg, ctx) -> {
            ctx.enqueueWork(() -> handler.accept(msg, ctx));
            ctx.setPacketHandled(true);
        });
    }
}
