package net.minecraftforge.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;

public class ForgeRuntimeCodec extends FMLIndexedMessageToMessageCodec<ForgeMessage> {
    public ForgeRuntimeCodec()
    {
        addDiscriminator(1, ForgeMessage.DimensionRegisterMessage.class);
        addDiscriminator(2, ForgeMessage.FluidIdMapMessage.class);
    }
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ForgeMessage msg, ByteBuf target) throws Exception
    {
        msg.toBytes(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, ForgeMessage msg)
    {
        msg.fromBytes(source);
    }
}
