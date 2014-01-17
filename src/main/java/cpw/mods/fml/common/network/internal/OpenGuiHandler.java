package cpw.mods.fml.common.network.internal;

import org.apache.logging.log4j.Level;
import net.minecraft.entity.player.EntityPlayer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.internal.FMLMessage.OpenGui;

public class OpenGuiHandler extends SimpleChannelInboundHandler<FMLMessage.OpenGui> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OpenGui msg) throws Exception
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().field_71439_g;
        player.openGui(msg.modId, msg.modGuiId, player.field_70170_p, msg.x, msg.y, msg.z);
        player.field_71070_bA.field_75152_c = msg.windowId;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "OpenGuiHandler exception");
        super.exceptionCaught(ctx, cause);
    }

}
