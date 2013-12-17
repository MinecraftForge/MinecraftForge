package net.minecraftforge.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FluidIdRegistryMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.FluidIdMapMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ForgeMessage.FluidIdMapMessage msg) throws Exception
    {
        // Do something with the message
    }
}
