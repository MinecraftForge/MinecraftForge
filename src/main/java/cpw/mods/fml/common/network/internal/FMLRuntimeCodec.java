package cpw.mods.fml.common.network.internal;

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class FMLRuntimeCodec extends FMLIndexedMessageToMessageCodec<FMLMessage> {
    public FMLRuntimeCodec()
    {
        addDiscriminator(0,FMLMessage.CompleteHandshake.class);
        addDiscriminator(1,FMLMessage.OpenGui.class);
        addDiscriminator(2,FMLMessage.EntitySpawnMessage.class);
        addDiscriminator(3,FMLMessage.EntityAdjustMessage.class);
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
