package cpw.mods.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.FMLLog;

enum FMLHandshakeServerState implements IHandshakeState<FMLHandshakeServerState>
{
    START
    {
        @Override
        public FMLHandshakeServerState accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg)
        {
            NetworkDispatcher dispatcher = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
            dispatcher.serverInitiateHandshake();
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

            FMLHandshakeMessage.ClientModList client = (FMLHandshakeMessage.ClientModList)msg;
            FMLLog.info("Client joining with %d mods : %s", client.modListSize(), client.modListAsString());
            ctx.writeAndFlush(new FMLHandshakeMessage.ServerModList());
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
    };
}