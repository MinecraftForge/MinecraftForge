/*
 * Minecraft Forge
 * Copyright (c) 2018.
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
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkInstance;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class SimpleChannel
{
    private final NetworkInstance instance;
    private final IndexedMessageCodec indexedCodec;

    public SimpleChannel(NetworkInstance instance)
    {
        this.instance = instance;
        this.indexedCodec = new IndexedMessageCodec();
        instance.addListener(this::networkEventListener);
    }

    private void networkEventListener(final NetworkEvent networkEvent)
    {
        if (networkEvent instanceof NetworkEvent.ILoginIndex)
        {
            this.indexedCodec.consume(networkEvent.getPayload(), ((NetworkEvent.ILoginIndex)networkEvent).getIndex(), networkEvent.getSource());
        }
        else
        {
            this.indexedCodec.consume(networkEvent.getPayload(), networkEvent.getSource());
        }
    }

    public <MSG> void encodeMessage(MSG message, final PacketBuffer target) {
        this.indexedCodec.build(message, target);
    }
    public <MSG> IndexedMessageCodec.CodecIndex<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return this.indexedCodec.addCodecIndex(index, messageType, encoder, decoder, messageConsumer);
    }

    private <MSG> PacketBuffer toBuffer(MSG msg) {
        final PacketBuffer bufIn = new PacketBuffer(Unpooled.buffer());
        encodeMessage(msg, bufIn);
        return bufIn;
    }
    public <MSG> void sendToServer(MSG message)
    {
        sendTo(message, Minecraft.getMinecraft().getConnection().getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
    }

    public <MSG> void sendTo(MSG message, NetworkManager manager, NetworkDirection direction) {
        ICustomPacket<Packet<?>> payload = direction.buildPacket(toBuffer(message), instance.getChannelName(), -1);
        manager.sendPacket(payload.getThis());
    }

    public <MSG> void sendLogin(MSG message, NetworkManager manager, NetworkDirection direction, int packetIndex) {
        ICustomPacket<Packet<?>> payload = direction.buildPacket(toBuffer(message), instance.getChannelName(), packetIndex);
        manager.sendPacket(payload.getThis());
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
        private BiConsumer<MSG, Integer> loginIndexFunction;

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

        public MessageBuilder<MSG> loginIndex(BiConsumer<MSG, Integer> loginIndexFunction) {
            this.loginIndexFunction = loginIndexFunction;
            return this;
        }
        public MessageBuilder<MSG> consumer(BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
            this.consumer = consumer;
            return this;
        }

        public void add() {
            final IndexedMessageCodec.CodecIndex<MSG> message = this.channel.registerMessage(this.id, this.type, this.encoder, this.decoder, this.consumer);
            if (this.loginIndexFunction != null) {
                message.setLoginIndexFunction(this.loginIndexFunction);
            }
        }
    }
}
