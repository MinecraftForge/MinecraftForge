/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface BaseProtocol<FLOW, PROTOCOL extends BaseProtocol<FLOW, PROTOCOL>> {
    FLOW flow(@Nullable PacketFlow flow);

    /**
     * Consumer version of {@link #flow(PacketFlow)}. The Consumer will immediately be called with the created protocol.
     */
    default PROTOCOL flow(@Nullable PacketFlow flow, Consumer<FLOW> consumer) {
        consumer.accept(flow(flow));
        @SuppressWarnings("unchecked")
        var ret = (PROTOCOL)this;
        return ret;
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Server to Client
     */
    default FLOW clientbound() {
        return flow(PacketFlow.CLIENTBOUND);
    }

    /**
     * Consumer version of {@link #clientbound()}. The Consumer will immediately be called with the created flow.
     */
    default PROTOCOL clientbound(Consumer<FLOW> consumer) {
        return flow(PacketFlow.CLIENTBOUND, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Client to Server
     */
    default FLOW serverbound() {
        return flow(PacketFlow.SERVERBOUND);
    }

    /**
     * Consumer version of {@link #serverbound()}. The Consumer will immediately be called with the created flow.
     */
    default PROTOCOL serverbound(Consumer<FLOW> consumer) {
        return flow(PacketFlow.SERVERBOUND, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from either flow
     */
    default FLOW bidirectional() {
        return flow((PacketFlow)null);
    }

    /**
     * Consumer version of {@link #bidirectional()}. The Consumer will immediately be called with the created flow.
     */
    default PROTOCOL bidirectional(Consumer<FLOW> consumer) {
        return flow(null, consumer);
    }

    /**
     * An alias for {@link #bidirectional(Consumer)}. The Consumer will immediately be called with the created flow.
     */
    default PROTOCOL flow(Consumer<FLOW> consumer) {
        return bidirectional(consumer);
    }
}
