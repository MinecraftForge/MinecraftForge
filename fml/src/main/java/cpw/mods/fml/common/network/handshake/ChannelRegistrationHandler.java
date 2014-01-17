package cpw.mods.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Set;
import net.minecraft.network.NetworkManager;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

public class ChannelRegistrationHandler extends SimpleChannelInboundHandler<FMLProxyPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket msg) throws Exception
    {
        Side side = ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get();
        NetworkManager manager = msg.getOrigin();
        if (msg.channel().equals("REGISTER") || msg.channel().equals("UNREGISTER"))
        {
            byte[] data = new byte[msg.payload().readableBytes()];
            msg.payload().readBytes(data);
            String channels = new String(data,Charsets.UTF_8);
            String[] split = channels.split("\0");
            Set<String> channelSet = ImmutableSet.copyOf(split);
            FMLCommonHandler.instance().fireNetRegistrationEvent(manager, channelSet, msg.channel(), side);
        }
        else
        {
            ctx.fireChannelRead(msg);
        }
    }

}
