/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fmllegacy.network.simple;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.fmllegacy.network.NetworkInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IndexedMessageCodec
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SIMPLENET = MarkerManager.getMarker("SIMPLENET");
    private final Short2ObjectArrayMap<MessageHandler<?>> indicies = new Short2ObjectArrayMap<>();
    private final Object2ObjectArrayMap<Class<?>, MessageHandler<?>> types = new Object2ObjectArrayMap<>();
    private final NetworkInstance networkInstance;

    public IndexedMessageCodec() {
        this(null);
    }
    public IndexedMessageCodec(final NetworkInstance instance) {
        this.networkInstance = instance;
    }

    @SuppressWarnings("unchecked")
    public <MSG> MessageHandler<MSG> findMessageType(final MSG msgToReply) {
        return (MessageHandler<MSG>) types.get(msgToReply.getClass());
    }

    @SuppressWarnings("unchecked")
    <MSG> MessageHandler<MSG> findIndex(final short i) {
        return (MessageHandler<MSG>) indicies.get(i);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    class MessageHandler<MSG>
    {
        private final Optional<BiConsumer<MSG, FriendlyByteBuf>> encoder;
        private final Optional<Function<FriendlyByteBuf, MSG>> decoder;
        private final int index;
        private final BiConsumer<MSG,Supplier<NetworkEvent.Context>> messageConsumer;
        private final Class<MSG> messageType;
        private final Optional<NetworkDirection> networkDirection;
        private Optional<BiConsumer<MSG, Integer>> loginIndexSetter;
        private Optional<Function<MSG, Integer>> loginIndexGetter;

        public MessageHandler(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, final Optional<NetworkDirection> networkDirection)
        {
            this.index = index;
            this.messageType = messageType;
            this.encoder = Optional.ofNullable(encoder);
            this.decoder = Optional.ofNullable(decoder);
            this.messageConsumer = messageConsumer;
            this.networkDirection = networkDirection;
            this.loginIndexGetter = Optional.empty();
            this.loginIndexSetter = Optional.empty();
            indicies.put((short)(index & 0xff), this);
            types.put(messageType, this);
        }

        void setLoginIndexSetter(BiConsumer<MSG, Integer> loginIndexSetter)
        {
            this.loginIndexSetter = Optional.of(loginIndexSetter);
        }

        Optional<BiConsumer<MSG, Integer>> getLoginIndexSetter() {
            return this.loginIndexSetter;
        }

        void setLoginIndexGetter(Function<MSG, Integer> loginIndexGetter) {
            this.loginIndexGetter = Optional.of(loginIndexGetter);
        }

        public Optional<Function<MSG, Integer>> getLoginIndexGetter() {
            return this.loginIndexGetter;
        }

        MSG newInstance() {
            try {
                return messageType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Invalid login message", e);
                throw new RuntimeException(e);
            }
        }
    }

    private static <M> void tryDecode(FriendlyByteBuf payload, Supplier<NetworkEvent.Context> context, int payloadIndex, MessageHandler<M> codec)
    {
        codec.decoder.map(d->d.apply(payload)).
                map(p->{
                    // Only run the loginIndex function for payloadIndexed packets (login)
                    if (payloadIndex != Integer.MIN_VALUE)
                    {
                        codec.getLoginIndexSetter().ifPresent(f-> f.accept(p, payloadIndex));
                    }
                    return p;
                }).ifPresent(m->codec.messageConsumer.accept(m, context));
    }

    private static <M> int tryEncode(FriendlyByteBuf target, M message, MessageHandler<M> codec) {
        codec.encoder.ifPresent(encoder->{
            target.writeByte(codec.index & 0xff);
            encoder.accept(message, target);
        });
        return codec.loginIndexGetter.orElse(m -> Integer.MIN_VALUE).apply(message);
    }

    public <MSG> int build(MSG message, FriendlyByteBuf target)
    {
        @SuppressWarnings("unchecked")
        MessageHandler<MSG> messageHandler = (MessageHandler<MSG>)types.get(message.getClass());
        if (messageHandler == null) {
            LOGGER.error(SIMPLENET, "Received invalid message {} on channel {}", message.getClass().getName(), Optional.ofNullable(networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
            throw new IllegalArgumentException("Invalid message "+message.getClass().getName());
        }
        return tryEncode(target, message, messageHandler);
    }

    void consume(FriendlyByteBuf payload, int payloadIndex, Supplier<NetworkEvent.Context> context) {
        if (payload == null) {
            LOGGER.error(SIMPLENET, "Received empty payload on channel {}", Optional.ofNullable(networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
            return;
        }
        short discriminator = payload.readUnsignedByte();
        final MessageHandler<?> messageHandler = indicies.get(discriminator);
        if (messageHandler == null) {
            LOGGER.error(SIMPLENET, "Received invalid discriminator byte {} on channel {}", discriminator, Optional.ofNullable(networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
            return;
        }
        NetworkHooks.validatePacketDirection(context.get().getDirection(), messageHandler.networkDirection, context.get().getNetworkManager());
        tryDecode(payload, context, payloadIndex, messageHandler);
    }

    <MSG> MessageHandler<MSG> addCodecIndex(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, final Optional<NetworkDirection> networkDirection) {
        return new MessageHandler<>(index, messageType, encoder, decoder, messageConsumer, networkDirection);
    }
}
