package net.minecraftforge.fml.common.network.internal;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.FMLNetworkException;

import com.google.common.base.Splitter;

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

    @Override
    protected void testMessageValidity(FMLProxyPacket msg)
    {
        if (msg.payload().getByte(0) == 0 && msg.payload().readableBytes() > 2)
        {
            FMLLog.severe("The connection appears to have sent an invalid FML packet of type 0, this is likely because it think's it's talking to 1.6.4 FML");
            FMLLog.info("Bad data :");
            for (String l : Splitter.on('\n').split(ByteBufUtils.getContentDump(msg.payload()))) {
                FMLLog.info("\t%s",l);
            }
            throw new FMLNetworkException("Invalid FML packet");
        }
    }
}
