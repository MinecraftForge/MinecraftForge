package cpw.mods.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.FMLNetworkException;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLMessage;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

public class NetworkDispatcher extends SimpleChannelInboundHandler<Packet> implements ChannelOutboundHandler {
    private static enum ConnectionState {
        OPENING, AWAITING_HANDSHAKE, HANDSHAKING, HANDSHAKECOMPLETE, CONNECTED;
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
    public static final AttributeKey<Boolean> IS_LOCAL = new AttributeKey<Boolean>("fml:isLocal");
    public final NetworkManager manager;
    private final ServerConfigurationManager scm;
    private EntityPlayerMP player;
    private ConnectionState state;
    private ConnectionType connectionType;
    private final Side side;
    private final EmbeddedChannel handshakeChannel;
    private NetHandlerPlayServer serverHandler;
    private INetHandler netHandler;

    public NetworkDispatcher(NetworkManager manager)
    {
        super(Packet.class, false);
        this.manager = manager;
        this.scm = null;
        this.side = Side.CLIENT;
        this.handshakeChannel = new EmbeddedChannel(new HandshakeInjector(this), new ChannelRegistrationHandler(), new FMLHandshakeCodec(), new HandshakeMessageHandler<FMLHandshakeClientState>(FMLHandshakeClientState.class));
        this.handshakeChannel.attr(FML_DISPATCHER).set(this);
        this.handshakeChannel.attr(NetworkRegistry.CHANNEL_SOURCE).set(Side.SERVER);
        this.handshakeChannel.attr(NetworkRegistry.FML_CHANNEL).set("FML|HS");
        this.handshakeChannel.attr(IS_LOCAL).set(manager.func_150731_c());
    }

    public NetworkDispatcher(NetworkManager manager, ServerConfigurationManager scm)
    {
        super(Packet.class, false);
        this.manager = manager;
        this.scm = scm;
        this.side = Side.SERVER;
        this.handshakeChannel = new EmbeddedChannel(new HandshakeInjector(this), new ChannelRegistrationHandler(), new FMLHandshakeCodec(), new HandshakeMessageHandler<FMLHandshakeServerState>(FMLHandshakeServerState.class));
        this.handshakeChannel.attr(FML_DISPATCHER).set(this);
        this.handshakeChannel.attr(NetworkRegistry.CHANNEL_SOURCE).set(Side.CLIENT);
        this.handshakeChannel.attr(NetworkRegistry.FML_CHANNEL).set("FML|HS");
        this.handshakeChannel.attr(IS_LOCAL).set(manager.func_150731_c());
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
        // Need to start the handler here, so we can send custompayload packets
        serverHandler = new NetHandlerPlayServer(scm.func_72365_p(), manager, player);
        this.netHandler = serverHandler;
        // NULL the play server here - we restore it further on. If not, there are packets sent before the login
        player.field_71135_a = null;
        // manually for the manager into the PLAY state, so we can send packets later
        this.manager.func_150723_a(EnumConnectionState.PLAY);
    }

    void clientListenForServerHandshake()
    {
        manager.func_150723_a(EnumConnectionState.PLAY);
        FMLCommonHandler.instance().waitForPlayClient();
        this.netHandler = FMLCommonHandler.instance().getClientPlayHandler();
        this.state = ConnectionState.AWAITING_HANDSHAKE;
    }

    private void completeClientSideConnection(ConnectionType type)
    {
        this.connectionType = type;
        FMLLog.info("[%s] Client side %s connection established", Thread.currentThread().getName(), this.connectionType.name().toLowerCase(Locale.ENGLISH));
        this.state = ConnectionState.CONNECTED;
        FMLCommonHandler.instance().bus().post(new FMLNetworkEvent.ClientConnectedToServerEvent(manager, this.connectionType.name()));
    }

    private void completeServerSideConnection(ConnectionType type)
    {
        this.connectionType = type;
        FMLLog.info("[%s] Server side %s connection established", Thread.currentThread().getName(), this.connectionType.name().toLowerCase(Locale.ENGLISH));
        this.state = ConnectionState.CONNECTED;
        FMLCommonHandler.instance().bus().post(new FMLNetworkEvent.ServerConnectionFromClientEvent(manager));
        scm.func_72355_a(manager, player, serverHandler);
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
        else if (state != ConnectionState.CONNECTED && state != ConnectionState.HANDSHAKECOMPLETE)
        {
            handled = handleVanilla(msg);
        }
        if (!handled)
        {
            ctx.fireChannelRead(msg);
        }
    }

    private boolean handleVanilla(Packet msg)
    {
        if (state == ConnectionState.AWAITING_HANDSHAKE && msg instanceof S01PacketJoinGame)
        {
            handshakeChannel.pipeline().fireUserEventTriggered(msg);
        }
        else
        {
            FMLLog.info("Unexpected packet during modded negotiation - assuming vanilla or keepalives : %s", msg.getClass().getName());
        }
        return false;
    }

    public INetHandler getNetHandler()
    {
        return netHandler;
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
        kickWithMessage("This is modded. No modded response received. Bye!");
    }
    private void kickWithMessage(String message)
    {
        final ChatComponentText chatcomponenttext = new ChatComponentText(message);
        manager.func_150725_a(new S40PacketDisconnect(chatcomponenttext), new GenericFutureListener<Future<?>>()
        {
            @Override
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
        if ("FML|HS".equals(channelName) || "REGISTER".equals(channelName) || "UNREGISTER".equals(channelName))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            proxy.setDispatcher(this);
            handshakeChannel.writeInbound(proxy);
            // forward any messages into the regular channel
            for (Object push : handshakeChannel.inboundMessages())
            {
                List<FMLProxyPacket> messageResult = FMLNetworkHandler.forwardHandshake((FMLMessage.CompleteHandshake)push, this, Side.CLIENT);
                for (FMLProxyPacket result: messageResult)
                {
                    result.setTarget(Side.CLIENT);
                    result.payload().resetReaderIndex();
                    context.fireChannelRead(result);
                }
            }
            handshakeChannel.inboundMessages().clear();
            return true;
        }
        else if (NetworkRegistry.INSTANCE.hasChannel(channelName, Side.CLIENT))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            proxy.setDispatcher(this);
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
        if ("FML|HS".equals(channelName) || "REGISTER".equals(channelName) || "UNREGISTER".equals(channelName))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            proxy.setDispatcher(this);
            handshakeChannel.writeInbound(proxy);
            for (Object push : handshakeChannel.inboundMessages())
            {
                List<FMLProxyPacket> messageResult = FMLNetworkHandler.forwardHandshake((FMLMessage.CompleteHandshake)push, this, Side.SERVER);
                for (FMLProxyPacket result: messageResult)
                {
                    result.setTarget(Side.SERVER);
                    result.payload().resetReaderIndex();
                    context.fireChannelRead(result);
                }
            }
            handshakeChannel.inboundMessages().clear();
            return true;
        }
        else if (NetworkRegistry.INSTANCE.hasChannel(channelName, Side.SERVER))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            proxy.setDispatcher(this);
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

    public void sendProxy(FMLProxyPacket msg)
    {
        manager.func_150725_a(msg);
    }

    public void rejectHandshake(String result)
    {
        kickWithMessage(result);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception
    {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception
    {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
    {
        if (side == Side.CLIENT)
        {
            FMLCommonHandler.instance().bus().post(new FMLNetworkEvent.ClientDisconnectionFromServerEvent(manager));
        }
        else
        {
            FMLCommonHandler.instance().bus().post(new FMLNetworkEvent.ServerDisconnectionFromClientEvent(manager));
        }
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
    {
        ctx.close(promise);
    }

    @Override
    @Deprecated
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
    {
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception
    {
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        if (msg instanceof FMLProxyPacket)
        {
            if (side == Side.CLIENT)
                ctx.write(((FMLProxyPacket) msg).toC17Packet(), promise);
            else
                ctx.write(((FMLProxyPacket) msg).toS3FPacket(), promise);
        }
        else
        {
            ctx.write(msg, promise);
        }
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    public void completeHandshake(Side target)
    {
        if (state == ConnectionState.CONNECTED)
        {
            FMLLog.severe("Attempt to double complete the network connection!");
            throw new FMLNetworkException("Attempt to double complete!");
        }
        if (side == Side.CLIENT)
        {
            completeClientSideConnection(ConnectionType.MODDED);
        }
        else
        {
            completeServerSideConnection(ConnectionType.MODDED);
        }
    }

    public void completeClientHandshake()
    {
        state = ConnectionState.HANDSHAKECOMPLETE;
    }

    public void abortClientHandshake(String type)
    {
        completeClientSideConnection(ConnectionType.valueOf(type));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        // Stop the epic channel closed spam at close
        if (!(cause instanceof ClosedChannelException))
        {
            FMLLog.log(Level.ERROR, cause, "NetworkDispatcher exception");
        }
        super.exceptionCaught(ctx, cause);
    }

}
