/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.network.simple;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleChannel
{
    private final NetworkInstance instance;
    private final IndexedMessageCodec indexedCodec;
    private List<Function<Boolean, ? extends List<? extends Pair<String,?>>>> loginPackets;

    public SimpleChannel(NetworkInstance instance)
    {
        this.instance = instance;
        this.indexedCodec = new IndexedMessageCodec();
        this.loginPackets = new ArrayList<>();
        instance.addListener(this::networkEventListener);
        instance.addGatherListener(this::networkLoginGather);
    }

    private void networkLoginGather(final NetworkEvent.GatherLoginPayloadsEvent gatherEvent) {
        loginPackets.forEach(packetGenerator->{
            packetGenerator.apply(gatherEvent.isLocal()).forEach(p->{
                PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
                this.indexedCodec.build(p.getRight(), pb);
                gatherEvent.add(pb, this.instance.getChannelName(), p.getLeft());
            });
        });
    }
    private void networkEventListener(final NetworkEvent networkEvent)
    {
        this.indexedCodec.consume(networkEvent.getPayload(), networkEvent.getLoginIndex(), networkEvent.getSource());
    }

    public <MSG> int encodeMessage(MSG message, final PacketBuffer target) {
        return this.indexedCodec.build(message, target);
    }
    public <MSG> IndexedMessageCodec.MessageHandler<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return this.indexedCodec.addCodecIndex(index, messageType, encoder, decoder, messageConsumer);
    }

    private <MSG> Pair<PacketBuffer,Integer> toBuffer(MSG msg) {
        final PacketBuffer bufIn = new PacketBuffer(Unpooled.buffer());
        int index = encodeMessage(msg, bufIn);
        return Pair.of(bufIn, index);
    }

    public <MSG> void sendToServer(MSG message)
    {
        sendTo(message, Minecraft.getInstance().getConnection().getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
    }

    public <MSG> void sendTo(MSG message, NetworkManager manager, NetworkDirection direction)
    {
        manager.sendPacket(toVanillaPacket(message, direction));
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

    public <MSG> IPacket<?> toVanillaPacket(MSG message, NetworkDirection direction)
    {
        return direction.buildPacket(toBuffer(message), instance.getChannelName()).getThis();
    }

    public <MSG> void reply(MSG msgToReply, NetworkEvent.Context context)
    {
        context.getPacketDispatcher().sendPacket(instance.getChannelName(), toBuffer(msgToReply).getLeft());
    }

    public <M> MessageBuilder<M> messageBuilder(final Class<M> type, int id) {
        return MessageBuilder.forType(this, type, id);
    }

    public static class MessageBuilder<MSG>  {
        private SimpleChannel channel;
        private Class<MSG> type;
        private int id;
        private BiConsumer<MSG, PacketBuffer> encoder;
        private Function<PacketBuffer, MSG> decoder;
        private BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer;
        private Function<MSG, Integer> loginIndexGetter;
        private BiConsumer<MSG, Integer> loginIndexSetter;
        private Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators;

        private static <MSG> MessageBuilder<MSG> forType(final SimpleChannel channel, final Class<MSG> type, int id) {
            MessageBuilder<MSG> builder = new MessageBuilder<>();
            builder.channel = channel;
            builder.id = id;
            builder.type = type;
            return builder;
        }

        public MessageBuilder<MSG> encoder(BiConsumer<MSG, PacketBuffer> encoder) {
            this.encoder = encoder;
            return this;
        }

        public MessageBuilder<MSG> decoder(Function<PacketBuffer, MSG> decoder) {
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

        public void add() {
            final IndexedMessageCodec.MessageHandler<MSG> message = this.channel.registerMessage(this.id, this.type, this.encoder, this.decoder, this.consumer);
            if (this.loginIndexSetter != null) {
                message.setLoginIndexSetter(this.loginIndexSetter);
            }
            if (this.loginIndexGetter != null) {
                message.setLoginIndexGetter(this.loginIndexGetter);
            }
            if (this.loginPacketGenerators != null) {
                this.channel.loginPackets.add(this.loginPacketGenerators);
            }
        }
    }
}
