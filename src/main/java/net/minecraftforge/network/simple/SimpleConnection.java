/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.network.NetworkProtocol;

public interface SimpleConnection<BASE, PARENT> extends SimpleBuildable<PARENT> {
    /**
     * Creates a builder grouping together all packets under the same protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    abstract <BUF extends FriendlyByteBuf> SimpleProtocol<BUF, BASE, SimpleConnection<BASE, PARENT>> protocol(NetworkProtocol<BUF> protocol);

    /**
     * Consumer version of {@link #protocol(NetworkProtocol)}. The Consumer will immediately be called with the created protocol.
     */
    default <BUF extends FriendlyByteBuf> SimpleConnection<BASE, PARENT> protocol(NetworkProtocol<BUF> protocol, Consumer<SimpleProtocol<BUF, BASE, SimpleConnection<BASE, PARENT>>> consumer) {
        var tmp = protocol(protocol);
        consumer.accept(tmp);
        return tmp.build();
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Configuration protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<FriendlyByteBuf, BASE, SimpleConnection<BASE, PARENT>> configuration() {
        return protocol(NetworkProtocol.CONFIGURATION);
    }

    /**
     * Consumer version of {@link #configuration()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleConnection<BASE, PARENT> configuration(Consumer<SimpleProtocol<FriendlyByteBuf, BASE, SimpleConnection<BASE, PARENT>>> consumer) {
        return protocol(NetworkProtocol.CONFIGURATION, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Login protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<FriendlyByteBuf, BASE, SimpleConnection<BASE, PARENT>> login() {
        return protocol(NetworkProtocol.LOGIN);
    }

    /**
     * Consumer version of {@link #login()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleConnection<BASE, PARENT> login(Consumer<SimpleProtocol<FriendlyByteBuf, BASE, SimpleConnection<BASE, PARENT>>> consumer) {
        return protocol(NetworkProtocol.LOGIN, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Play protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<RegistryFriendlyByteBuf, BASE, SimpleConnection<BASE, PARENT>> play() {
        return protocol(NetworkProtocol.PLAY);
    }

    /**
     * Consumer version of {@link #play()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleConnection<BASE, PARENT> play(Consumer<SimpleProtocol<RegistryFriendlyByteBuf, BASE, SimpleConnection<BASE, PARENT>>> consumer) {
        return protocol(NetworkProtocol.PLAY, consumer);
    }
}
