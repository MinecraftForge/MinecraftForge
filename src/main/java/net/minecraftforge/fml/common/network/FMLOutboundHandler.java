/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;

public class FMLOutboundHandler extends ChannelOutboundHandlerAdapter {
    public static final AttributeKey<OutboundTarget> FML_MESSAGETARGET = AttributeKey.valueOf("fml:outboundTarget");
    public static final AttributeKey<Object> FML_MESSAGETARGETARGS = AttributeKey.valueOf("fml:outboundTargetArgs");
    public enum OutboundTarget {
        /**
         * The packet is sent nowhere. It will be on the {@link EmbeddedChannel#outboundMessages()} Queue.
         *
         * @author cpw
         *
         */
        NOWHERE(Sets.immutableEnumSet(Side.CLIENT, Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
                // NOOP
            }

            @Override
            @Nullable
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                return null;
            }

        },
        /**
         * The packet is sent to the {@link NetworkDispatcher} supplied as an argument.
         *
         * @author cpw
         *
         */
        DISPATCHER(Sets.immutableEnumSet(Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof NetworkDispatcher))
                {
                    throw new RuntimeException("DISPATCHER expects a NetworkDispatcher");
                }
            }

            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                return ImmutableList.of((NetworkDispatcher)args);
            }
        },
        /**
         * The packet is sent to the originator of the packet. This requires the inbound packet
         * to have it's originator information set.
         *
         * @author cpw
         *
         */
        REPLY(Sets.immutableEnumSet(Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
                // NOOP
            }

            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                return ImmutableList.of(packet.getDispatcher());
            }
        },
        /**
         * The packet is sent to the {@link EntityPlayerMP} supplied as an argument.
         *
         * @author cpw
         *
         */
        PLAYER(Sets.immutableEnumSet(Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof EntityPlayerMP))
                {
                    throw new RuntimeException("PLAYER target expects a Player arg");
                }
            }
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                EntityPlayerMP player = (EntityPlayerMP) args;
                NetworkDispatcher dispatcher = (player == null || player instanceof FakePlayer) ? null : player.connection.netManager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                return dispatcher == null ? ImmutableList.<NetworkDispatcher>of() : ImmutableList.of(dispatcher);
            }
        },
        /**
         * The packet is dispatched to all players connected to the server.
         * @author cpw
         *
         */
        ALL(Sets.immutableEnumSet(Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
            }
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.builder();
                for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
                {
                    NetworkDispatcher dispatcher = player.connection.netManager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                    if (dispatcher != null) builder.add(dispatcher);
                }
                return builder.build();
            }
        },
        /**
         * The packet is sent to all players in the dimension identified by the integer argument.
         * @author cpw
         *
         */
        DIMENSION(Sets.immutableEnumSet(Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof Integer))
                {
                    throw new RuntimeException("DIMENSION expects an integer argument");
                }
            }
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                int dimension = (Integer)args;
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.builder();
                for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
                {
                    if (dimension == player.dimension)
                    {
                        NetworkDispatcher dispatcher = player.connection.netManager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                        // Null dispatchers may exist for fake players - skip them
                        if (dispatcher != null) builder.add(dispatcher);
                    }
                }
                return builder.build();
            }
        },
        /**
         * The packet is sent to all players within range of the {@link TargetPoint} argument supplied.
         *
         * @author cpw
         *
         */
        ALLAROUNDPOINT(Sets.immutableEnumSet(Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof TargetPoint))
                {
                    throw new RuntimeException("ALLAROUNDPOINT expects a TargetPoint argument");
                }
            }

            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                TargetPoint tp = (TargetPoint)args;
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.builder();
                for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
                {
                    if (player.dimension == tp.dimension)
                    {
                        double d4 = tp.x - player.posX;
                        double d5 = tp.y - player.posY;
                        double d6 = tp.z - player.posZ;

                        if (d4 * d4 + d5 * d5 + d6 * d6 < tp.range * tp.range)
                        {
                            NetworkDispatcher dispatcher = player.connection.netManager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                            if (dispatcher != null) builder.add(dispatcher);
                        }
                    }
                }
                return builder.build();
            }
        },
        /**
         * The packet is sent to all players that are watching the Chunk containing the supplied {@link TargetPoint}.
         * The {@code range} field of the {@link TargetPoint} is ignored.
         */
        TRACKING_POINT(Sets.immutableEnumSet(Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof TargetPoint))
                {
                    throw new RuntimeException("TRACKING_POINT expects a TargetPoint argument");
                }
            }

            @Nullable
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                TargetPoint tp = (TargetPoint)args;
                WorldServer world = DimensionManager.getWorld(tp.dimension);
                if (world == null)
                {
                    return ImmutableList.of();
                }

                PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(MathHelper.floor(tp.x) >> 4, MathHelper.floor(tp.z) >> 4);
                if (entry == null)
                {
                    return ImmutableList.of();
                }

                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.builder();
                for (EntityPlayerMP player : entry.getWatchingPlayers())
                {
                    NetworkDispatcher dispatcher = player.connection.netManager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                    if (dispatcher != null) builder.add(dispatcher);
                }
                return builder.build();
            }
        },
        /**
         * The packet is sent to all players tracking the supplied {@link Entity}. This is different from {@link #TRACKING_POINT} because Entities
         * can have different tracking distances depending on their type.
         */
        TRACKING_ENTITY(Sets.immutableEnumSet(Side.SERVER))
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof Entity))
                {
                    throw new RuntimeException("TRACKING_ENTITY expects an Entity argument");
                }
            }

            @Nullable
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                Entity e = (Entity)args;
                Set<? extends EntityPlayer> players = FMLCommonHandler.instance().getMinecraftServerInstance()
                        .getWorld(e.dimension).getEntityTracker().getTrackingPlayers(e);

                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.builder();
                for (EntityPlayer player : players)
                {
                    NetworkDispatcher dispatcher = ((EntityPlayerMP) player).connection.netManager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                    if (dispatcher != null) builder.add(dispatcher);
                }
                return builder.build();
            }
        },
        /**
         * The packet is sent to the server this client is currently conversing with.
         * @author cpw
         *
         */
        TOSERVER(Sets.immutableEnumSet(Side.CLIENT))
        {
            @Override
            public void validateArgs(Object args)
            {
            }
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                NetworkManager clientConnection = FMLCommonHandler.instance().getClientToServerNetworkManager();
                return clientConnection == null || clientConnection.channel().attr(NetworkDispatcher.FML_DISPATCHER).get() == null ? ImmutableList.<NetworkDispatcher>of() : ImmutableList.of(clientConnection.channel().attr(NetworkDispatcher.FML_DISPATCHER).get());
            }
        };

        private OutboundTarget(ImmutableSet<Side> sides)
        {
            this.allowed = sides;
        }
        public final ImmutableSet<Side> allowed;
        public abstract void validateArgs(Object args);
        @Nullable
        public abstract List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        if (!(msg instanceof FMLProxyPacket))
        {
            return;
        }
        FMLProxyPacket pkt = (FMLProxyPacket) msg;
        OutboundTarget outboundTarget;
        Object args = null;
        NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
        // INTERNAL message callback - let it pass out
        if (dispatcher != null)
        {
            ctx.write(msg, promise);
            return;
        }

        outboundTarget = ctx.channel().attr(FML_MESSAGETARGET).get();
        Side channelSide = ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get();
        if (outboundTarget != null && outboundTarget.allowed.contains(channelSide))
        {
            args = ctx.channel().attr(FML_MESSAGETARGETARGS).get();
            outboundTarget.validateArgs(args);
        }
        else if (channelSide == Side.CLIENT)
        {
            outboundTarget = OutboundTarget.TOSERVER;
        }
        else
        {
            throw new FMLNetworkException("Packet arrived at the outbound handler without a valid target!");
        }

        List<NetworkDispatcher> dispatchers = outboundTarget.selectNetworks(args, ctx, pkt);

        // This will drop the messages into the output queue at the embedded channel
        if (dispatchers == null)
        {
            ctx.write(msg, promise);
            return;
        }
        for (NetworkDispatcher targetDispatcher : dispatchers)
        {
            pkt.payload().retain();
            targetDispatcher.sendProxy(pkt);
        }
        pkt.payload().release();
    }

}
