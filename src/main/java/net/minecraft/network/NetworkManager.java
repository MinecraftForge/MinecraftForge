package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.crypto.SecretKey;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler
{
    private static final Logger field_150735_g = LogManager.getLogger();
    public static final Marker field_150740_a = MarkerManager.getMarker("NETWORK");
    public static final Marker field_150738_b = MarkerManager.getMarker("NETWORK_PACKETS", field_150740_a);
    public static final AttributeKey field_150739_c = new AttributeKey("protocol");
    public static final AttributeKey field_150736_d = new AttributeKey("receivable_packets");
    public static final AttributeKey field_150737_e = new AttributeKey("sendable_packets");
    public static final NioEventLoopGroup field_150734_f = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    private final boolean field_150747_h;
    private final Queue field_150748_i = Queues.newConcurrentLinkedQueue();
    private final Queue field_150745_j = Queues.newConcurrentLinkedQueue();
    private Channel field_150746_k;
    private SocketAddress field_150743_l;
    private INetHandler field_150744_m;
    private EnumConnectionState field_150741_n;
    private IChatComponent field_150742_o;
    private static final String __OBFID = "CL_00001240";

    public NetworkManager(boolean p_i45147_1_)
    {
        this.field_150747_h = p_i45147_1_;
    }

    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception
    {
        super.channelActive(p_channelActive_1_);
        this.field_150746_k = p_channelActive_1_.channel();
        this.field_150743_l = this.field_150746_k.remoteAddress();
        this.func_150723_a(EnumConnectionState.HANDSHAKING);
    }

    public void func_150723_a(EnumConnectionState p_150723_1_)
    {
        this.field_150741_n = (EnumConnectionState)this.field_150746_k.attr(field_150739_c).getAndSet(p_150723_1_);
        this.field_150746_k.attr(field_150736_d).set(p_150723_1_.func_150757_a(this.field_150747_h));
        this.field_150746_k.attr(field_150737_e).set(p_150723_1_.func_150754_b(this.field_150747_h));
        this.field_150746_k.config().setAutoRead(true);
        field_150735_g.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext p_channelInactive_1_)
    {
        this.func_150718_a(new ChatComponentTranslation("disconnect.endOfStream", new Object[0]));
    }

    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_)
    {
        this.func_150718_a(new ChatComponentTranslation("disconnect.genericReason", new Object[] {"Internal Exception: " + p_exceptionCaught_2_}));
    }

    protected void channelRead0(ChannelHandlerContext p_150728_1_, Packet p_150728_2_)
    {
        if (this.field_150746_k.isOpen())
        {
            if (p_150728_2_.func_148836_a())
            {
                p_150728_2_.func_148833_a(this.field_150744_m);
            }
            else
            {
                this.field_150748_i.add(p_150728_2_);
            }
        }
    }

    public void func_150719_a(INetHandler p_150719_1_)
    {
        Validate.notNull(p_150719_1_, "packetListener", new Object[0]);
        field_150735_g.debug("Set listener of {} to {}", new Object[] {this, p_150719_1_});
        this.field_150744_m = p_150719_1_;
    }

    public void func_150725_a(Packet p_150725_1_, GenericFutureListener ... p_150725_2_)
    {
        if (this.field_150746_k != null && this.field_150746_k.isOpen())
        {
            this.func_150733_h();
            this.func_150732_b(p_150725_1_, p_150725_2_);
        }
        else
        {
            this.field_150745_j.add(new NetworkManager.InboundHandlerTuplePacketListener(p_150725_1_, p_150725_2_));
        }
    }

    private void func_150732_b(final Packet p_150732_1_, final GenericFutureListener[] p_150732_2_)
    {
        final EnumConnectionState enumconnectionstate = EnumConnectionState.func_150752_a(p_150732_1_);
        final EnumConnectionState enumconnectionstate1 = (EnumConnectionState)this.field_150746_k.attr(field_150739_c).get();

        if (enumconnectionstate1 != enumconnectionstate && !( p_150732_1_ instanceof FMLProxyPacket))
        {
            field_150735_g.debug("Disabled auto read");
            this.field_150746_k.config().setAutoRead(false);
        }

        if (this.field_150746_k.eventLoop().inEventLoop())
        {
            if (enumconnectionstate != enumconnectionstate1 && !( p_150732_1_ instanceof FMLProxyPacket))
            {
                this.func_150723_a(enumconnectionstate);
            }

            this.field_150746_k.writeAndFlush(p_150732_1_).addListeners(p_150732_2_).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else
        {
            this.field_150746_k.eventLoop().execute(new Runnable()
            {
                private static final String __OBFID = "CL_00001241";
                public void run()
                {
                    if (enumconnectionstate != enumconnectionstate1  && !( p_150732_1_ instanceof FMLProxyPacket))
                    {
                        NetworkManager.this.func_150723_a(enumconnectionstate);
                    }

                    NetworkManager.this.field_150746_k.writeAndFlush(p_150732_1_).addListeners(p_150732_2_).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    private void func_150733_h()
    {
        if (this.field_150746_k != null && this.field_150746_k.isOpen())
        {
            while (!this.field_150745_j.isEmpty())
            {
                NetworkManager.InboundHandlerTuplePacketListener inboundhandlertuplepacketlistener = (NetworkManager.InboundHandlerTuplePacketListener)this.field_150745_j.poll();
                this.func_150732_b(inboundhandlertuplepacketlistener.field_150774_a, inboundhandlertuplepacketlistener.field_150773_b);
            }
        }
    }

    // JAVADOC METHOD $$ func_74428_b
    public void processReadPackets()
    {
        this.func_150733_h();
        EnumConnectionState enumconnectionstate = (EnumConnectionState)this.field_150746_k.attr(field_150739_c).get();

        if (this.field_150741_n != enumconnectionstate)
        {
            if (this.field_150741_n != null)
            {
                this.field_150744_m.func_147232_a(this.field_150741_n, enumconnectionstate);
            }

            this.field_150741_n = enumconnectionstate;
        }

        if (this.field_150744_m != null)
        {
            for (int i = 1000; !this.field_150748_i.isEmpty() && i >= 0; --i)
            {
                Packet packet = (Packet)this.field_150748_i.poll();
                packet.func_148833_a(this.field_150744_m);
            }

            this.field_150744_m.func_147233_a();
        }

        this.field_150746_k.flush();
    }

    // JAVADOC METHOD $$ func_74430_c
    public SocketAddress getSocketAddress()
    {
        return this.field_150743_l;
    }

    public void func_150718_a(IChatComponent p_150718_1_)
    {
        if (this.field_150746_k.isOpen())
        {
            this.field_150746_k.close();
            this.field_150742_o = p_150718_1_;
        }
    }

    public boolean func_150731_c()
    {
        return this.field_150746_k instanceof LocalChannel || this.field_150746_k instanceof LocalServerChannel;
    }

    @SideOnly(Side.CLIENT)
    public static NetworkManager func_150726_a(InetAddress p_150726_0_, int p_150726_1_)
    {
        final NetworkManager networkmanager = new NetworkManager(true);
        ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group(field_150734_f)).handler(new ChannelInitializer()
        {
            private static final String __OBFID = "CL_00001242";
            protected void initChannel(Channel p_initChannel_1_)
            {
                try
                {
                    p_initChannel_1_.config().setOption(ChannelOption.IP_TOS, Integer.valueOf(24));
                }
                catch (ChannelException channelexception1)
                {
                    ;
                }

                try
                {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(false));
                }
                catch (ChannelException channelexception)
                {
                    ;
                }

                p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(20)).addLast("splitter", new MessageDeserializer2()).addLast("decoder", new MessageDeserializer()).addLast("prepender", new MessageSerializer2()).addLast("encoder", new MessageSerializer()).addLast("packet_handler", networkmanager);
            }
        })).channel(NioSocketChannel.class)).connect(p_150726_0_, p_150726_1_).syncUninterruptibly();
        return networkmanager;
    }

    @SideOnly(Side.CLIENT)
    public static NetworkManager func_150722_a(SocketAddress p_150722_0_)
    {
        final NetworkManager networkmanager = new NetworkManager(true);
        ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group(field_150734_f)).handler(new ChannelInitializer()
        {
            private static final String __OBFID = "CL_00001243";
            protected void initChannel(Channel p_initChannel_1_)
            {
                p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
            }
        })).channel(LocalChannel.class)).connect(p_150722_0_).syncUninterruptibly();
        return networkmanager;
    }

    public void func_150727_a(SecretKey p_150727_1_)
    {
        this.field_150746_k.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.func_151229_a(2, p_150727_1_)));
        this.field_150746_k.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.func_151229_a(1, p_150727_1_)));
    }

    public boolean func_150724_d()
    {
        return this.field_150746_k != null && this.field_150746_k.isOpen();
    }

    public INetHandler func_150729_e()
    {
        return this.field_150744_m;
    }

    public IChatComponent func_150730_f()
    {
        return this.field_150742_o;
    }

    public void func_150721_g()
    {
        this.field_150746_k.config().setAutoRead(false);
    }

    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Object p_channelRead0_2_)
    {
        this.channelRead0(p_channelRead0_1_, (Packet)p_channelRead0_2_);
    }

    public Channel channel()
    {
        return field_150746_k;
    }

    static class InboundHandlerTuplePacketListener
        {
            private final Packet field_150774_a;
            private final GenericFutureListener[] field_150773_b;
            private static final String __OBFID = "CL_00001244";

            public InboundHandlerTuplePacketListener(Packet p_i45146_1_, GenericFutureListener ... p_i45146_2_)
            {
                this.field_150774_a = p_i45146_1_;
                this.field_150773_b = p_i45146_2_;
            }
        }
}