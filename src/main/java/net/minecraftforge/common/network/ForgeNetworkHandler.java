package net.minecraftforge.common.network;

import io.netty.channel.embedded.EmbeddedChannel;

import java.util.EnumMap;

import net.minecraftforge.common.ForgeModContainer;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLRuntimeCodec;
import cpw.mods.fml.common.network.internal.HandshakeCompletionHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ForgeNetworkHandler
{
    private static EnumMap<Side, EmbeddedChannel> channelPair;

    public static void registerChannel(ForgeModContainer forgeModContainer, Side side)
    {
        channelPair = NetworkRegistry.INSTANCE.newChannel(forgeModContainer, "FORGE", new ForgeRuntimeCodec(), new HandshakeCompletionHandler());
        EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.NOWHERE);

        if (side == Side.CLIENT)
        {
            addClientHandlers();
        }
        channelPair.get(Side.SERVER).pipeline().addAfter("net.minecraftforge.common.network.ForgeRuntimeCodec#0", "ServerToClientConnection", new ServerToClientConnectionEstablishedHandler());
    }

    @SideOnly(Side.CLIENT)
    private static void addClientHandlers()
    {
        channelPair.get(Side.CLIENT).pipeline().addAfter("net.minecraftforge.common.network.ForgeRuntimeCodec#0", "DimensionHandler", new DimensionMessageHandler());
        channelPair.get(Side.CLIENT).pipeline().addAfter("net.minecraftforge.common.network.ForgeRuntimeCodec#0", "FluidIdRegistryHandler", new FluidIdRegistryMessageHandler());
    }
}
