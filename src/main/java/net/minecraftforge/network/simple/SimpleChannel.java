/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class SimpleChannel
{
    private final NetworkInstance instance;
    private final IndexedMessageCodec indexedCodec;
    private final Optional<Consumer<NetworkEvent.ChannelRegistrationChangeEvent>> registryChangeConsumer;
    private List<Function<Boolean, ? extends List<? extends Pair<String,?>>>> loginPackets;

    public SimpleChannel(NetworkInstance instance) {
        this(instance, Optional.empty());
    }

    private SimpleChannel(NetworkInstance instance, Optional<Consumer<NetworkEvent.ChannelRegistrationChangeEvent>> registryChangeNotify) {
        this.instance = instance;
        this.indexedCodec = new IndexedMessageCodec(instance);
        this.loginPackets = new ArrayList<>();
        instance.addListener(this::networkEventListener);
        instance.addGatherListener(this::networkLoginGather);
        this.registryChangeConsumer = registryChangeNotify;
    }

    public SimpleChannel(NetworkInstance instance, Consumer<NetworkEvent.ChannelRegistrationChangeEvent> registryChangeNotify) {
        this(instance, Optional.of(registryChangeNotify));
    }

    private void networkLoginGather(final NetworkEvent.GatherLoginPayloadsEvent gatherEvent) {
        loginPackets.forEach(packetGenerator->{
            packetGenerator.apply(gatherEvent.isLocal()).forEach(p->{
                FriendlyByteBuf pb = new FriendlyByteBuf(Unpooled.buffer());
                this.indexedCodec.build(p.getRight(), pb);
                gatherEvent.add(pb, this.instance.getChannelName(), p.getLeft());
            });
        });
    }
    private void networkEventListener(final NetworkEvent networkEvent)
    {
        if (networkEvent instanceof NetworkEvent.ChannelRegistrationChangeEvent) {
            this.registryChangeConsumer.ifPresent(l->l.accept(((NetworkEvent.ChannelRegistrationChangeEvent) networkEvent)));
        } else {
            this.indexedCodec.consume(networkEvent.getPayload(), networkEvent.getLoginIndex(), networkEvent.getSource());
        }
    }

    public <MSG> int encodeMessage(MSG message, final FriendlyByteBuf target) {
        return this.indexedCodec.build(message, target);
    }

    public <MSG> IndexedMessageCodec.MessageHandler<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return registerMessage(index, messageType, encoder, decoder, messageConsumer, Optional.empty());
    }

    public <MSG> IndexedMessageCodec.MessageHandler<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, final Optional<NetworkDirection> networkDirection) {
        return this.indexedCodec.addCodecIndex(index, messageType, encoder, decoder, messageConsumer, networkDirection);
    }

    private <MSG> Pair<FriendlyByteBuf,Integer> toBuffer(MSG msg) {
        final FriendlyByteBuf bufIn = new FriendlyByteBuf(Unpooled.buffer());
        int index = encodeMessage(msg, bufIn);
        return Pair.of(bufIn, index);
    }

    public <MSG> void sendToServer(MSG message)
    {
        sendTo(message, Minecraft.getInstance().getConnection().getConnection(), NetworkDirection.PLAY_TO_SERVER);
    }

    public <MSG> void sendTo(MSG message, Connection manager, NetworkDirection direction)
    {
        manager.send(toVanillaPacket(message, direction));
    }

    /**
     * Send a message to the {@link PacketDistributor.PacketTarget} from a {@link PacketDistributor} instance.
     *
     * <pre>
     *     channel.send(PacketDistributor.PLAYER.with(()->player), message)
     * </pre>
     *
     * @param target The curried target from a PacketDistributor
     * @param message The message to send
     * @param <MSG> The type of the message
     */
    public <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
        target.send(toVanillaPacket(message, target.getDirection()));
    }

    public <MSG> Packet<?> toVanillaPacket(MSG message, NetworkDirection direction)
    {
        return direction.buildPacket(toBuffer(message), instance.getChannelName()).getThis();
    }

    public <MSG> void reply(MSG msgToReply, NetworkEvent.Context context)
    {
        context.getPacketDispatcher().sendPacket(instance.getChannelName(), toBuffer(msgToReply).getLeft());
    }

    /**
     * Returns true if the channel is present in the given connection.
     */
    public boolean isRemotePresent(Connection manager) {
        return instance.isRemotePresent(manager);
    }

    /**
     * Build a new MessageBuilder. The type should implement {@link java.util.function.IntSupplier} if it is a login
     * packet.
     * @param type Type of message
     * @param id id in the indexed codec
     * @param <M> Type of type
     * @return a MessageBuilder
     */
    public <M> MessageBuilder<M> messageBuilder(final Class<M> type, int id) {
        return MessageBuilder.forType(this, type, id, null);
    }

    /**
     * Build a new MessageBuilder. The type should implement {@link java.util.function.IntSupplier} if it is a login
     * packet.
     * @param type Type of message
     * @param id id in the indexed codec
     * @param direction a impl direction which will be asserted before any processing of this message occurs. Use to
     *                  enforce strict sided handling to prevent spoofing.
     * @param <M> Type of type
     * @return a MessageBuilder
     */
    public <M> MessageBuilder<M> messageBuilder(final Class<M> type, int id, NetworkDirection direction) {
        return MessageBuilder.forType(this, type, id, direction);
    }

    public static class MessageBuilder<MSG>  {
        private SimpleChannel channel;
        private Class<MSG> type;
        private int id;
        private BiConsumer<MSG, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, MSG> decoder;
        private BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer;
        private Function<MSG, Integer> loginIndexGetter;
        private BiConsumer<MSG, Integer> loginIndexSetter;
        private Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators;
        private Optional<NetworkDirection> networkDirection;

        private static <MSG> MessageBuilder<MSG> forType(final SimpleChannel channel, final Class<MSG> type, int id, NetworkDirection networkDirection) {
            MessageBuilder<MSG> builder = new MessageBuilder<>();
            builder.channel = channel;
            builder.id = id;
            builder.type = type;
            builder.networkDirection = Optional.ofNullable(networkDirection);
            return builder;
        }

        public MessageBuilder<MSG> encoder(BiConsumer<MSG, FriendlyByteBuf> encoder) {
            this.encoder = encoder;
            return this;
        }

        public MessageBuilder<MSG> decoder(Function<FriendlyByteBuf, MSG> decoder) {
            this.decoder = decoder;
            return this;
        }

        public MessageBuilder<MSG> loginIndex(Function<MSG, Integer> loginIndexGetter, BiConsumer<MSG, Integer> loginIndexSetter) {
            this.loginIndexGetter = loginIndexGetter;
            this.loginIndexSetter = loginIndexSetter;
            return this;
        }

        public MessageBuilder<MSG> buildLoginPacketList(Function<Boolean, List<Pair<String,MSG>>> loginPacketGenerators) {
            this.loginPacketGenerators = loginPacketGenerators;
            return this;
        }

        public MessageBuilder<MSG> markAsLoginPacket()
        {
            this.loginPacketGenerators = (isLocal) -> {
                try {
                    return Collections.singletonList(Pair.of(type.getName(), type.newInstance()));
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("Inaccessible no-arg constructor for message "+type.getName(), e);
                }
            };
            return this;
        }

        public MessageBuilder<MSG> consumer(BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
            this.consumer = consumer;
            return this;
        }

        public interface ToBooleanBiFunction<T, U> {
            boolean applyAsBool(T first, U second);
        }

        /**
         * Function returning a boolean "packet handled" indication, for simpler channel building.
         * @param handler a handler
         * @return this
         */
        public MessageBuilder<MSG> consumer(ToBooleanBiFunction<MSG, Supplier<NetworkEvent.Context>> handler) {
            this.consumer = (msg, ctx) -> {
                boolean handled = handler.applyAsBool(msg, ctx);
                ctx.get().setPacketHandled(handled);
            };
            return this;
        }

        public void add() {
            final IndexedMessageCodec.MessageHandler<MSG> message = this.channel.registerMessage(this.id, this.type, this.encoder, this.decoder, this.consumer, this.networkDirection);
            if (this.loginIndexSetter != null) {
                message.setLoginIndexSetter(this.loginIndexSetter);
            }
            if (this.loginIndexGetter != null) {
                if (!IntSupplier.class.isAssignableFrom(this.type)) {
                    throw new IllegalArgumentException("Login packet type that does not supply an index as an IntSupplier");
                }
                message.setLoginIndexGetter(this.loginIndexGetter);
            }
            if (this.loginPacketGenerators != null) {
                this.channel.loginPackets.add(this.loginPacketGenerators);
            }
        }
    }
}
