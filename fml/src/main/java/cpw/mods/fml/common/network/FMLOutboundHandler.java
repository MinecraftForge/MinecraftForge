package cpw.mods.fml.common.network;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.OutboundTarget;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class FMLOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        if (!(msg instanceof FMLProxyPacket))
        {
            return;
        }
        OutboundTarget outboundTarget;
        Object args = null;
        if (ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get() == Side.CLIENT)
        {
            outboundTarget = OutboundTarget.TOSERVER;
        }
        else
        {
            outboundTarget = ctx.channel().attr(NetworkRegistry.FML_MESSAGETARGET).get();
            args = ctx.channel().attr(NetworkRegistry.FML_MESSAGETARGETARGS).get();

            outboundTarget.validateArgs(args);
        }
        List<NetworkDispatcher> dispatchers = outboundTarget.selectNetworks(args);

        for (NetworkDispatcher dispatcher : dispatchers)
        {
            dispatcher.sendProxy((FMLProxyPacket) msg);
        }

    }

}
