package net.minecraftforge.network;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;

public abstract class TargetedNetworkFilter extends MessageToMessageEncoder<IPacket<?>>
{

    protected final Map<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>> handlers;

    protected TargetedNetworkFilter(Map<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>> handlers)
    {
        this.handlers = handlers;
    }

    /**
     * Helper function for building the handler map.
     */
    @Nonnull
    protected static <T extends IPacket<?>> Map.Entry<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>> handler(Class<T> cls, Function<T, ? extends IPacket<?>> function)
    {
        return handler(cls, (pkt, list) -> list.add(function.apply(cls.cast(pkt))));
    }

    /**
     * Helper function for building the handler map.
     */
    @Nonnull
    protected static <T extends IPacket<?>> Map.Entry<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>> handler(Class<T> cls, BiConsumer<IPacket<?>, List<? super IPacket<?>>> consumer)
    {
        return new AbstractMap.SimpleEntry<>(cls, consumer);
    }

    protected abstract boolean isNecessary(NetworkManager manager);

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket<?> msg, List<Object> out)
    {
        BiConsumer<IPacket<?>, List<? super IPacket<?>>> consumer = handlers.getOrDefault(msg.getClass(), (pkt, list) -> list.add(pkt));
        consumer.accept(msg, out);
    }

}
