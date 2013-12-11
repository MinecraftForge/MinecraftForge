package cpw.mods.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;

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
            FMLLog.info("Server protocol version %x", ((FMLHandshakeMessage.ServerHello)msg).protocolVersion());
            ctx.writeAndFlush(FMLHandshakeMessage.makeCustomChannelRegistration(NetworkRegistry.INSTANCE.channelNamesFor(Side.CLIENT)));
            ctx.writeAndFlush(new FMLHandshakeMessage.ClientHello());
            ctx.writeAndFlush(new FMLHandshakeMessage.ModList(Loader.instance().getActiveModList()));
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
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck());
            if (!ctx.channel().attr(NetworkDispatcher.IS_LOCAL).get())
            {
                return WAITINGSERVERCOMPLETE;
            }
            else
            {
                return COMPLETE;
            }
        }
    },
    WAITINGSERVERCOMPLETE
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            FMLHandshakeMessage.ModIdData modIds = (FMLHandshakeMessage.ModIdData)msg;
            GameData.injectWorldIDMap(modIds.dataList());
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck());
            return COMPLETE;
        }
    },
    COMPLETE
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            dispatcher.continueToClientPlayState();
            FMLLog.info("Client side modded connection established");
            ctx.writeAndFlush(new FMLHandshakeMessage.HandshakeAck());
            return DONE;
        }
    },
    DONE
    {
        @Override
        public FMLHandshakeClientState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
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