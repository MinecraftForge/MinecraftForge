package net.minecraftforge.common.network;

import net.minecraftforge.fml.common.network.NetworkHandshakeEstablished;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerToClientConnectionEstablishedHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        if (evt instanceof NetworkHandshakeEstablished)
        {
            ctx.writeAndFlush(new ForgeMessage.FluidIdMapMessage());
            return;
        }
        // pass it forward
        ctx.fireUserEventTriggered(evt);
    }
}
