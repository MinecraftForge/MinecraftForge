/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Iterator;
import java.util.List;


import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketLoggingHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static void register(Connection manager)
    {
        ChannelPipeline pipeline = manager.channel().pipeline();
        final PacketFlow direction = manager.getDirection();
        if (manager.isMemoryConnection())
        {
            pipeline.addBefore("packet_handler", "splitter", new SimpleChannelInboundHandler<Packet<?>>()
            {
                String prefix = (direction == PacketFlow.SERVERBOUND ? "SERVER: C->S" : "CLIENT: S->C");
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, Packet<?> msg) throws Exception
                {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    msg.write(buf);
                    LOGGER.debug("{} {}:\n{}", prefix, msg.getClass().getSimpleName(), ByteBufUtils.getContentDump(buf));
                    ctx.fireChannelRead(msg);
                }
            });
            pipeline.addBefore("splitter", "prepender", new ChannelOutboundHandlerAdapter()
            {
                String prefix = (direction == PacketFlow.SERVERBOUND ? "SERVER: S->C" : "CLIENT: C->S");
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
                {
                    if (msg instanceof Packet<?>)
                    {
                        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                        ((Packet<?>)msg).write(buf);
                        LOGGER.debug("{} {}:\n{}", prefix, msg.getClass().getSimpleName(), ByteBufUtils.getContentDump(buf));
                    }
                    ctx.write(msg, promise);
                }
            });
        }
        else
        {
            pipeline.replace("splitter", "splitter", new Varint21FrameDecoder()
            {
                String prefix = (direction == PacketFlow.SERVERBOUND ? "SERVER: C->S" : "CLIENT: S->C");
                @Override
                protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output)
                {
                    super.decode(context, input, output);
                    Iterator<Object> itr = output.iterator();
                    while (itr.hasNext())
                    {
                        ByteBuf pkt = (ByteBuf)itr.next();
                        pkt.markReaderIndex();
                        LOGGER.debug("{}:\n{}", prefix, ByteBufUtils.getContentDump(pkt));
                        pkt.resetReaderIndex();
                    }
                }
            });
            pipeline.replace("prepender", "prepender", new Varint21LengthFieldPrepender()
            {
                String prefix = (direction == PacketFlow.SERVERBOUND ? "SERVER: S->C" : "CLIENT: C->S");
                @Override
                protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output)
                {
                    input.markReaderIndex();
                    LOGGER.debug("{}:\n{}", prefix, ByteBufUtils.getContentDump(input));
                    input.resetReaderIndex();
                    super.encode(context, input, output);
                }
            });
        }
    }
}
