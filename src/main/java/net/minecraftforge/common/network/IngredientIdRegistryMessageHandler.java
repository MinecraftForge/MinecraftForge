package net.minecraftforge.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.ingredients.IngredientRegistry;
import org.apache.logging.log4j.Level;

public class IngredientIdRegistryMessageHandler extends SimpleChannelInboundHandler<ForgeMessage.IngredientIdMapMessage>
{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ForgeMessage.IngredientIdMapMessage msg) throws Exception
    {
        IngredientRegistry.initIngredientIDs(msg.ingredientIds, msg.defaultIngredients);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "FluidIdRegistryMessageHandler exception");
        super.exceptionCaught(ctx, cause);
    }

}
