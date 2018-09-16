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

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class IndexedMessageCodec
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SIMPLENET = MarkerManager.getMarker("SIMPLENET");
    private final Short2ObjectArrayMap<CodecIndex<?>> indicies = new Short2ObjectArrayMap<>();
    private final Object2ObjectArrayMap<Class<?>, CodecIndex<?>> types = new Object2ObjectArrayMap<>();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public class CodecIndex<MSG>
    {

        private final Optional<BiConsumer<MSG, PacketBuffer>> encoder;
        private final Optional<Function<PacketBuffer, MSG>> decoder;
        private final int index;
        private final BiConsumer<MSG,Supplier<NetworkEvent.Context>> messageConsumer;
        private final Class<MSG> messageType;
        private Optional<BiConsumer<MSG, Integer>> loginIndexFunction;

        public CodecIndex(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer)
        {
            this.index = index;
            this.messageType = messageType;
            this.encoder = Optional.ofNullable(encoder);
            this.decoder = Optional.ofNullable(decoder);
            this.messageConsumer = messageConsumer;
            this.loginIndexFunction = Optional.empty();
            indicies.put((short)(index & 0xff), this);
            types.put(messageType, this);
        }

        public void setLoginIndexFunction(BiConsumer<MSG, Integer> loginIndexFunction)
        {
            this.loginIndexFunction = Optional.of(loginIndexFunction);
        }

        public Optional<BiConsumer<MSG, Integer>> getLoginIndexFunction() {
            return this.loginIndexFunction;
        }
    }
    private static <M> void tryDecode(PacketBuffer payload, Supplier<NetworkEvent.Context> context, CodecIndex<M> codec)
    {
        codec.decoder.map(d->d.apply(payload)).ifPresent(m->codec.messageConsumer.accept(m, context));
    }

    private static <M> void tryDecode(PacketBuffer payload, Supplier<NetworkEvent.Context> context, int payloadIndex, CodecIndex<M> codec)
    {
        codec.decoder.map(d->d.apply(payload)).
                map(p->{ codec.getLoginIndexFunction().ifPresent(f-> f.accept(p, payloadIndex)); return p; }).
                ifPresent(m->codec.messageConsumer.accept(m, context));
    }

    private static <M> void tryEncode(PacketBuffer target, M message, CodecIndex<M> codec) {
        codec.encoder.ifPresent(encoder->{
            target.writeByte(codec.index & 0xff);
            encoder.accept(message, target);
        });
    }

    public <MSG> void build(MSG message, PacketBuffer target)
    {
        @SuppressWarnings("unchecked")
        CodecIndex<MSG> codecIndex = (CodecIndex<MSG>)types.get(message.getClass());
        if (codecIndex == null) {
            LOGGER.error(SIMPLENET, "Received invalid message {}", message.getClass().getName());
            throw new IllegalArgumentException("Invalid message "+message.getClass().getName());
        }
        tryEncode(target, message, codecIndex);
    }

    void consume(PacketBuffer payload, int payloadIndex, Supplier<NetworkEvent.Context> context) {
        if (payload == null) {
            LOGGER.error(SIMPLENET, "Received empty payload");
            return;
        }
        short discriminator = payload.readUnsignedByte();
        final CodecIndex<?> codecIndex = indicies.get(discriminator);
        if (codecIndex == null) {
            LOGGER.error(SIMPLENET, "Received invalid discriminator byte {}", discriminator);
            return;
        }
        tryDecode(payload, context, payloadIndex, codecIndex);
    }

    void consume(PacketBuffer payload, Supplier<NetworkEvent.Context> context) {
        // no data in empty payload
        if (payload == null) {
            LOGGER.error(SIMPLENET, "Received empty payload");
            return;
        }
        short discriminator = payload.readUnsignedByte();
        final CodecIndex<?> codecIndex = indicies.get(discriminator);
        if (codecIndex == null) {
            LOGGER.error(SIMPLENET, "Received invalid discriminator byte {}", discriminator);
            return;
        }
        tryDecode(payload, context, codecIndex);
    }

    <MSG> CodecIndex<MSG> addCodecIndex(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return new CodecIndex<>(index, messageType, encoder, decoder, messageConsumer);
    }
}
