/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import io.netty.util.AttributeKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.network.simple.handler.SimplePacket;
import net.minecraftforge.network.payload.PayloadBuildable;
import net.minecraftforge.network.payload.PayloadConnection;
import net.minecraftforge.network.payload.PayloadFlow;
import net.minecraftforge.network.payload.PayloadProtocol;
import net.minecraftforge.network.payload.handler.PayloadHandlerFlow;
import net.minecraftforge.network.payload.handler.PayloadHandlerProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public final class PayloadChannel extends Channel<CustomPacketPayload> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("PAYLOAD_CHANNEL");
    private final Int2ObjectMap<Message<?, ?>> byId;
    private final Object2ObjectMap<CustomPacketPayload.Type<?>, Message<?, ?>> byType;

    @ApiStatus.Internal
    private PayloadChannel(Builder builder) {
        super(builder.instance);
        builder.instance.addListener(this::networkEventListener);
        this.byId = Int2ObjectMaps.unmodifiable(builder.byId);
        this.byType = Object2ObjectMaps.unmodifiable(builder.byType);
    }

    public static PayloadConnection<CustomPacketPayload> builder(NetworkInstance instance) {
        return new Builder(instance);
    }

    private final static class Builder implements PayloadConnection<CustomPacketPayload> {
        private final NetworkInstance instance;
        private boolean built = false;
        private int lastIndex = 0;
        private final Int2ObjectMap<Message<?, ?>> byId = new Int2ObjectArrayMap<>();
        private final Object2ObjectMap<CustomPacketPayload.Type<?>, Message<?, ?>> byType = new Object2ObjectArrayMap<>();

        private Builder(NetworkInstance instance) {
            this.instance = instance;
        }

        @Override
        public <NEWBUF extends FriendlyByteBuf, NEWBASE extends CustomPacketPayload> PayloadProtocol<NEWBUF, NEWBASE> protocol(NetworkProtocol<NEWBUF> protocol) {
            return new Protocol<>(new BuilderContext<NEWBUF, NEWBASE>(this).protocol(protocol));
        }

        @Override
        public <NEWBUF extends FriendlyByteBuf, CTX, NEWBASE extends CustomPacketPayload & SimplePacket<CTX>> PayloadHandlerProtocol<NEWBUF, NEWBASE> protocol(AttributeKey<CTX> context, NetworkProtocol<NEWBUF> protocol) {
            return new HandlerProtocol<>(context, new BuilderContext<NEWBUF, NEWBASE>(this).protocol(protocol));
        }

        private int nextIndex() {
            while (byId.containsKey(lastIndex)) {
                lastIndex++;
                if (lastIndex < 0)
                    throw new IllegalStateException("Ran out of discriminator, only " + Integer.MAX_VALUE + " are allowed");
            }
            return lastIndex;
        }

        private void add(Message<?, ?> msg) {
            this.byId.put(msg.index(), msg);
            this.byType.put(msg.type(), msg);
        }

        private void checkBuilt() {
            if (built)
                throw new IllegalStateException("PayloadChannel builder is fully built, can not modify it any more");
        }

        public PayloadChannel build() {
            checkBuilt();
            return new PayloadChannel(this);
        }
    }

    private record BuilderContext<BUF extends FriendlyByteBuf, BASE>(Builder builder, @Nullable NetworkProtocol<BUF> protocol, @Nullable PacketFlow flow) {
        public BuilderContext(Builder builder) {
            this(builder, null, null);
        }

        public <NEWBUF extends FriendlyByteBuf> BuilderContext<NEWBUF, BASE> protocol(NetworkProtocol<NEWBUF> protocol) {
            return new BuilderContext<>(builder, protocol, flow);
        }

        private BuilderContext<BUF, BASE> flow(@Nullable PacketFlow flow) {
            return new BuilderContext<>(builder, protocol, flow);
        }
    }

    private record Protocol<BUF extends FriendlyByteBuf, BASE extends CustomPacketPayload>(BuilderContext<BUF, BASE> ctx) implements ProtocolFactory<BUF, BASE>, PayloadProtocol<BUF, BASE> {
        @Override
        public PayloadFlow<BUF, BASE> flow(@Nullable PacketFlow flow) {
            return new Flow<>(ctx.flow(flow));
        }
    }

    private record HandlerProtocol<BUF extends FriendlyByteBuf, CTX, BASE extends CustomPacketPayload & SimplePacket<CTX>>(
        AttributeKey<CTX> key,
        BuilderContext<BUF, BASE> ctx
    ) implements ProtocolFactory<BUF, BASE>, PayloadHandlerProtocol<BUF, BASE> {
        @Override
        public PayloadHandlerFlow<BUF, BASE> flow(@Nullable PacketFlow flow) {
            return new HandlerFlow<>(key, ctx.flow(flow));
        }
    }

    private record Flow<BUF extends FriendlyByteBuf, BASE extends CustomPacketPayload>(BuilderContext<BUF, BASE> ctx) implements ProtocolFactory<BUF, BASE>, PayloadFlow<BUF, BASE> {
        @Override
        public <MSG extends BASE> Flow<BUF, BASE> add(Type<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, Context> consumer) {
            ctx.builder().add(new Message<>(ctx.builder().nextIndex(), type, ctx.protocol(), ctx.flow(), codec, consumer));
            return this;
        }

        @Override
        public PayloadFlow<BUF, BASE> flow(@Nullable PacketFlow flow) {
            return new Flow<>(ctx.flow(flow));
        }
    }

    private record HandlerFlow<BUF extends FriendlyByteBuf, CTX, BASE extends CustomPacketPayload & SimplePacket<CTX>>(
        AttributeKey<CTX> key,
        BuilderContext<BUF, BASE> ctx
    ) implements ProtocolFactory<BUF, BASE>, PayloadHandlerFlow<BUF, BASE> {
        @Override
        public <MSG extends BASE> PayloadHandlerFlow<BUF, BASE> add(Type<MSG> type, StreamCodec<BUF, MSG> codec) {
            return this.add(type, codec, (msg, ctx) -> {
                var inst = ctx.getConnection().channel().attr(key).get();
                ctx.setPacketHandled(msg.handle(inst, ctx));
            });
        }

        @Override
        public <MSG extends BASE> PayloadHandlerFlow<BUF, BASE> addMain(Type<MSG> type, StreamCodec<BUF, MSG> codec) {
            return this.add(type, codec, (msg, ctx) -> {
                ctx.enqueueWork(() -> {
                    var inst = ctx.getConnection().channel().attr(key).get();
                    msg.handle(inst, ctx);
                });
                ctx.setPacketHandled(true);
            });
        }

        @Override
        public <MSG extends BASE> PayloadHandlerFlow<BUF, BASE> add(Type<MSG> type, StreamCodec<BUF, MSG> codec, BiConsumer<MSG, Context> consumer) {
            ctx.builder().add(new Message<>(ctx.builder().nextIndex(), type, ctx.protocol(), ctx.flow(), codec, consumer));
            return this;
        }

        @Override
        public PayloadHandlerFlow<BUF, BASE> flow(@Nullable PacketFlow flow) {
            return new HandlerFlow<>(key, ctx.flow(flow));
        }
    }

    private interface ProtocolFactory<BUF extends FriendlyByteBuf, BASE extends CustomPacketPayload> extends PayloadConnection<BASE>, PayloadBuildable {
        BuilderContext<BUF, BASE> ctx();

        @Override
        default <NEWBUF extends FriendlyByteBuf, NEWBASE extends CustomPacketPayload> PayloadProtocol<NEWBUF, NEWBASE> protocol(NetworkProtocol<NEWBUF> protocol) {
            return ctx().builder.protocol(protocol);
        }

        @Override
        default <NEWBUF extends FriendlyByteBuf, CTX, NEWBASE extends SimplePacket<CTX> & CustomPacketPayload> PayloadHandlerProtocol<NEWBUF, NEWBASE> protocol(AttributeKey<CTX> context, NetworkProtocol<NEWBUF> protocol) {
            return ctx().builder.protocol(context, protocol);
        }

        @Override
        default PayloadChannel build() {
            return ctx().builder.build();
        }
    }

    /* ===================================================================================
     *                           INTERNAL BELOW THIS
     * ===================================================================================
     */

    private record Message<MSG extends CustomPacketPayload, BUF extends FriendlyByteBuf>(
        int index,
        Type<MSG> type,
        @Nullable
        NetworkProtocol<BUF> protocol,
        @Nullable
        PacketFlow direction,
        StreamCodec<BUF, MSG> codec,
        BiConsumer<MSG, CustomPayloadEvent.Context> consumer
    ){ };

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
            var error = "Illegal packet received, terminating connection. " + msg.type().id() + " expected " +
                direction.name() + " " + protocol.name() + " but was " +
                con.getReceiving().name() + " " + con.getProtocol().name();

            LOGGER.error(MARKER, error);
            con.disconnect(Component.literal(error));
            throw new IllegalStateException(error);
        }

        decodeAndDispatch(data, event.getSource(), msg);
    }

    // Yay generics
    private static <MSG extends CustomPacketPayload> void decodeAndDispatch(FriendlyByteBuf data, CustomPayloadEvent.Context ctx, Message<MSG, FriendlyByteBuf> msg) {
        var message = msg.codec.decode(data);
        msg.consumer.accept(message, ctx);
    }

    @Override
    protected Packet<?> toVanillaPacket(Connection con, CustomPacketPayload message) {
        var msg = byType.get(message.type());

        if (msg == null) {
            LOGGER.error(MARKER, "Attempted to send invalid message {} on channel {}", message.type().id(), getName());
            throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
        }

        var protocol = msg.protocol() == null ? con.getProtocol() : msg.protocol().toVanilla();
        var direction = msg.direction() == null ?  con.getSending() : msg.direction();

        if (protocol != con.getProtocol() || direction != con.getSending()) {
            var error = "Illegal packet sent, terminating connection. " + msg.type().id() + " expected " +
                direction.name() + " " + protocol.name() + " but was " +
                con.getSending().name() + " " + con.getProtocol().name();

            LOGGER.error(MARKER, error);
            con.disconnect(Component.literal(error));
            throw new IllegalStateException(error);
        }

        return super.toVanillaPacket(con, message);
    }

    @Override
    public void encode(FriendlyByteBuf out, CustomPacketPayload message) {
        @SuppressWarnings("unchecked")
        var msg = (Message<CustomPacketPayload, FriendlyByteBuf>)byType.get(message.type());

        if (msg == null) {
            LOGGER.error(MARKER, "Attempted to send invalid message {} on channel {}", message.getClass().getName(), getName());
            throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
        }

        out.writeVarInt(msg.index());
        msg.codec.encode(out, message);
    }
}
