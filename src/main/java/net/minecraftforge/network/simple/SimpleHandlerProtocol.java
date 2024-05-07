/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface SimpleHandlerProtocol<BUF extends FriendlyByteBuf, BASE> extends SimpleConnection<Object> {

    SimpleHandlerFlow<BUF, BASE> flow(@Nullable PacketFlow flow);

    default SimpleHandlerProtocol<BUF, BASE> flow(@Nullable PacketFlow flow, Consumer<SimpleHandlerFlow<BUF, BASE>> consumer) {
        consumer.accept(flow(flow));
        return this;
    }

    default SimpleHandlerFlow<BUF, BASE> clientbound() {
        return flow(PacketFlow.CLIENTBOUND);
    }

    default SimpleHandlerProtocol<BUF, BASE> clientbound(Consumer<SimpleHandlerFlow<BUF, BASE>> consumer) {
        return flow(PacketFlow.CLIENTBOUND, consumer);
    }

    default SimpleHandlerFlow<BUF, BASE> serverbound() {
        return flow(PacketFlow.SERVERBOUND);
    }

    default SimpleHandlerProtocol<BUF, BASE> serverbound(Consumer<SimpleHandlerFlow<BUF, BASE>> consumer) {
        return flow(PacketFlow.SERVERBOUND, consumer);
    }

    default SimpleHandlerFlow<BUF, BASE> bidirectional() {
        return flow(null);
    }

    default SimpleHandlerProtocol<BUF, BASE> bidirectional(Consumer<SimpleHandlerFlow<BUF, BASE>> consumer) {
        return flow(null, consumer);
    }
}
