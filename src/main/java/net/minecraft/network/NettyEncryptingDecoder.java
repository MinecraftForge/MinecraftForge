package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptingDecoder extends MessageToMessageDecoder
{
    private final NettyEncryptionTranslator field_150509_a;
    private static final String __OBFID = "CL_00001238";

    public NettyEncryptingDecoder(Cipher p_i45141_1_)
    {
        this.field_150509_a = new NettyEncryptionTranslator(p_i45141_1_);
    }

    protected void decode(ChannelHandlerContext p_150508_1_, ByteBuf p_150508_2_, List p_150508_3_) throws ShortBufferException
    {
        p_150508_3_.add(this.field_150509_a.func_150503_a(p_150508_1_, p_150508_2_));
    }

    protected void decode(ChannelHandlerContext p_decode_1_, Object p_decode_2_, List p_decode_3_) throws ShortBufferException
    {
        this.decode(p_decode_1_, (ByteBuf)p_decode_2_, p_decode_3_);
    }
}