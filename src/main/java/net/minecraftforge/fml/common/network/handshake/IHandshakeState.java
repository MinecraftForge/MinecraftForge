package net.minecraftforge.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;

public interface IHandshakeState<S> {
    S accept(ChannelHandlerContext ctx, FMLHandshakeMessage msg);
}
