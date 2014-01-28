package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.PacketBuffer;

public class MessageDeserializer2 extends ByteToMessageDecoder
{
    private static final String __OBFID = "CL_00001255";

    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List p_decode_3_)
    {
        p_decode_2_.markReaderIndex();
        byte[] abyte = new byte[3];

        for (int i = 0; i < abyte.length; ++i)
        {
            if (!p_decode_2_.isReadable())
            {
                p_decode_2_.resetReaderIndex();
                return;
            }

            abyte[i] = p_decode_2_.readByte();

            if (abyte[i] >= 0)
            {
                int j = (new PacketBuffer(Unpooled.wrappedBuffer(abyte))).func_150792_a();

                if (p_decode_2_.readableBytes() < j)
                {
                    p_decode_2_.resetReaderIndex();
                    return;
                }

                p_decode_3_.add(p_decode_2_.readBytes(j));
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}