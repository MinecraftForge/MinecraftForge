package net.minecraftforge.network.simple;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkProtocol;

public interface SimpleProtocol<BUF extends FriendlyByteBuf, BASE, PARENT> extends SimpleBuildable<PARENT> {
    /**
     * Creates a builder grouping together all packets under the same protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    abstract <NEWBUF extends FriendlyByteBuf> SimpleProtocol<NEWBUF, BASE, SimpleProtocol<BUF, BASE, PARENT>> protocol(NetworkProtocol<NEWBUF> protocol);

    /**
     * Consumer version of {@link #protocol(NetworkProtocol)}. The Consumer will immediately be called with the created protocol.
     */
    default <NEWBUF extends FriendlyByteBuf> SimpleProtocol<BUF, BASE, PARENT> protocol(NetworkProtocol<NEWBUF> protocol, Consumer<SimpleProtocol<NEWBUF, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        var tmp = protocol(protocol);
        consumer.accept(tmp);
        return tmp.build();
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Configuration protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<FriendlyByteBuf, BASE, SimpleProtocol<BUF, BASE, PARENT>> configuration() {
        return protocol(NetworkProtocol.CONFIGURATION);
    }

    /**
     * Consumer version of {@link #configuration()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleProtocol<BUF, BASE, PARENT> configuration(Consumer<SimpleProtocol<FriendlyByteBuf, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        return protocol(NetworkProtocol.CONFIGURATION, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Login protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<FriendlyByteBuf, BASE, SimpleProtocol<BUF, BASE, PARENT>> login() {
        return protocol(NetworkProtocol.LOGIN);
    }

    /**
     * Consumer version of {@link #login()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleProtocol<BUF, BASE, PARENT> login(Consumer<SimpleProtocol<FriendlyByteBuf, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        return protocol(NetworkProtocol.LOGIN, consumer);
    }

    /**
     * Creates a builder grouping together packets that are only valid when under the Play protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    default SimpleProtocol<RegistryFriendlyByteBuf, BASE, SimpleProtocol<BUF, BASE, PARENT>> play() {
        return protocol(NetworkProtocol.PLAY);
    }

    /**
     * Consumer version of {@link #play()}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleProtocol<BUF, BASE, PARENT> play(Consumer<SimpleProtocol<RegistryFriendlyByteBuf, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        return protocol(NetworkProtocol.PLAY, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and packet sending direction.
     * @param flow The direction that following packets are valid for
     */
    SimpleProtocol<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>> flow(PacketFlow flow);

    /**
     * Consumer version of {@link #flow(PacketFlow)}. The Consumer will immediately be called with the created protocol.
     */
    default SimpleProtocol<BUF, BASE, PARENT> flow(PacketFlow flow, Consumer<SimpleProtocol<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        var tmp = flow(flow);
        consumer.accept(tmp);
        return tmp.build();
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Server to Client
     */
    default SimpleProtocol<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>> clientbound() {
        return flow(PacketFlow.CLIENTBOUND);
    }

    /**
     * Consumer version of {@link #clientbound()}. The Consumer will immediately be called with the created flow.
     */
    default SimpleProtocol<BUF, BASE, PARENT> clientbound(Consumer<SimpleProtocol<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
        return flow(PacketFlow.CLIENTBOUND, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Client to Server
     */
    default SimpleProtocol<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>> serverbound() {
        return flow(PacketFlow.SERVERBOUND);
    }

    /**
     * Consumer version of {@link #serverbound()}. The Consumer will immediately be called with the created flow.
     */
    default SimpleProtocol<BUF, BASE, PARENT> serverbound(Consumer<SimpleProtocol<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>>> consumer) {
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
