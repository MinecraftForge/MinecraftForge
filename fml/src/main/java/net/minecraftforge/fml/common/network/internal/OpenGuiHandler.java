package net.minecraftforge.fml.common.network.internal;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.internal.FMLMessage.OpenGui;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class OpenGuiHandler extends SimpleChannelInboundHandler<FMLMessage.OpenGui> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OpenGui msg) throws Exception
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        player.openGui(msg.modId, msg.modGuiId, player.worldObj, msg.x, msg.y, msg.z);
        player.openContainer.windowId = msg.windowId;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "OpenGuiHandler exception");
        super.exceptionCaught(ctx, cause);
    }

}
