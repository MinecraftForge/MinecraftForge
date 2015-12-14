package net.minecraftforge.fml.common.network.internal;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLMessage.CompleteHandshake;

import org.apache.logging.log4j.Level;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class HandshakeCompletionHandler extends SimpleChannelInboundHandler<FMLMessage.CompleteHandshake> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CompleteHandshake msg) throws Exception
    {
        NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).getAndRemove();
        dispatcher.completeHandshake(msg.target);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "HandshakeCompletionHandler exception");
        super.exceptionCaught(ctx, cause);
    }
}
