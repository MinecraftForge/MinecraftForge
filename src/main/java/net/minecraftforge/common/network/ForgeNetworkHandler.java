/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.network;

import java.util.EnumMap;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.api.distmarker.Dist;

public class ForgeNetworkHandler
{
    private static EnumMap<Side, FMLEmbeddedChannel> channelPair;

    public static void registerChannel(ForgeMod forgeMod, Side side)
    {
        channelPair = NetworkRegistry.INSTANCE.newChannel(forgeMod, "FORGE", new ForgeRuntimeCodec());
        if (side == Side.CLIENT)
        {
            addClientHandlers();
        }

        FMLEmbeddedChannel serverChannel = channelPair.get(Side.SERVER);
        serverChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.NOWHERE);
        String handlerName = serverChannel.findChannelHandlerNameForType(ForgeRuntimeCodec.class);
        serverChannel.pipeline().addAfter(handlerName, "ServerToClientConnection", new ServerToClientConnectionEstablishedHandler());
    }

    @OnlyIn(Dist.CLIENT)
    private static void addClientHandlers()
    {
        FMLEmbeddedChannel clientChannel = channelPair.get(Side.CLIENT);
        String handlerName = clientChannel.findChannelHandlerNameForType(ForgeRuntimeCodec.class);
        clientChannel.pipeline().addAfter(handlerName, "DimensionHandler", new DimensionMessageHandler());
        clientChannel.pipeline().addAfter(handlerName, "FluidIdRegistryHandler", new FluidIdRegistryMessageHandler());
    }
}
