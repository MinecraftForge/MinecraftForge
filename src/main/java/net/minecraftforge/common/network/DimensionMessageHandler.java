package net.minecraftforge.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DimensionMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.DimensionRegisterMessage>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DimensionRegisterMessage msg) throws Exception
    {
        if (!DimensionManager.isDimensionRegistered(msg.dimensionId))
        {
            DimensionManager.registerDimension(msg.dimensionId, msg.providerId);
        }
    }

}
