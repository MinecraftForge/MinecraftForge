package net.minecraftforge.fml.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer2;
import net.minecraftforge.fml.common.FMLLog;

public class PacketLoggingHandler
{
    @SuppressWarnings("rawtypes")
	public static void register(NetworkManager manager)
    {
        ChannelPipeline pipeline = manager.channel().pipeline();
        final EnumPacketDirection direction = manager.getDirection();
        if (manager.isLocalChannel())
        {
            pipeline.addBefore("packet_handler", "splitter", new SimpleChannelInboundHandler<Packet>()
            {
                String prefix = (direction == EnumPacketDirection.SERVERBOUND ? "SERVER: C->S" : "CLIENT: S->C");
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception
                {
                    PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                    msg.writePacketData(buf);
                    FMLLog.log(Level.DEBUG, "%s %s:\n%s", prefix, msg.getClass().getSimpleName(), ByteBufUtils.getContentDump(buf));
                    ctx.fireChannelRead(msg);
                }
            });
            pipeline.addBefore("splitter", "prepender", new ChannelOutboundHandlerAdapter()
            {
                String prefix = (direction == EnumPacketDirection.SERVERBOUND ? "SERVER: S->C" : "CLIENT: C->S");
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
                {
                    if (msg instanceof Packet)
                    {
                        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                        ((Packet)msg).writePacketData(buf);
                        FMLLog.log(Level.DEBUG, "%s %s:\n%s", prefix, msg.getClass().getSimpleName(), ByteBufUtils.getContentDump(buf));
                    }
                    ctx.write(msg, promise);
                }
            });
        }
        else
        {
            pipeline.replace("splitter", "splitter", new MessageDeserializer2()
            {
                String prefix = (direction == EnumPacketDirection.SERVERBOUND ? "SERVER: C->S" : "CLIENT: S->C");
                @Override
                protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) throws Exception
                {
                    super.decode(context, input, output);
                    Iterator<Object> itr = output.iterator();
                    while (itr.hasNext())
                    {
                        ByteBuf pkt = (ByteBuf)itr.next();
                        pkt.markReaderIndex();
                        FMLLog.log(Level.DEBUG, "%s:\n%s", prefix, ByteBufUtils.getContentDump(pkt));
                        pkt.resetReaderIndex();
                    }
                }
            });
            pipeline.replace("prepender", "prepender", new MessageSerializer2()
            {
                String prefix = (direction == EnumPacketDirection.SERVERBOUND ? "SERVER: S->C" : "CLIENT: C->S");
                @Override
                protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) throws Exception
                {
                    input.markReaderIndex();
                    FMLLog.log(Level.DEBUG, "%s:\n%s", prefix, ByteBufUtils.getContentDump(input));
                    input.resetReaderIndex();
                    super.encode(context, input, output);
                }
            });
        }
    }
}
