package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptingEncoder extends MessageToByteEncoder
{
    private final NettyEncryptionTranslator field_150750_a;
    private static final String __OBFID = "CL_00001239";

    public NettyEncryptingEncoder(Cipher p_i45142_1_)
    {
        this.field_150750_a = new NettyEncryptionTranslator(p_i45142_1_);
    }

    protected void encode(ChannelHandlerContext p_150749_1_, ByteBuf p_150749_2_, ByteBuf p_150749_3_) throws ShortBufferException
    {
        this.field_150750_a.func_150504_a(p_150749_2_, p_150749_3_);
    }

    protected void encode(ChannelHandlerContext p_encode_1_, Object p_encode_2_, ByteBuf p_encode_3_) throws ShortBufferException
    {
        this.encode(p_encode_1_, (ByteBuf)p_encode_2_, p_encode_3_);
    }
}