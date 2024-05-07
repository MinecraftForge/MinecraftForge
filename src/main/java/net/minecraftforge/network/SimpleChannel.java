/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.network.simple.SimplePacket;
import net.minecraftforge.network.simple.SimpleBuildable;
import net.minecraftforge.network.simple.SimpleConnection;
import net.minecraftforge.network.simple.SimpleFlow;
import net.minecraftforge.network.simple.SimpleProtocol;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.function.TriConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import io.netty.util.AttributeKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

public class SimpleChannel extends Channel<Object> implements SimpleConnection<Object, SimpleChannel> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("SIMPLE_CHANNEL");
    private boolean built = false;

    @ApiStatus.Internal
    SimpleChannel(NetworkInstance instance) {
        super(instance);
        instance.addListener(this::networkEventListener);
    }

    /**
     * Creates a builder context where every packet type must implement {@link SimplePacket}.
     * All added packets will automatically have their {@link SimplePacket#handle(CTX, CustomMessagePayloadEvent.Context)} method called.
     * This is added to mimic vanilla design.
     *
     * @param context AttributeKey holding a reference to the context object. See {@link ChannelBuilder#attribute(AttributeKey, Supplier)}
     */
    public <CTX, BASE extends SimplePacket<CTX>> SimpleConnection<BASE, SimpleConnection<Object, SimpleChannel>> handler(AttributeKey<CTX> context) {
        return new ContextConnection<>(this, new SimpleContext<>(this).base(context));
    }

    /**
     * Creates a builder grouping together all packets under the same protocol.
     * This will validate that the protocol matches before the packet is sent or received.
     */
    public <BUF extends FriendlyByteBuf> SimpleProtocol<BUF, Object, SimpleConnection<Object, SimpleChannel>> protocol(NetworkProtocol<BUF> protocol) {
        return new Protocol<>(this, new SimpleContext<>(this).protocol(protocol));
    }

    /**
     * Build a new MessageBuilder, using the next available discriminator.
     *
     * @param type Type of message
     * @param <M> Type of type
     */
    public <M> MessageBuilder<M, FriendlyByteBuf> messageBuilder(Class<M> type) {
        return messageBuilder(type, nextIndex());
    }

    /**
     * Build a new MessageBuilder.
     *
     * @param type Type of message
     * @param discriminator Manually configured discriminator, Must be a positive number.
     * @param <M> Type of type
     */
    public <M> MessageBuilder<M, FriendlyByteBuf> messageBuilder(Class<M> type, int discriminator) {
        return messageBuilder(type, discriminator, (NetworkProtocol<FriendlyByteBuf>)null);
    }

    /**
     * Build a new MessageBuilder, using the next available discriminator.
     *
     * @param type Type of message
     * @param protocol The protocol that this packet is allowed to be sent/received in, Use enforce strict state handling to prevent spoofing.
     * @param <M> Type of type
     */
    public <M, B extends FriendlyByteBuf> MessageBuilder<M, B> messageBuilder(Class<M> type, NetworkProtocol<B> protocol) {
        return messageBuilder(type, nextIndex(), protocol);
    }

    /**
     * Build a new MessageBuilder, using the next available discriminator.
     *
     * @param type Type of message
     * @param direction a impl direction which will be asserted before any processing of this message occurs. Use to
     *                  enforce strict sided handling to prevent spoofing.
     * @param <M> Type of type
     */
    public <M, B extends FriendlyByteBuf> MessageBuilder<M, B> messageBuilder(Class<M> type, NetworkDirection<B> direction) {
        return messageBuilder(type, nextIndex(), direction.protocol()).direction(direction.direction());
    }

    /**
     * Build a new MessageBuilder, using the next available discriminator.
     *
     * @param type Type of message
     * @param discriminator Manually configured discriminator, Must be a positive number.
     * @param protocol The protocol that this packet is allowed to be sent/received in, Use enforce strict state handling to prevent spoofing.
     * @param <M> Type of type
     */
    public <M, B extends FriendlyByteBuf> MessageBuilder<M, B> messageBuilder(Class<M> type, int descriminator, NetworkProtocol<B> protocol) {
        checkBuilt();
        return new MessageBuilder<>(this, type, descriminator, protocol);
    }

    /**
     * Build a new MessageBuilder.
     *
     * @param type Type of message
     * @param discriminator Manually configured discriminator, Must be a positive number.
     * @param direction a impl direction which will be asserted before any processing of this message occurs. Use to
     *                  enforce strict sided handling to prevent spoofing.
     * @param <M> Type of type
     */
    public <M, B extends FriendlyByteBuf> MessageBuilder<M, B> messageBuilder(Class<M> type, int discriminator, NetworkDirection<B> direction) {
        return messageBuilder(type, discriminator, direction.protocol()).direction(direction.direction());
    }

    /**
     * Finishes off the builder side of the SimpleChanel. This prevents adding more messages to this channel.
     */
    @Override
    public SimpleChannel build() {
        checkBuilt();
        this.built = true;
        return this;
    }

    @Override
    public SimpleChannel buildAll() {
        return build();
    }

    private void checkBuilt() {
        if (built)
            throw new IllegalStateException("SimpleChannel builder is fully built, can not modify it any more");
    }

    public static class MessageBuilder<MSG, BUF extends FriendlyByteBuf> extends Buildable<SimpleChannel> {
        private final SimpleChannel channel;
        private final Class<MSG> type;
        private final int id;
        private final NetworkProtocol<BUF> protocol;

        private BiConsumer<MSG, BUF> encoder;
        private Function<BUF, MSG> decoder;
        private BiConsumer<MSG, CustomPayloadEvent.Context> consumer;
        private PacketFlow direction;

        private MessageBuilder(SimpleChannel channel, Class<MSG> type, int id, @Nullable NetworkProtocol<BUF> protocol) {
            super(channel);
            this.channel = channel;
            this.type = type;
            this.id = id;
            this.protocol = protocol;
        }

        /**
         * Set the direction that this packet is allowed to be sent/received.
         * Use to enforce strict sided handling to prevent spoofing.
         *
         * @param direction The direction this packet is allowed on.
         * @return This message builder, for chaining.
         */
        public MessageBuilder<MSG, BUF> direction(PacketFlow direction) {
            this.direction = direction;
            return this;
        }

        /**
         * Set the message encoder, which writes this message to a {@link FriendlyByteBuf}.
         * <p>
         * The encoder is called <em>immediately</em> {@linkplain #send(PacketDistributor.PacketTarget, Object) when the
         * packet is sent}. This means encoding typically occurs on the main server/client thread rather than on the
         * network thread.
         * <p>
         * However, this behaviour should not be relied on, and the encoder should try to be thread-safe and not
         * interact with the current game state.
         *
         * @param encoder The message encoder.
         * @return This message builder, for chaining.
         */
        public MessageBuilder<MSG, BUF> encoder(BiConsumer<MSG, BUF> encoder) {
            this.encoder = encoder;
            return this;
        }

        /**
         * Set the message decoder, which reads the message from a {@link FriendlyByteBuf}.
         * <p>
         * The decoder is called when the message is received on the network thread. The decoder should not attempt to
         * access or mutate any game state, deferring that until the {@linkplain #consumer(ToBooleanBiFunction) the
         * message is handled}.
         *
         * @param decoder The message decoder.
         * @return The message builder, for chaining.
         */
        public MessageBuilder<MSG, BUF> decoder(Function<BUF, MSG> decoder) {
            this.decoder = decoder;
            return this;
        }

        /**
         * Set the StreamCodec to be used for marshaling the message object to and from a {@link FriendlyByteBuf}
         * <p>
         * This has all the same limitations of {@link #encoder(BiFunction)} and {@link #decoder(Function)} as it is equivalent of calling those functions
         * with the codec's encode and decode functions.
         *
         * @param codec The codec to use.
         * @return The message builder, for chaining.
         */
        public MessageBuilder<MSG, BUF> codec(StreamCodec<BUF, MSG> codec) {
            return this.encoder((msg, buf) -> codec.encode(buf, msg))
                .decoder(codec::decode);
        }

        /**
         * Set the message consumer, which is called once a message has been decoded. This accepts the decoded message
         * object and the message's context.
         * <p>
         * The consumer is called on the network thread, and so should not interact with most game state by default.
         * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
         * client thread. Alternatively one can use {@link #consumerMainThread(BiConsumer)} to run the handler on the
         * main thread.
         *
         * @param consumer The message consumer.
         * @return The message builder, for chaining.
         *
         * @see #consumerMainThread(BiConsumer)
         */
        public MessageBuilder<MSG, BUF> consumer(BiConsumer<MSG, CustomPayloadEvent.Context> consumer) {
            this.consumer = consumer;
            return this;
        }

        /**
         * Set the message consumer, which is called once a message has been decoded. This accepts the decoded message
         * object and the message's context.
         * <p>
         * The consumer is called on the network thread, and so should not interact with most game state by default.
         * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
         * client thread. Alternatively one can use {@link #consumerMainThread(BiConsumer)} to run the handler on the
         * main thread.
         *
         * @param consumer The message consumer.
         * @return The message builder, for chaining.
         *
         * @see #consumerMainThread(BiConsumer)
         */
        public MessageBuilder<MSG, BUF> consumerNetworkThread(BiConsumer<MSG, CustomPayloadEvent.Context> consumer) {
            return this.consumer(consumer);
        }

        /**
         * Set the message consumer, which is called once a message has been decoded. This accepts the decoded message
         * object and the message's context. The instance will be retrieved from the associated AttributeKey on the
         * channel.
         * <p>
         * The consumer is called on the network thread, and so should not interact with most game state by default.
         * {@link CustomPayloadEvent.Context#enqueueWork(Runnable)} can be used to handle the message on the main server or
         * client thread. Alternatively one can use {@link #consumerMainThread(TriConsumer)} to run the handler on the
         * main thread.
         *
         * @param consumer The message consumer.
         * @return The message builder, for chaining.
         *
         * @see #consumerMainThread(BiConsumer)
         */
        public <C> MessageBuilder<MSG, BUF> consumerNetworkThread(AttributeKey<C> key, TriConsumer<C, MSG, CustomPayloadEvent.Context> consumer) {
            return consumerNetworkThread((msg, ctx) -> {
                var inst = ctx.getConnection().channel().attr(key).get();
                consumer.accept(inst, msg, ctx);
            });
        }

        /**
         * Function returning a boolean "packet handled" indication, for simpler channel building.
         *
         * @see #consumerNetworkThread(BiConsumer)
         */
        public MessageBuilder<MSG, BUF> consumerNetworkThread(ToBooleanBiFunction<MSG, CustomPayloadEvent.Context> handler) {
            this.consumer = (msg, ctx) -> ctx.setPacketHandled(handler.applyAsBool(msg, ctx));
            return this;
        }

        /**
         * Function returning a boolean "packet handled" indication, for simpler channel building.
         *
         * @see #consumerNetworkThread(AttributeKey,TriConsumer)
         */
        public <C> MessageBuilder<MSG, BUF> consumerNetworkThread(AttributeKey<C> key, ToBooleanTriFunction<C, MSG, CustomPayloadEvent.Context> handler) {
            return consumerNetworkThread((msg, ctx) -> {
                var inst = ctx.getConnection().channel().attr(key).get();
                return handler.applyAsBool(inst, msg, ctx);
            });
        }

        /**
         * Set the message consumer, which is called once a message has been decoded. This accepts the decoded message
         * object and the message's context.
         * <p>
         * Unlike {@link #consumerNetworkThread(BiConsumer)}, the consumer is called on the main thread, and so can
         * interact with most game state by default.
         *
         * @param consumer The message consumer.
         * @return The message builder, for chaining.
         *
         * @see #consumerNetworkThread(BiConsumer)
         */
        public MessageBuilder<MSG, BUF> consumerMainThread(BiConsumer<MSG, CustomPayloadEvent.Context> consumer) {
            this.consumer = (msg, context) -> {
                var ctx = context;
                ctx.enqueueWork(() -> consumer.accept(msg, context));
                ctx.setPacketHandled(true);
            };
            return this;
        }

        /**
         * Set the message consumer, which is called once a message has been decoded. This accepts the decoded message
         * object and the message's context. The instance will be retrieved from the associated AttributeKey on the
         * channel.
         * <p>
         * Unlike {@link #consumerNetworkThread(AttributeKey,TriConsumer)}, the consumer is called on the main thread,
         * and so can interact with most game state by default.
         *
         * @param consumer The message consumer.
         * @return The message builder, for chaining.
         *
         * @see #consumerNetworkThread(BiConsumer)
         */
        public <C> MessageBuilder<MSG, BUF> consumerMainThread(AttributeKey<C> key, TriConsumer<C, MSG, CustomPayloadEvent.Context> consumer) {
            this.consumer = (msg, ctx) -> {
                ctx.enqueueWork(() -> {
                    var inst = ctx.getConnection().channel().attr(key).get();
                    consumer.accept(inst, msg, ctx);
                });
                ctx.setPacketHandled(true);
            };
            return this;
        }

        // TODO: Move out to full classes?
        @FunctionalInterface
        public interface ToBooleanBiFunction<T, U> {
            boolean applyAsBool(T first, U second);
        }
        @FunctionalInterface
        public interface ToBooleanTriFunction<T, U, V> {
            boolean applyAsBool(T first, U second, V third);
        }

        /**
         * Finishes building this packet.
         * @return The attached SimpleChannel to facilitate chaining.
         */
        public SimpleChannel add() {
            checkBuilt();

            if (this.id < 0)
                throw new IllegalStateException("Failed to register SimpleChannel message, Invalid ID " + this.id + ": " + this.type.getName());

            var msg = new Message<MSG, BUF>(
                this.id, this.type, this.protocol, this.direction,
                this.encoder, this.decoder, this.consumer
            );

            if (channel.byId.containsKey(msg.index()))
                throw new IllegalStateException("Failed to register SimpleChannel message, ID " + msg.index() + " already claimed: " + msg.type().getName());
            if (channel.byType.containsKey(msg.type()))
                throw new IllegalStateException("Failed to register SimpleChannel message, Class " + msg.type().getName() + " already registered");

            channel.byId.put(msg.index(), msg);
            channel.byType.put(msg.type(), msg);

            return this.channel;
        }

        public SimpleChannel build() {
            return add();
        }

        @Override
        public SimpleChannel buildAll() {
            return build().build();
        }
    }

    private record SimpleContext<BUF extends FriendlyByteBuf, BASE>(
        SimpleChannel channel,
        NetworkProtocol<BUF> protocol,
        PacketFlow flow,
        Function<Boolean, BiConsumer<BASE, CustomPayloadEvent.Context>> defaultConsumer
    ) {
        private SimpleContext(SimpleChannel channel) {
            this(channel, null, null, null);
        }

        @SuppressWarnings("unchecked")
        private <MSG extends BASE> BiConsumer<MSG, Context> consumer(boolean network) {
            if (this.defaultConsumer == null)
                throw new IllegalStateException("DefaultConsumer factory not set");
            return (BiConsumer<MSG, Context>)this.defaultConsumer.apply(network);
        }

        private <NEWBUF extends FriendlyByteBuf> SimpleContext<NEWBUF, BASE> protocol(NetworkProtocol<NEWBUF> protocol){
            return new SimpleContext<>(channel, protocol, flow, defaultConsumer);
        }

        private SimpleContext<BUF, BASE> flow(PacketFlow flow) {
            return new SimpleContext<>(channel, protocol, flow, defaultConsumer);
        }

        private <CTX, NEWBASE extends SimplePacket<CTX>> SimpleContext<BUF, NEWBASE> base(AttributeKey<CTX> context) {
            return new SimpleContext<>(
                channel, protocol, flow,
                network -> {
                    if (network) {
                        return (msg, ctx) -> {
                            var inst = ctx.getConnection().channel().attr(context).get();
                            ctx.setPacketHandled(msg.handle(inst, ctx));
                        };
                    } else {
                        return (msg, ctx) -> {
                            ctx.enqueueWork(() -> {
                                var inst = ctx.getConnection().channel().attr(context).get();
                                msg.handle(inst, ctx);
                            });
                            ctx.setPacketHandled(true);
                        };
                    }
                }
            );
        }
    };

    private static class ContextConnection<BASE, PARENT extends SimpleBuildable<?>> extends Buildable<PARENT> implements SimpleConnection<BASE, PARENT> {
        private final SimpleContext<FriendlyByteBuf, BASE> ctx;

        private ContextConnection(PARENT parent, SimpleContext<FriendlyByteBuf, BASE> ctx) {
            super(parent);
            this.ctx = ctx;
        }

        @Override
        public <BUF extends FriendlyByteBuf> SimpleProtocol<BUF, BASE, SimpleConnection<BASE, PARENT>> protocol(NetworkProtocol<BUF> protocol) {
            return new Protocol<>(this, this.ctx.protocol(protocol));
        }
    }

    private static class Protocol<BUF extends FriendlyByteBuf, BASE, PARENT extends SimpleBuildable<?>> extends Buildable<PARENT> implements SimpleProtocol<BUF, BASE, PARENT> {
        private final SimpleContext<BUF, BASE> ctx;

        private Protocol(PARENT parent, SimpleContext<BUF, BASE> ctx) {
            super(parent);
            this.ctx = ctx;
        }

        @Override
        public SimpleFlow<BUF, BASE, SimpleProtocol<BUF, BASE, PARENT>> flow(PacketFlow flow) {
            return new Flow<>(this, this.ctx.flow(flow));
        }

        @Override
        public <MSG extends BASE> Protocol<BUF, BASE, PARENT> add(Class<MSG> type, StreamCodec<BUF, MSG> codec) {
            return add(type, codec, ctx.consumer(true));
        }

        @Override
        public <MSG extends BASE> Protocol<BUF, BASE, PARENT> addMain(Class<MSG> type, StreamCodec<BUF, MSG> codec) {
            return add(type, codec, ctx.consumer(false));
        }

        @Override
        public <MSG extends BASE> Protocol<BUF, BASE, PARENT> add(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler) {
            ctx.channel.messageBuilder(type, ctx.protocol)
                .codec(codec)
                .consumer(handler)
                .add();
            return this;
        }
    }

    private static class Flow<BUF extends FriendlyByteBuf, BASE, PARENT extends SimpleBuildable<?>> extends Buildable<PARENT> implements SimpleFlow<BUF, BASE, PARENT> {
        private final SimpleContext<BUF, BASE> ctx;

        private Flow(PARENT parent, SimpleContext<BUF, BASE> ctx) {
            super(parent);
            this.ctx = ctx;
        }

        @Override
        public <MSG extends BASE> SimpleFlow<BUF, BASE, PARENT> add(Class<MSG> type, StreamCodec<BUF, MSG> codec) {
            return add(type, codec, ctx.consumer(true));
        }

        @Override
        public <MSG extends BASE> SimpleFlow<BUF, BASE, PARENT> addMain(Class<MSG> type, StreamCodec<BUF, MSG> codec) {
            return add(type, codec, ctx.consumer(false));
        }

        @Override
        public <MSG extends BASE> SimpleFlow<BUF, BASE, PARENT> add(Class<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, CustomPayloadEvent.Context> handler) {
            ctx.channel.messageBuilder(type, ctx.protocol)
                .codec(codec)
                .consumer(handler)
                .direction(ctx.flow())
                .add();
            return this;
        }
    }

    /* ===================================================================================
     *                           INTERNAL BELOW THIS
     * ===================================================================================
     */
    protected int lastIndex = 0;
    protected Int2ObjectMap<Message<?, ?>> byId = new Int2ObjectArrayMap<>();
    protected Object2ObjectMap<Class<?>, Message<?, ?>> byType = new Object2ObjectArrayMap<>();

    protected record Message<MSG, BUF extends FriendlyByteBuf>(
        int index,
        Class<MSG> type,
        NetworkProtocol<BUF> protocol,
        PacketFlow direction,
        BiConsumer<MSG, BUF> encoder,
        Function<BUF, MSG> decoder,
        BiConsumer<MSG, CustomPayloadEvent.Context> consumer
    ){ };

    protected int nextIndex() {
        while (byId.containsKey(lastIndex)) {
            lastIndex++;
            if (lastIndex < 0)
                throw new IllegalStateException("Ran out of discriminator, only " + Integer.MAX_VALUE + " are allowed");
        }
        return lastIndex;
    }

    private void networkEventListener(CustomPayloadEvent event) {
        var data = event.getPayload();
        if (data == null || !data.isReadable()) {
            LOGGER.error(MARKER, "Received empty payload on channel {} login index {}", getName(), event.getLoginIndex());
            return;
        }

        int id = data.readVarInt();
        @SuppressWarnings("unchecked")
        var msg = (Message<?, FriendlyByteBuf>)byId.get(id);
        if (msg == null) {
            LOGGER.error(MARKER, "Received invalid discriminator {} on channel {}", id, getName());
            return;
        }

        var con = event.getSource().getConnection();
        var protocol = msg.protocol() == null ? con.getProtocol() : msg.protocol().toVanilla();
        var direction = msg.direction() == null ?  con.getReceiving() : msg.direction();

        if (protocol != con.getProtocol() || direction != con.getReceiving()) {
            var error = "Illegal packet received, terminating connection. " + msg.type().getName() + " expected " +
                direction.name() + " " + protocol.name() + " but was " +
                con.getReceiving().name() + " " + con.getProtocol().name();

            LOGGER.error(MARKER, error);
            con.disconnect(Component.literal(error));
            throw new IllegalStateException(error);
        }

        decodeAndDispatch(data, event.getSource(), msg);
    }

    // Yay generics
    private static <MSG> void decodeAndDispatch(FriendlyByteBuf data, CustomPayloadEvent.Context ctx, Message<MSG, FriendlyByteBuf> msg) {
        var message = msg.decoder().apply(data);
        msg.consumer.accept(message, ctx);
    }

    @Override
    protected Packet<?> toVanillaPacket(Connection con, Object message) {
        var msg = byType.get(message.getClass());

        if (msg == null) {
            LOGGER.error(MARKER, "Attempted to send invalid message {} on channel {}", message.getClass().getName(), getName());
            throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
        }

        var protocol = msg.protocol() == null ? con.getProtocol() : msg.protocol().toVanilla();
        var direction = msg.direction() == null ?  con.getSending() : msg.direction();

        if (protocol != con.getProtocol() || direction != con.getSending()) {
            var error = "Illegal packet sent, terminating connection. " + msg.type().getName() + " expected " +
                direction.name() + " " + protocol.name() + " but was " +
                con.getSending().name() + " " + con.getProtocol().name();

            LOGGER.error(MARKER, error);
            con.disconnect(Component.literal(error));
            throw new IllegalStateException(error);
        }

        return super.toVanillaPacket(con, message);
    }

    @Override
    public void encode(FriendlyByteBuf out, Object message) {
        @SuppressWarnings("unchecked")
        var msg = (Message<Object, FriendlyByteBuf>)byType.get(message.getClass());

        if (msg == null) {
            LOGGER.error(MARKER, "Attempted to send invalid message {} on channel {}", message.getClass().getName(), getName());
            throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
        }

        out.writeVarInt(msg.index());
        if (msg.encoder() != null)
            msg.encoder().accept(message, out);
    }

    private static abstract class Buildable<T extends SimpleBuildable<?>> implements SimpleBuildable<T> {
        private final T parent;
        protected boolean built = false;

        private Buildable(T parent) {
            this.parent = parent;
        }

        protected void checkBuilt() {
            if (built)
                throw new IllegalStateException(getClass().getName() + " is fully built, can not modify it any more");
        }

        @Override
        public T build() {
            checkBuilt();
            this.built = true;
            return parent;
        }

        @Override
        public SimpleChannel buildAll() {
            return build().buildAll();
        }
    }
}
