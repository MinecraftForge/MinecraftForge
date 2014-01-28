package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

public class MessageSerializer2 extends MessageToByteEncoder
{
    private static final String __OBFID = "CL_00001256";

    protected void encode(ChannelHandlerContext p_150667_1_, ByteBuf p_150667_2_, ByteBuf p_150667_3_)
    {
        int i = p_150667_2_.readableBytes();
        int j = PacketBuffer.func_150790_a(i);

        if (j > 3)
        {
            throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
        }
        else
        {
            PacketBuffer packetbuffer = new PacketBuffer(p_150667_3_);
            packetbuffer.ensureWritable(j + i);
            packetbuffer.func_150787_b(i);
            packetbuffer.writeBytes(p_150667_2_, p_150667_2_.readerIndex(), i);
        }
    }

    protected void encode(ChannelHandlerContext p_encode_1_, Object p_encode_2_, ByteBuf p_encode_3_)
    {
        this.encode(p_encode_1_, (ByteBuf)p_encode_2_, p_encode_3_);
    }
}