package cpw.mods.fml.common.network.handshake;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLMessage;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;

enum FMLHandshakeServerState implements IHandshakeState<FMLHandshakeServerState>
{
    START
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            dispatcher.serverInitiateHandshake();
            ctx.writeAndFlush(FMLHandshakeMessage.makeCustomChannelRegistration(NetworkRegistry.INSTANCE.channelNamesFor(Side.SERVER))).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            ctx.writeAndFlush(new FMLHandshakeMessage.ServerHello()).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
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
            FMLLog.info("Client attempting to join with %d mods : %s", client.modListSize(), client.modListAsString());
            String result = FMLNetworkHandler.checkModList(client, Side.CLIENT);
            if (result != null)
            {
                NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
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
                ctx.writeAndFlush(new FMLHandshakeMessage.ModIdData(GameData.buildItemDataList())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
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