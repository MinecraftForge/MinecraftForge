package net.minecraftforge.fml.common.network;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import org.apache.logging.log4j.Level;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Use this handler as the only thing in your channel, to receive network events
 * whenever your channel receives a message.
 * Note: it will not forward on to other handlers.
 *
 * @author cpw
 *
 */
@Sharable
public class NetworkEventFiringHandler extends SimpleChannelInboundHandler<FMLProxyPacket> {
    private FMLEventChannel eventChannel;

    NetworkEventFiringHandler(FMLEventChannel fmlEventChannel)
    {
        this.eventChannel = fmlEventChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket msg) throws Exception
    {
        eventChannel.fireRead(msg,ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        eventChannel.fireUserEvent(evt,ctx);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "NetworkEventFiringHandler exception");
        super.exceptionCaught(ctx, cause);
    }
}
