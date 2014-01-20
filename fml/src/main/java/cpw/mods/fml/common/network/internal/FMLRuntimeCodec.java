package cpw.mods.fml.common.network.internal;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import com.google.common.primitives.Bytes;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLNetworkException;

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

    @Override
    protected void testMessageValidity(FMLProxyPacket msg)
    {
        if (msg.payload().getByte(0) == 0 && msg.payload().readableBytes() > 2)
        {
            FMLLog.severe("The connection appears to have sent an invalid FML packet of type 0, this is likely because it think's it's talking to 1.6.4 FML");
            byte[] badData = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(0, badData);
            FMLLog.info("Bad data : %s",Bytes.asList(badData));
            throw new FMLNetworkException("Invalid FML packet");
        }
    }
}
