package net.minecraftforge.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;
import org.apache.logging.log4j.Level;
import net.minecraftforge.fml.common.FMLLog;

public class DimensionMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.DimensionRegisterMessage>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DimensionRegisterMessage msg) throws Exception
    {
        if (!DimensionManager.isDimensionRegistered(msg.dimensionId))
        {
            DimensionManager.registerDimension(msg.dimensionId, msg.providerId);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "DimensionMessageHandler exception");
        super.exceptionCaught(ctx, cause);
    }

}
