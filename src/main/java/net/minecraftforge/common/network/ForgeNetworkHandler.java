package net.minecraftforge.common.network;

import java.util.EnumMap;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ForgeNetworkHandler
{
    private static EnumMap<Side, FMLEmbeddedChannel> channelPair;

    public static void registerChannel(ForgeModContainer forgeModContainer, Side side)
    {
        channelPair = NetworkRegistry.INSTANCE.newChannel(forgeModContainer, "FORGE", new ForgeRuntimeCodec());
        if (side == Side.CLIENT)
        {
            addClientHandlers();
        }

        FMLEmbeddedChannel serverChannel = channelPair.get(Side.SERVER);
        serverChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.NOWHERE);
        String handlerName = serverChannel.findChannelHandlerNameForType(ForgeRuntimeCodec.class);
        serverChannel.pipeline().addAfter(handlerName, "ServerToClientConnection", new ServerToClientConnectionEstablishedHandler());
    }

    @SideOnly(Side.CLIENT)
    private static void addClientHandlers()
    {
        FMLEmbeddedChannel clientChannel = channelPair.get(Side.CLIENT);
        String handlerName = clientChannel.findChannelHandlerNameForType(ForgeRuntimeCodec.class);
        clientChannel.pipeline().addAfter(handlerName, "DimensionHandler", new DimensionMessageHandler());
        clientChannel.pipeline().addAfter(handlerName, "FluidIdRegistryHandler", new FluidIdRegistryMessageHandler());
    }
}
