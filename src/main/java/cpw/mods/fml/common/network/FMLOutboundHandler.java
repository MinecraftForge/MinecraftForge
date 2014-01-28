package cpw.mods.fml.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

public class FMLOutboundHandler extends ChannelOutboundHandlerAdapter {
    public static final AttributeKey<OutboundTarget> FML_MESSAGETARGET = new AttributeKey<OutboundTarget>("fml:outboundTarget");
    public static final AttributeKey<Object> FML_MESSAGETARGETARGS = new AttributeKey<Object>("fml:outboundTargetArgs");
    public enum OutboundTarget {
        NOWHERE
        {
            @Override
            public void validateArgs(Object args)
            {
                // NOOP
            }

            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                return null;
            }

        },
        REPLY
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
        PLAYER
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
                NetworkDispatcher dispatcher = player.playerNetServerHandler.field_147371_a.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                return ImmutableList.of(dispatcher);
            }
        },
        ALL
        {
            @Override
            public void validateArgs(Object args)
            {
            }
            @SuppressWarnings("unchecked")
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.<NetworkDispatcher>builder();
                for (EntityPlayerMP player : (List<EntityPlayerMP>)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
                {
                    NetworkDispatcher dispatcher = player.playerNetServerHandler.field_147371_a.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                    builder.add(dispatcher);
                }
                return builder.build();
            }
        },
        DIMENSION
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof Integer))
                {
                    throw new RuntimeException("DIMENSION expects an integer argument");
                }
            }
            @SuppressWarnings("unchecked")
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                int dimension = (Integer)args;
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.<NetworkDispatcher>builder();
                for (EntityPlayerMP player : (List<EntityPlayerMP>)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
                {
                    if (dimension == player.dimension)
                    {
                        NetworkDispatcher dispatcher = player.playerNetServerHandler.field_147371_a.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                        builder.add(dispatcher);
                    }
                }
                return builder.build();
            }
        },
        ALLAROUNDPOINT
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof TargetPoint))
                {
                    throw new RuntimeException("ALLAROUNDPOINT expects a TargetPoint argument");
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                TargetPoint tp = (TargetPoint)args;
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.<NetworkDispatcher>builder();
                for (EntityPlayerMP player : (List<EntityPlayerMP>)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
                {
                    if (player.dimension == tp.dimension)
                    {
                        double d4 = tp.x - player.posX;
                        double d5 = tp.y - player.posY;
                        double d6 = tp.z - player.posZ;

                        if (d4 * d4 + d5 * d5 + d6 * d6 < tp.range * tp.range)
                        {
                            NetworkDispatcher dispatcher = player.playerNetServerHandler.field_147371_a.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                            builder.add(dispatcher);
                        }
                    }
                }
                return builder.build();
            }
        },
        TOSERVER
        {
            @Override
            public void validateArgs(Object args)
            {
                throw new RuntimeException("Cannot set TOSERVER as a target on the server");
            }
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args, ChannelHandlerContext context, FMLProxyPacket packet)
            {
                NetworkManager clientConnection = FMLCommonHandler.instance().getClientToServerNetworkManager();
                return clientConnection == null ? ImmutableList.<NetworkDispatcher>of() : ImmutableList.of(clientConnection.channel().attr(NetworkDispatcher.FML_DISPATCHER).get());
            }
        };

        public abstract void validateArgs(Object args);
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
        if (ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get() == Side.CLIENT)
        {
            outboundTarget = OutboundTarget.TOSERVER;
        }
        else
        {
            outboundTarget = ctx.channel().attr(FML_MESSAGETARGET).get();
            args = ctx.channel().attr(FML_MESSAGETARGETARGS).get();

            outboundTarget.validateArgs(args);
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
            targetDispatcher.sendProxy((FMLProxyPacket) msg);
        }

    }

}