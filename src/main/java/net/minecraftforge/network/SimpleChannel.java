/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.function.TriConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import io.netty.buffer.Unpooled;
import io.netty.util.AttributeKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

public class SimpleChannel extends Channel<Object> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("SIMPLE_CHANNEL");

    @ApiStatus.Internal
    SimpleChannel(NetworkInstance instance) {
        super(instance);
        instance.addListener(this::networkEventListener);
    }

    /**
     * Build a new MessageBuilder, using the next available discriminator.
     *
     * @param type Type of message
     * @param <M> Type of type
     */
    public <M> MessageBuilder<M> messageBuilder(final Class<M> type) {
        return messageBuilder(type, nextIndex());
    }

    /**
     * Build a new MessageBuilder.
     *
     * @param type Type of message
     * @param discriminator Manually configured discriminator, Must be a positive number.
     * @param <M> Type of type
     */
    public <M> MessageBuilder<M> messageBuilder(final Class<M> type, int discriminator) {
        return messageBuilder(type, discriminator, null);
    }

    /**
     * Build a new MessageBuilder, using the next available discriminator.
     *
     * @param type Type of message
     * @param direction a impl direction which will be asserted before any processing of this message occurs. Use to
     *                  enforce strict sided handling to prevent spoofing.
     * @param <M> Type of type
     */
    public <M> MessageBuilder<M> messageBuilder(final Class<M> type, NetworkDirection direction) {
        return messageBuilder(type, nextIndex(), direction);
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
    public <M> MessageBuilder<M> messageBuilder(final Class<M> type, int discriminator, NetworkDirection direction) {
        return new MessageBuilder<>(this, type, discriminator, direction);
    }

    public static class MessageBuilder<MSG>  {
        private final SimpleChannel channel;
        private final Class<MSG> type;
        private final int id;
        private final Optional<NetworkDirection> direction;

        private BiConsumer<MSG, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, MSG> decoder;
        private BiConsumer<MSG, CustomPayloadEvent.Context> consumer;

        private MessageBuilder(final SimpleChannel channel, final Class<MSG> type, int id, @Nullable NetworkDirection networkDirection) {
            this.channel = channel;
            this.type = type;
            this.id = id;
            this.direction = Optional.ofNullable(networkDirection);
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
        public MessageBuilder<MSG> encoder(BiConsumer<MSG, FriendlyByteBuf> encoder) {
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
        public MessageBuilder<MSG> decoder(Function<FriendlyByteBuf, MSG> decoder) {
            this.decoder = decoder;
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
        public MessageBuilder<MSG> consumerNetworkThread(BiConsumer<MSG, CustomPayloadEvent.Context> consumer) {
            this.consumer = consumer;
            return this;
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
        public <C> MessageBuilder<MSG> consumerNetworkThread(AttributeKey<C> key, TriConsumer<C, MSG, CustomPayloadEvent.Context> consumer) {
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
        public MessageBuilder<MSG> consumerNetworkThread(ToBooleanBiFunction<MSG, CustomPayloadEvent.Context> handler) {
            this.consumer = (msg, ctx) -> ctx.setPacketHandled(handler.applyAsBool(msg, ctx));
            return this;
        }

        /**
         * Function returning a boolean "packet handled" indication, for simpler channel building.
         *
         * @see #consumerNetworkThread(AttributeKey,TriConsumer)
         */
        public <C> MessageBuilder<MSG> consumerNetworkThread(AttributeKey<C> key, ToBooleanTriFunction<C, MSG, CustomPayloadEvent.Context> handler) {
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
        public MessageBuilder<MSG> consumerMainThread(BiConsumer<MSG, CustomPayloadEvent.Context> consumer) {
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
        public <C> MessageBuilder<MSG> consumerMainThread(AttributeKey<C> key, TriConsumer<C, MSG, CustomPayloadEvent.Context> consumer) {
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
            if (this.id < 0)
                throw new IllegalStateException("Failed to register SimpleChannel message, Invalid ID " + this.id + ": " + this.type.getName());

            var msg = new Message<>(this.id, this.type, this.direction, this.encoder, this.decoder, this.consumer);

            if (channel.byId.containsKey(msg.index()))
                throw new IllegalStateException("Failed to register SimpleChannel message, ID " + msg.index() + " already claimed: " + msg.type().getName());
            if (channel.byType.containsKey(msg.type()))
                throw new IllegalStateException("Failed to register SimpleChannel message, Class " + msg.type().getName() + " already registered");

            channel.byId.put(msg.index(), msg);
            channel.byType.put(msg.type(), msg);
            return this.channel;
        }
    }

    /* ===================================================================================
     *                           INTERNAL BELOW THIS
     * ===================================================================================
     */
    private int lastIndex = 0;
    private Int2ObjectMap<Message<?>> byId = new Int2ObjectArrayMap<>();
    private Object2ObjectMap<Class<?>, Message<?>> byType = new Object2ObjectArrayMap<>();

    private record Message<MSG>(
        int index,
        Class<MSG> type,
        Optional<NetworkDirection> direction,
        BiConsumer<MSG, FriendlyByteBuf> encoder,
        Function<FriendlyByteBuf, MSG> decoder,
        BiConsumer<MSG, CustomPayloadEvent.Context> consumer
    ){ };

    private int nextIndex() {
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
        var msg = byId.get(id);
        if (msg == null) {
            LOGGER.error(MARKER, "Received invalid discriminator {} on channel {}", id, getName());
            return;
        }

        var con = event.getSource().getConnection();
        var dir = event.getSource().getDirection();

        if (msg.direction.isPresent() && msg.direction.get() != dir) {
            var error = "Illegal packet received, terminating connection. " + msg.type().getName() + " expexted " + msg.direction.get() + " but was " + dir;
            LOGGER.error(MARKER, error);
            con.disconnect(Component.literal(error));
            throw new IllegalStateException(error);
        }

        decodeAndDispatch(data, event.getSource(), msg);
    }

    // Yay generics
    private static <MSG> void decodeAndDispatch(FriendlyByteBuf data, CustomPayloadEvent.Context ctx, Message<MSG> msg) {
        var message = msg.decoder().apply(data);
        msg.consumer.accept(message, ctx);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FriendlyByteBuf toBuffer(Object message) {
        var msg = byType.get(message.getClass());

        if (msg == null) {
            LOGGER.error(MARKER, "Attempted to send invalid message {} on channel {}", message.getClass().getName(), getName());
            throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
        }
        var ret = new FriendlyByteBuf(Unpooled.buffer());
        ret.writeVarInt(msg.index());
        if (msg.encoder() != null)
            ((BiConsumer<Object, FriendlyByteBuf>)msg.encoder()).accept(message, ret);

        return ret;
    }
}
