package net.minecraftforge.network.simple;

import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.network.NetworkProtocol;
import net.minecraftforge.network.SimpleChannel;

public interface SimpleConnection<T> extends SimpleBuildable<SimpleChannel> {
    /**
     * Creates a builder grouping together all packets under the same protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    abstract <BUF extends FriendlyByteBuf> SimpleProtocol<BUF, T, SimpleConnection<T>> protocol(NetworkProtocol<BUF> protocol);

    /**
     * Consumer version of {@link #protocol(NetworkProtocol)}. The Consumer will immediately be called with the created protocol.
     */
    default <BUF extends FriendlyByteBuf> SimpleConnection<T> protocol(NetworkProtocol<BUF> protocol, Consumer<SimpleProtocol<BUF, T, SimpleConnection<T>>> consumer) {
        var tmp = protocol(protocol);
        consumer.accept(tmp);
        return tmp.build();
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Configuration protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<FriendlyByteBuf, T, SimpleConnection<T>> configuration() {
        return protocol(NetworkProtocol.CONFIGURATION);
    }

    /**
     * Consumer version of {@link #configuration()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleConnection<T> configuration(Consumer<SimpleProtocol<FriendlyByteBuf, T, SimpleConnection<T>>> consumer) {
        return protocol(NetworkProtocol.CONFIGURATION, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Login protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<FriendlyByteBuf, T, SimpleConnection<T>> login() {
        return protocol(NetworkProtocol.LOGIN);
    }

    /**
     * Consumer version of {@link #login()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleConnection<T> login(Consumer<SimpleProtocol<FriendlyByteBuf, T, SimpleConnection<T>>> consumer) {
        return protocol(NetworkProtocol.LOGIN, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Play protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<RegistryFriendlyByteBuf, T, SimpleConnection<T>> play() {
        return protocol(NetworkProtocol.PLAY);
    }

    /**
     * Consumer version of {@link #play()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleConnection<T> play(Consumer<SimpleProtocol<RegistryFriendlyByteBuf, T, SimpleConnection<T>>> consumer) {
        return protocol(NetworkProtocol.PLAY, consumer);
    }
}
