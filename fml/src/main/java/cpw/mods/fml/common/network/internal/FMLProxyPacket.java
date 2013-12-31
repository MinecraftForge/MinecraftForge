package cpw.mods.fml.common.network.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

public class FMLProxyPacket extends Packet {
    final String channel;
    private Side target;
    private final ByteBuf payload;
    private INetHandler netHandler;

    private FMLProxyPacket(byte[] payload, String channel)
    {
        this(Unpooled.wrappedBuffer(payload), channel);
    }

    public FMLProxyPacket(S3FPacketCustomPayload original)
    {
        this(original.func_149168_d(), original.func_149169_c());
        this.target = Side.CLIENT;
    }

    public FMLProxyPacket(C17PacketCustomPayload original)
    {
        this(original.func_149558_e(), original.func_149559_c());
        this.target = Side.SERVER;
    }

    public FMLProxyPacket(ByteBuf payload, String channel)
    {
        this.channel = channel;
        this.payload = payload;
    }
    @Override
    public void func_148837_a(PacketBuffer packetbuffer) throws IOException
    {
        // NOOP - we are not built this way
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) throws IOException
    {
        // NOOP - we are not built this way
    }

    @Override
    public void func_148833_a(INetHandler inethandler)
    {
        this.netHandler = inethandler;
        EmbeddedChannel internalChannel = NetworkRegistry.INSTANCE.getChannel(this.channel, this.target);
        internalChannel.attr(NetworkRegistry.NET_HANDLER).set(this.netHandler);
        if (internalChannel != null)
        {
            internalChannel.writeInbound(this);
        }
    }

    public String channel()
    {
        return channel;
    }
    public ByteBuf payload()
    {
        return payload;
    }
    public INetHandler handler()
    {
        return netHandler;
    }
    public Packet toC17Packet()
    {
        return new C17PacketCustomPayload(channel, payload.array());
    }

    public Packet toS3FPacket()
    {
        return new S3FPacketCustomPayload(channel, payload.array());
    }

    public void setTarget(Side target)
    {
        this.target = target;
    }
}
