/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.filters;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.NotNull;

/**
 * A filter for vanilla impl packets.
 */
public abstract class VanillaPacketFilter extends MessageToMessageEncoder<Packet<?>>
{

    protected final Map<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> handlers;

    protected VanillaPacketFilter(Map<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> handlers)
    {
        this.handlers = handlers;
    }

    /**
     * Helper function for building the handler map.
     */
    @NotNull
    protected static <T extends Packet<?>> Map.Entry<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> handler(Class<T> cls, Function<T, ? extends Packet<?>> function)
    {
        return handler(cls, (pkt, list) -> list.add(function.apply(cls.cast(pkt))));
    }

    /**
     * Helper function for building the handler map.
     */
    @NotNull
    protected static <T extends Packet<?>> Map.Entry<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> handler(Class<T> cls, BiConsumer<Packet<?>, List<? super Packet<?>>> consumer)
    {
        return new AbstractMap.SimpleEntry<>(cls, consumer);
    }

    /**
     * Whether this filter is necessary on the given connection.
     */
    protected abstract boolean isNecessary(Connection manager);

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> msg, List<Object> out)
    {
        BiConsumer<Packet<?>, List<? super Packet<?>>> consumer = handlers.getOrDefault(msg.getClass(), (pkt, list) -> list.add(pkt));
        consumer.accept(msg, out);
    }

}
