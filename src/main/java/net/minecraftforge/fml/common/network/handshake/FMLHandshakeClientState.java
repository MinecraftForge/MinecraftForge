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

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage.ServerHello;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

/**
 * Packet handshake sequence manager- client side (responding to remote server)
 *
 * Flow:
 * 1. Wait for server hello. (START). Move to HELLO state.
 * 2. Receive Server Hello. Send customchannel registration. Send Client Hello. Send our modlist. Move to WAITINGFORSERVERDATA state.
 * 3. Receive server modlist. Send ack if acceptable, else send nack and exit error. Receive server IDs. Move to COMPLETE state. Send ack.
 *
 * @author cpw
 *
 */
enum FMLHandshakeClientState implements IHandshakeState<FMLHandshakeClientState>
{
    START
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            dispatcher.clientListenForServerHandshake();
            return HELLO;
        }
    },
    HELLO
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            // write our custom packet registration, always
            ctx.writeAndFlush(FMLHandshakeMessage.makeCustomChannelRegistration(NetworkRegistry.INSTANCE.channelNamesFor(Side.CLIENT)));
            if (msg == null)
            {
                NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                dispatcher.abortClientHandshake("VANILLA");
                // VANILLA login
                return DONE;
            }

            ServerHello serverHelloPacket = (FMLHandshakeMessage.ServerHello)msg;
            FMLLog.log.info("Server protocol version {}", Integer.toHexString(serverHelloPacket.protocolVersion()));
            if (serverHelloPacket.protocolVersion() > 1)
            {
                // Server sent us an extra dimension for the logging in player - stash it for retrieval later
                NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                dispatcher.setOverrideDimension(serverHelloPacket.overrideDim());
            }
            ctx.writeAndFlush(new FMLHandshakeMessage.ClientHello()).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            ctx.writeAndFlush(new FMLHandshakeMessage.ModList(Loader.instance().getActiveModList())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            return WAITINGSERVERDATA;
        }
    },

    WAITINGSERVERDATA
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            String result = FMLNetworkHandler.checkModList((FMLHandshakeMessage.ModList) msg, Side.SERVER);
            if (result != null)
            {
                NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                dispatcher.rejectHandshake(result);
                return ERROR;
            }
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck(ordinal())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            if (!ctx.channel().attr(NetworkDispatcher.IS_LOCAL).get())
            {
                return WAITINGSERVERCOMPLETE;
            }
            else
            {
                return PENDINGCOMPLETE;
            }
        }
    },
    WAITINGSERVERCOMPLETE
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            FMLHandshakeMessage.RegistryData pkt = (FMLHandshakeMessage.RegistryData)msg;
            Map<ResourceLocation, ForgeRegistry.Snapshot> snap = ctx.channel().attr(NetworkDispatcher.FML_GAMEDATA_SNAPSHOT).get();
            if (snap == null)
            {
                snap = Maps.newHashMap();
                ctx.channel().attr(NetworkDispatcher.FML_GAMEDATA_SNAPSHOT).set(snap);
            }

            if (RegistryManager.ACTIVE.getRegistry(pkt.getName()) != null)
                RegistryManager.ACTIVE.getRegistry(pkt.getName()).readFromBuffer(pkt.getExtra());
            ForgeRegistry.Snapshot entry = new ForgeRegistry.Snapshot();
            entry.ids.putAll(pkt.getIdMap());
            entry.dummied.addAll(pkt.getDummied());
            entry.overrides.putAll(pkt.getOverrides());
            snap.put(pkt.getName(), entry);

            if (pkt.hasMore())
            {
                FMLLog.log.debug("Received Mod Registry mapping for {}: {} IDs {} overrides {} dummied", pkt.getName(), entry.ids.size(), entry.overrides.size(), entry.dummied.size());
                return WAITINGSERVERCOMPLETE;
            }

            ctx.channel().attr(NetworkDispatcher.FML_GAMEDATA_SNAPSHOT).set(null);

            Multimap<ResourceLocation, ResourceLocation> locallyMissing = GameData.injectSnapshot(snap, false, false);
            if (!locallyMissing.isEmpty())
            {
                NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                dispatcher.rejectHandshake("Fatally missing blocks and items");
                FMLLog.log.fatal("Failed to connect to server: there are {} missing blocks and items", locallyMissing.size());
                locallyMissing.asMap().forEach((key, value) ->  FMLLog.log.debug("Missing {} Entries: {}", key, value));
                return ERROR;
            }
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck(ordinal())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            return PENDINGCOMPLETE;
        }
    },
    PENDINGCOMPLETE
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck(ordinal())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            return COMPLETE;
        }
    },
    COMPLETE
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            dispatcher.completeClientHandshake();
            FMLMessage.CompleteHandshake complete = new FMLMessage.CompleteHandshake(Side.CLIENT);
            ctx.fireChannelRead(complete);
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck(ordinal())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            return DONE;
        }
    },
    DONE
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            if (msg instanceof FMLHandshakeMessage.HandshakeReset)
            {
                GameData.revertToFrozen();
                return HELLO;
            }
            return this;
        }
    },
    ERROR
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            return this;
        }
    };
}
