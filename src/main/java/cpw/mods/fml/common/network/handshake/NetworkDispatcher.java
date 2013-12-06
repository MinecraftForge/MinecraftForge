package cpw.mods.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.FMLProxyPacket;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

public class NetworkDispatcher extends SimpleChannelInboundHandler<Packet> {
    private static enum ConnectionState {
        OPENING, AWAITING_HANDSHAKE, HANDSHAKING, CONNECTED;
    }

    private static enum ConnectionType {
        MODDED, BUKKIT, VANILLA;
    }

    public static NetworkDispatcher get(NetworkManager manager)
    {
        return manager.channel().attr(FML_DISPATCHER).get();
    }

    public static NetworkDispatcher allocAndSet(NetworkManager manager)
    {
        NetworkDispatcher net = new NetworkDispatcher(manager);
        manager.channel().attr(FML_DISPATCHER).getAndSet(net);
        return net;
    }

    public static NetworkDispatcher allocAndSet(NetworkManager manager, ServerConfigurationManager scm)
    {
        NetworkDispatcher net = new NetworkDispatcher(manager, scm);
        manager.channel().attr(FML_DISPATCHER).getAndSet(net);
        return net;
    }

    public static final AttributeKey<NetworkDispatcher> FML_DISPATCHER = new AttributeKey<NetworkDispatcher>("fml:dispatcher");
    private final NetworkManager manager;
    private final ServerConfigurationManager scm;
    private EntityPlayerMP player;
    private ConnectionState state;
    private ConnectionType connectionType;
    private final Side side;
    private final EmbeddedChannel handshakeChannel;

    public NetworkDispatcher(NetworkManager manager)
    {
        super(Packet.class, false);
        this.manager = manager;
        this.scm = null;
        this.side = Side.CLIENT;
        this.handshakeChannel = new EmbeddedChannel(new HandshakeInjector(this), new FMLHandshakeCodec(), new HandshakeMessageHandler<FMLHandshakeClientState>(FMLHandshakeClientState.class));
        this.handshakeChannel.attr(FML_DISPATCHER).set(this);
        this.handshakeChannel.attr(NetworkRegistry.FML_CHANNEL).set("FML|HS");
    }

    public NetworkDispatcher(NetworkManager manager, ServerConfigurationManager scm)
    {
        super(Packet.class, false);
        this.manager = manager;
        this.scm = scm;
        this.side = Side.SERVER;
        this.handshakeChannel = new EmbeddedChannel(new HandshakeInjector(this), new FMLHandshakeCodec(), new HandshakeMessageHandler<FMLHandshakeServerState>(FMLHandshakeServerState.class));
        this.handshakeChannel.attr(FML_DISPATCHER).set(this);
        this.handshakeChannel.attr(NetworkRegistry.FML_CHANNEL).set("FML|HS");
    }

    public void serverToClientHandshake(EntityPlayerMP player)
    {
        this.player = player;
        insertIntoChannel();
    }

    private void insertIntoChannel()
    {
        this.manager.channel().config().setAutoRead(false);
        // Insert ourselves into the pipeline
        this.manager.channel().pipeline().addBefore("packet_handler", "fml:packet_handler", this);
    }

    public void clientToServerHandshake()
    {
        insertIntoChannel();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        this.state = ConnectionState.OPENING;
        // send ourselves as a user event, to kick the pipeline active
        this.handshakeChannel.pipeline().fireUserEventTriggered(this);
        this.manager.channel().config().setAutoRead(true);
    }

    void serverInitiateHandshake()
    {
        // Send mod salutation to the client
        // This will be ignored by vanilla clients
        this.state = ConnectionState.AWAITING_HANDSHAKE;
        this.manager.channel().pipeline().addFirst("fml:vanilla_detector", new VanillaTimeoutWaiter());
    }

    void clientListenForServerHandshake()
    {
        manager.func_150723_a(EnumConnectionState.PLAY);
        this.state = ConnectionState.AWAITING_HANDSHAKE;
    }

    void continueToClientPlayState()
    {
        this.state = ConnectionState.CONNECTED;
        this.connectionType = ConnectionType.MODDED;
        completeClientSideConnection();
    }

    private void completeClientSideConnection()
    {
    }

    void continueToServerPlayState()
    {
        this.state = ConnectionState.CONNECTED;
        this.connectionType = ConnectionType.MODDED;
        completeServerSideConnection();
    }

    private void completeServerSideConnection()
    {
        NetHandlerPlayServer nethandler = new NetHandlerPlayServer(scm.func_72365_p(), manager, player);
        // NULL the play server here - we restore it further on. If not, there are packets sent before the login
        player.field_71135_a = null;
        scm.func_72355_a(manager, player, nethandler);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception
    {
        boolean handled = false;
        if (msg instanceof C17PacketCustomPayload)
        {
            handled = handleServerSideCustomPacket((C17PacketCustomPayload) msg, ctx);
        }
        else if (msg instanceof S3FPacketCustomPayload)
        {
            handled = handleClientSideCustomPacket((S3FPacketCustomPayload)msg, ctx);
        }
        else if (state != ConnectionState.CONNECTED)
        {
            FMLLog.info("Unexpected packet during modded negotiation - assuming vanilla");
            kickVanilla();
        }
        if (!handled)
        {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        if (evt instanceof ConnectionType && side == Side.SERVER)
        {
            FMLLog.info("Timeout occurred, assuming a vanilla client");
            kickVanilla();
        }
    }

    private void kickVanilla()
    {
        final ChatComponentText chatcomponenttext = new ChatComponentText("This is modded. No modded response received. Bye!");
        manager.func_150725_a(new S40PacketDisconnect(chatcomponenttext), new GenericFutureListener<Future<?>>()
        {
            public void operationComplete(Future<?> result)
            {
                manager.func_150718_a(chatcomponenttext);
            }
        });
        manager.channel().config().setAutoRead(false);
    }

    private boolean handleClientSideCustomPacket(S3FPacketCustomPayload msg, ChannelHandlerContext context)
    {
        String channelName = msg.func_149169_c();
        if ("FML|HS".equals(channelName))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            handshakeChannel.writeInbound(proxy);
            return true;
        }
        else if (NetworkRegistry.INSTANCE.hasChannel(channelName, Side.CLIENT))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            context.fireChannelRead(proxy);
            return true;
        }
        return false;
    }

    private boolean handleServerSideCustomPacket(C17PacketCustomPayload msg, ChannelHandlerContext context)
    {
        if (state == ConnectionState.AWAITING_HANDSHAKE)
        {
            this.manager.channel().pipeline().remove("fml:vanilla_detector");
            state = ConnectionState.HANDSHAKING;
        }
        String channelName = msg.func_149559_c();
        if ("FML|HS".equals(channelName))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            handshakeChannel.writeInbound(proxy);
            return true;
        }
        else if (NetworkRegistry.INSTANCE.hasChannel(channelName, Side.SERVER))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            context.fireChannelRead(proxy);
            return true;
        }
        return false;
    }

    private class VanillaTimeoutWaiter extends ChannelInboundHandlerAdapter
    {
        private ScheduledFuture<Void> future;

        @Override
        public void handlerAdded(final ChannelHandlerContext ctx) throws Exception
        {
            future = ctx.executor().schedule(new Callable<Void>() {
                @Override
                public Void call() throws Exception
                {
                    if (state != ConnectionState.CONNECTED)
                    {
                        FMLLog.info("Timeout occurred waiting for response, assuming vanilla connection");
                        ctx.fireUserEventTriggered(ConnectionType.VANILLA);
                    }
                    return null;
                }
            }, 10, TimeUnit.HOURS);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
        {
            future.cancel(true);
        }
    }

    /**
     * Callback from the networkmanager
     * @param fmlProxyPacket
     */
    public void dispatch(FMLProxyPacket fmlProxyPacket)
    {
    }

    public void sendProxy(FMLProxyPacket msg)
    {
        if (side == Side.CLIENT)
            manager.func_150725_a(msg.toC17Packet());
        else
            manager.func_150725_a(msg.toS3FPacket());
    }
}
