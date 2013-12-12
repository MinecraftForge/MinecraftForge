package cpw.mods.fml.common.network.handshake;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChannelRegistrationHandler extends SimpleChannelInboundHandler<FMLProxyPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket msg) throws Exception
    {
        Side side = ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get();
        if (msg.channel().equals("REGISTER") || msg.channel().equals("UNREGISTER"))
        {
            byte[] data = new byte[msg.payload().readableBytes()];
            msg.payload().readBytes(data);
            String channels = new String(data,Charsets.UTF_8);
            String[] split = channels.split("\0");
            for (String channel : split)
            {
                System.out.printf("Register %s from %s\n",channel, side);
            }
        }
        else
        {
            ctx.fireChannelRead(msg);
        }
    }

}
