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

/**
 * A filter for vanilla network packets.
 */
public abstract class VanillaPacketFilter extends MessageToMessageEncoder<IPacket<?>>
{

    protected final Map<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>> handlers;

    protected VanillaPacketFilter(Map<Class<? extends IPacket<?>>, BiConsumer<IPacket<?>, List<? super IPacket<?>>>> handlers)
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

    /**
     * Whether this filter is necessary on the given connection.
     */
    protected abstract boolean isNecessary(NetworkManager manager);

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket<?> msg, List<Object> out)
    {
        BiConsumer<IPacket<?>, List<? super IPacket<?>>> consumer = handlers.getOrDefault(msg.getClass(), (pkt, list) -> list.add(pkt));
        consumer.accept(msg, out);
    }

}
