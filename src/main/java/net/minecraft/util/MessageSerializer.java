package net.minecraft.util;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageSerializer extends MessageToByteEncoder
{
    private static final Logger field_150798_a = LogManager.getLogger();
    private static final Marker field_150797_b = MarkerManager.getMarker("PACKET_SENT", NetworkManager.field_150738_b);
    private static final String __OBFID = "CL_00001253";

    protected void encode(ChannelHandlerContext p_150796_1_, Packet p_150796_2_, ByteBuf p_150796_3_) throws IOException
    {
        Integer integer = (Integer)((BiMap)p_150796_1_.channel().attr(NetworkManager.field_150737_e).get()).inverse().get(p_150796_2_.getClass());

        if (field_150798_a.isDebugEnabled())
        {
            field_150798_a.debug(field_150797_b, "OUT: [{}:{}] {}[{}]", new Object[] {p_150796_1_.channel().attr(NetworkManager.field_150739_c).get(), integer, p_150796_2_.getClass().getName(), p_150796_2_.func_148835_b()});
        }

        if (integer == null)
        {
            throw new IOException("Can\'t serialize unregistered packet");
        }
        else
        {
            PacketBuffer packetbuffer = new PacketBuffer(p_150796_3_);
            packetbuffer.func_150787_b(integer.intValue());
            p_150796_2_.func_148840_b(packetbuffer);
        }
    }

    protected void encode(ChannelHandlerContext p_encode_1_, Object p_encode_2_, ByteBuf p_encode_3_) throws IOException
    {
        this.encode(p_encode_1_, (Packet)p_encode_2_, p_encode_3_);
    }
}