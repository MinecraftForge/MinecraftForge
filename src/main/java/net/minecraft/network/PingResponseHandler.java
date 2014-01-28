package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PingResponseHandler extends ChannelInboundHandlerAdapter
{
    private static final Logger field_151258_a = LogManager.getLogger();
    private NetworkSystem field_151257_b;
    private static final String __OBFID = "CL_00001444";

    public PingResponseHandler(NetworkSystem p_i45286_1_)
    {
        this.field_151257_b = p_i45286_1_;
    }

    public void channelRead(ChannelHandlerContext p_channelRead_1_, Object p_channelRead_2_)
    {
        ByteBuf bytebuf = (ByteBuf)p_channelRead_2_;
        bytebuf.markReaderIndex();
        boolean flag = true;

        try
        {
            if (bytebuf.readUnsignedByte() == 254)
            {
                InetSocketAddress inetsocketaddress = (InetSocketAddress)p_channelRead_1_.channel().remoteAddress();
                MinecraftServer minecraftserver = this.field_151257_b.func_151267_d();
                int i = bytebuf.readableBytes();
                String s;

                switch (i)
                {
                    case 0:
                        field_151258_a.debug("Ping: (<1.3.x) from {}:{}", new Object[] {inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort())});
                        s = String.format("%s\u00a7%d\u00a7%d", new Object[] {minecraftserver.getMOTD(), Integer.valueOf(minecraftserver.getCurrentPlayerCount()), Integer.valueOf(minecraftserver.getMaxPlayers())});
                        this.func_151256_a(p_channelRead_1_, this.func_151255_a(s));
                        break;
                    case 1:
                        if (bytebuf.readUnsignedByte() != 1)
                        {
                            return;
                        }

                        field_151258_a.debug("Ping: (1.4-1.5.x) from {}:{}", new Object[] {inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort())});
                        s = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[] {Integer.valueOf(127), minecraftserver.getMinecraftVersion(), minecraftserver.getMOTD(), Integer.valueOf(minecraftserver.getCurrentPlayerCount()), Integer.valueOf(minecraftserver.getMaxPlayers())});
                        this.func_151256_a(p_channelRead_1_, this.func_151255_a(s));
                        break;
                    default:
                        boolean flag1 = bytebuf.readUnsignedByte() == 1;
                        flag1 &= bytebuf.readUnsignedByte() == 250;
                        flag1 &= "MC|PingHost".equals(new String(bytebuf.readBytes(bytebuf.readShort() * 2).array(), Charsets.UTF_16BE));
                        int j = bytebuf.readUnsignedShort();
                        flag1 &= bytebuf.readUnsignedByte() >= 73;
                        flag1 &= 3 + bytebuf.readBytes(bytebuf.readShort() * 2).array().length + 4 == j;
                        flag1 &= bytebuf.readInt() <= 65535;
                        flag1 &= bytebuf.readableBytes() == 0;

                        if (!flag1)
                        {
                            return;
                        }

                        field_151258_a.debug("Ping: (1.6) from {}:{}", new Object[] {inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort())});
                        String s1 = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[] {Integer.valueOf(127), minecraftserver.getMinecraftVersion(), minecraftserver.getMOTD(), Integer.valueOf(minecraftserver.getCurrentPlayerCount()), Integer.valueOf(minecraftserver.getMaxPlayers())});
                        this.func_151256_a(p_channelRead_1_, this.func_151255_a(s1));
                }

                bytebuf.release();
                flag = false;
                return;
            }
        }
        catch (RuntimeException runtimeexception)
        {
            return;
        }
        finally
        {
            if (flag)
            {
                bytebuf.resetReaderIndex();
                p_channelRead_1_.channel().pipeline().remove("legacy_query");
                p_channelRead_1_.fireChannelRead(p_channelRead_2_);
            }
        }
    }

    private void func_151256_a(ChannelHandlerContext p_151256_1_, ByteBuf p_151256_2_)
    {
        p_151256_1_.pipeline().firstContext().writeAndFlush(p_151256_2_).addListener(ChannelFutureListener.CLOSE);
    }

    private ByteBuf func_151255_a(String p_151255_1_)
    {
        ByteBuf bytebuf = Unpooled.buffer();
        bytebuf.writeByte(255);
        char[] achar = p_151255_1_.toCharArray();
        bytebuf.writeShort(achar.length);
        char[] achar1 = achar;
        int i = achar.length;

        for (int j = 0; j < i; ++j)
        {
            char c0 = achar1[j];
            bytebuf.writeChar(c0);
        }

        return bytebuf;
    }
}