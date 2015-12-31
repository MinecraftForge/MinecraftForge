package net.minecraftforge.fml.common.network.handshake;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage.ServerHello;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;
import net.minecraftforge.fml.relauncher.Side;

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
            FMLLog.info("Server protocol version %x", serverHelloPacket.protocolVersion());
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
            PersistentRegistryManager.GameDataSnapshot snap = ctx.channel().attr(NetworkDispatcher.FML_GAMEDATA_SNAPSHOT).get();
            if (snap == null)
            {
                snap = new PersistentRegistryManager.GameDataSnapshot();
                ctx.channel().attr(NetworkDispatcher.FML_GAMEDATA_SNAPSHOT).set(snap);
            }

            PersistentRegistryManager.GameDataSnapshot.Entry entry = new PersistentRegistryManager.GameDataSnapshot.Entry();
            entry.ids.putAll(pkt.getIdMap());
            entry.substitutions.addAll(pkt.getSubstitutions());
            entry.dummied.addAll(pkt.getDummied());
            snap.entries.put(pkt.getName(), entry);

            if (pkt.hasMore())
            {
                FMLLog.fine("Received Mod Registry mapping for %s: %d IDs %d subs %d dummied", pkt.getName(), entry.ids.size(), entry.substitutions.size(), entry.dummied.size());
                return WAITINGSERVERCOMPLETE;
            }

            ctx.channel().attr(NetworkDispatcher.FML_GAMEDATA_SNAPSHOT).remove();

            List<String> locallyMissing = PersistentRegistryManager.injectSnapshot(snap, false, false);
            if (!locallyMissing.isEmpty())
            {
                NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                dispatcher.rejectHandshake("Fatally missing blocks and items");
                FMLLog.severe("Failed to connect to server: there are %d missing blocks and items", locallyMissing.size());
                FMLLog.fine("Missing list: %s", locallyMissing);
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
                PersistentRegistryManager.revertToFrozen();
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
