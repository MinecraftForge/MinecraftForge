package net.minecraftforge.fml.common.network.handshake;

import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class FMLHandshakeCodec extends FMLIndexedMessageToMessageCodec<FMLHandshakeMessage> {
    public FMLHandshakeCodec()
    {
        addDiscriminator((byte)0, FMLHandshakeMessage.ServerHello.class);
        addDiscriminator((byte)1, FMLHandshakeMessage.ClientHello.class);
        addDiscriminator((byte)2, FMLHandshakeMessage.ModList.class);
        addDiscriminator((byte)3, FMLHandshakeMessage.RegistryData.class);
        addDiscriminator((byte)-1, FMLHandshakeMessage.HandshakeAck.class);
        addDiscriminator((byte)-2, FMLHandshakeMessage.HandshakeReset.class);
    }
    @Override
    public void encodeInto(ChannelHandlerContext ctx, FMLHandshakeMessage msg, ByteBuf target) throws Exception
    {
        msg.toBytes(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, FMLHandshakeMessage msg)
    {
        msg.fromBytes(source);
    }
}
