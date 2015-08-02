package net.minecraftforge.fml.common.network.handshake;

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class HandshakeInjector extends ChannelOutboundHandlerAdapter {

    private NetworkDispatcher dispatcher;
    public HandshakeInjector(NetworkDispatcher networkDispatcher)
    {
        this.dispatcher = networkDispatcher;
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        if (msg instanceof FMLProxyPacket)
        {
            this.dispatcher.sendProxy((FMLProxyPacket) msg);
        }
    }
}
