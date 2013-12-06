package cpw.mods.fml.common.network.handshake;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;

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
            ctx.writeAndFlush(new FMLHandshakeMessage.ClientHello());
            ctx.writeAndFlush(new FMLHandshakeMessage.ClientModList(Loader.instance().getActiveModList()));
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
            ctx.writeAndFlush(new FMLHandshakeMessage.ClientAck());
            return this;
        }
    };
}