package net.minecraftforge.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Level;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;

public class ChannelRegistrationHandler extends SimpleChannelInboundHandler<FMLProxyPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket msg) throws Exception
    {
        Side side = msg.getTarget();
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "ChannelRegistrationHandler exception");
        super.exceptionCaught(ctx, cause);
    }
}
