/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.network.simple.handler.SimplePacket;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

final class PayloadChannel extends Channel<CustomPacketPayload> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("PAYLOAD_CHANNEL");
    private final Map<ResourceLocation, Message<?, ?>> payloads;

    @ApiStatus.Internal
    private PayloadChannel(Builder builder) {
        super(builder.instance);
        builder.instance.addListener(this::onPacketReceived);
        this.payloads = Collections.unmodifiableMap(builder.payloads);
    }

    static PayloadConnection<CustomPacketPayload> builder(NetworkInstance instance) {
        return new Builder(instance);
    }

    private final static class Builder implements PayloadConnection<CustomPacketPayload> {
        private final NetworkInstance instance;
        private boolean built = false;
        private final Map<ResourceLocation, Message<?, ?>> payloads = new HashMap<>();

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

        private void add(Message<?, ?> msg) {
            if (this.payloads.put(msg.type().id(), msg) != null)
                throw new IllegalArgumentException("Packet with type " + msg.type().id() + " already registered");

            this.instance.addChild(msg.type().id());
        }

        private void checkBuilt() {
            if (built)
                throw new IllegalStateException("PayloadChannel builder is fully built, can not modify it any more");
        }

        public PayloadChannel build() {
            checkBuilt();
            return new PayloadChannel(this);
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
                ctx.builder().add(new Message<>(type, ctx.protocol(), ctx.flow(), codec, consumer));
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
                ctx.builder().add(new Message<>(type, ctx.protocol(), ctx.flow(), codec, consumer));
                return this;
            }

            @Override
            public PayloadHandlerFlow<BUF, BASE> flow(@Nullable PacketFlow flow) {
                return new HandlerFlow<>(key, ctx.flow(flow));
            }
        }

        private interface ProtocolFactory<BUF extends FriendlyByteBuf, BASE extends CustomPacketPayload> extends PayloadConnection<BASE>, ChannelBuildable<CustomPacketPayload> {
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
            default Channel<CustomPacketPayload> build() {
                return ctx().builder.build();
            }
        }
    }

    private record Message<MSG extends CustomPacketPayload, BUF extends FriendlyByteBuf>(
        Type<MSG> type,
        @Nullable
        NetworkProtocol<BUF> protocol,
        @Nullable
        PacketFlow direction,
        StreamCodec<BUF, MSG> codec,
        BiConsumer<MSG, CustomPayloadEvent.Context> consumer
    ){ };

    private Message<CustomPacketPayload, FriendlyByteBuf> get(ResourceLocation type, boolean send) {
        @SuppressWarnings("unchecked")
        var msg = (Message<CustomPacketPayload, FriendlyByteBuf>)payloads.get(type);

        if (msg == null)
            error((send ? "Attemped to send" : "Received") + " invalid message " + type + " on channel " + getName());

        return msg;
    }

    private void error(String message) {
        LOGGER.error(MARKER, message);
        throw new IllegalArgumentException(message);
    }

    private void onPacketReceived(CustomPayloadEvent event) {
        var data = event.getPayload();
        if (data == null)
            error("Received empty payload on channel " + getName() + " login index " + event.getLoginIndex() + " for payload type " + event.getChannel());

        var msg = get(event.getChannel(), false);
        this.validate(event.getChannel(), event.getSource().getConnection(), msg.protocol(), msg.direction(), false);

        var pkt = msg.codec().decode(data);
        msg.consumer().accept(pkt, event.getSource());
    }

    @Override
    protected Packet<?> toVanillaPacket(Connection con, CustomPacketPayload message) {
        var msg = get(message.type().id(), true);
        this.validate(message.type().id(), con, msg.protocol(), msg.direction(), true);
        return super.toVanillaPacket(con, message);
    }

    @Override
    ResourceLocation getName(CustomPacketPayload packet) {
        return packet.type().id();
    }

    @Override
    public void encode(FriendlyByteBuf out, CustomPacketPayload message) {
        var msg = get(message.type().id(), true);
        msg.codec().encode(out, message);
    }
}
