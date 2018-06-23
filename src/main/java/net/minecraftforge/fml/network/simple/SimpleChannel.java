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
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkInstance;

import java.util.function.BiConsumer;
import java.util.function.Function;
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
        this.indexedCodec.consume(networkEvent.getPayload(),networkEvent.getSource());
    }

    public <MSG> void encodeMessage(MSG message, final PacketBuffer target) {
        this.indexedCodec.build(message, target);
    }
    public <MSG> void registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        this.indexedCodec.addCodecIndex(index, messageType, encoder, decoder, messageConsumer);
    }

    public <MSG> void sendToServer(MSG message)
    {
        final PacketBuffer bufIn = new PacketBuffer(Unpooled.buffer());
        encodeMessage(message, bufIn);
        final CPacketCustomPayload payload = new CPacketCustomPayload(instance.getChannelName(), bufIn);
        Minecraft.getMinecraft().getConnection().sendPacket(payload);
    }

    public static class MessageBuilder<MSG>  {
        private SimpleChannel channel;
        private Class<MSG> type;
        private int id;
        private BiConsumer<MSG, PacketBuffer> encoder;
        private Function<PacketBuffer, MSG> decoder;
        private BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer;

        public static <MSG> MessageBuilder<MSG> forType(final SimpleChannel channel, final Class<MSG> type, int id) {
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

        public MessageBuilder<MSG> consumer(BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
            this.consumer = consumer;
            return this;
        }

        public void add() {
            this.channel.registerMessage(this.id, this.type, this.encoder, this.decoder, this.consumer);
        }
    }
}
