package cpw.mods.fml.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class FMLRuntimeCodec extends FMLIndexedMessageToMessageCodec<FMLMessage> {
    public FMLRuntimeCodec()
    {
        addDiscriminator(1,FMLMessage.OpenGui.class);
        addDiscriminator(2,FMLMessage.EntitySpawnMessage.class);
    }
    @Override
    public void encodeInto(ChannelHandlerContext ctx, FMLMessage msg, ByteBuf target) throws Exception
    {
        msg.toBytes(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, FMLMessage msg)
    {
        msg.fromBytes(source);
    }

}
