/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common.network.handshake;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

enum FMLHandshakeServerState implements IHandshakeState<FMLHandshakeServerState>
{
    START
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            int overrideDim = dispatcher.serverInitiateHandshake();
            ctx.writeAndFlush(FMLHandshakeMessage.makeCustomChannelRegistration(NetworkRegistry.INSTANCE.channelNamesFor(Side.SERVER))).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            ctx.writeAndFlush(new FMLHandshakeMessage.ServerHello(overrideDim)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            return HELLO;
        }
    },
    HELLO
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            // Hello packet first
            if (msg instanceof FMLHandshakeMessage.ClientHello)
            {
                FMLLog.info("Client protocol version %x", ((FMLHandshakeMessage.ClientHello)msg).protocolVersion());
                return this;
            }

            FMLHandshakeMessage.ModList client = (FMLHandshakeMessage.ModList)msg;
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            dispatcher.setModList(client.modList());
            FMLLog.info("Client attempting to join with %d mods : %s", client.modListSize(), client.modListAsString());
            String result = FMLNetworkHandler.checkModList(client, Side.CLIENT);
            if (result != null)
            {
                dispatcher.rejectHandshake(result);
                return ERROR;
            }
            ctx.writeAndFlush(new FMLHandshakeMessage.ModList(Loader.instance().getActiveModList()));
            return WAITINGCACK;
        }
    },
    WAITINGCACK
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            if (!ctx.channel().attr(NetworkDispatcher.IS_LOCAL).get())
            {
                Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot = RegistryManager.ACTIVE.takeSnapshot(false);
                Iterator<Map.Entry<ResourceLocation, ForgeRegistry.Snapshot>> itr = snapshot.entrySet().iterator();
                while (itr.hasNext())
                {
                    Entry<ResourceLocation, ForgeRegistry.Snapshot> e = itr.next();
                    ctx.writeAndFlush(new FMLHandshakeMessage.RegistryData(itr.hasNext(), e.getKey(), e.getValue())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            }
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck(ordinal())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            NetworkRegistry.INSTANCE.fireNetworkHandshake(ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get(), Side.SERVER);
            return COMPLETE;
        }
    },
    COMPLETE
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            // Poke the client
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck(ordinal())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            FMLMessage.CompleteHandshake complete = new FMLMessage.CompleteHandshake(Side.SERVER);
            ctx.fireChannelRead(complete);
            return DONE;
        }
    },
    DONE
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            return this;
        }
    },
    ERROR
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            return this;
        }
    };
}
