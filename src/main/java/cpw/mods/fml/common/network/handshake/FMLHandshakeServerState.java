package cpw.mods.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
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
            ctx.writeAndFlush(FMLHandshakeMessage.makeCustomChannelRegistration(NetworkRegistry.INSTANCE.channelNamesFor(Side.SERVER)));
            ctx.writeAndFlush(new FMLHandshakeMessage.ServerHello());
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
            if (ctx.channel().attr(NetworkDispatcher.IS_LOCAL).get())
            {
                return COMPLETE;
            }
            return WAITINGCACK;
        }
    },
    WAITINGCACK
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            ctx.writeAndFlush(new FMLHandshakeMessage.ModIdData(GameData.buildItemDataList()));
            return COMPLETE;
        }
    },
    COMPLETE
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            FMLLog.info("Server side modded connection established");
            dispatcher.continueToServerPlayState();
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