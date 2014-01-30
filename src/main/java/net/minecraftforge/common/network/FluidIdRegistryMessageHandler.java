package net.minecraftforge.common.network;

import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FluidIdRegistryMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.FluidIdMapMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ForgeMessage.FluidIdMapMessage msg) throws Exception
    {
        FluidRegistry.initFluidIDs(msg.fluidIds);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "FluidIdRegistryMessageHandler exception");
        super.exceptionCaught(ctx, cause);
    }

}
