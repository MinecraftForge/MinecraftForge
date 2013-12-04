package cpw.mods.fml.common.network;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import sun.net.ProgressSource.State;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.ScheduledFuture;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.server.management.ServerConfigurationManager;

import com.google.common.collect.ListMultimap;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.packet.PacketManager;
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

    private static final AttributeKey<NetworkDispatcher> FML_DISPATCHER = new AttributeKey<NetworkDispatcher>("fml:dispatcher");
    private static final AttributeKey<ListMultimap<String,NetworkModHolder>> FML_PACKET_HANDLERS = new AttributeKey<ListMultimap<String,NetworkModHolder>>("fml:packet_handlers");
    private final NetworkManager manager;
    private final ServerConfigurationManager scm;
    private EntityPlayerMP player;
    private ConnectionState state;
    private ConnectionType connectionType;
    private Side side;

    public NetworkDispatcher(NetworkManager manager)
    {
        super(Packet.class, false);
        this.manager = manager;
        this.scm = null;
        this.side = Side.CLIENT;
    }

    public NetworkDispatcher(NetworkManager manager, ServerConfigurationManager scm)
    {
        super(Packet.class, false);
        this.manager = manager;
        this.scm = scm;
        this.side = Side.SERVER;
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
        if (side == Side.CLIENT)
        {
            clientListenForServerHandshake();
        }
        else
        {
            serverInitiateHandshake();
        }
        this.manager.channel().config().setAutoRead(true);
    }

    private void serverInitiateHandshake()
    {
        // Send mod salutation to the client
        // This will be ignored by vanilla clients
        this.state = ConnectionState.AWAITING_HANDSHAKE;
        this.manager.channel().pipeline().addFirst("fml:vanilla_detector", new VanillaTimeoutWaiter());
        this.manager.func_150725_a(new S3FPacketCustomPayload("FML", new byte[0]));
    }

    private void clientListenForServerHandshake()
    {
        manager.func_150723_a(EnumConnectionState.PLAY);
        this.state = ConnectionState.AWAITING_HANDSHAKE;
    }

    private void continueToClientPlayState()
    {
        this.state = ConnectionState.CONNECTED;
        this.connectionType = ConnectionType.MODDED;
        completeClientSideConnection();
        // Send modded ack to server
        this.manager.func_150725_a(new C17PacketCustomPayload("FML", new byte[0]));
    }

    private void completeClientSideConnection()
    {
    }

    private void continueToServerPlayState()
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
            handled = handleServerSideCustomPacket((C17PacketCustomPayload) msg);
        }
        else if (msg instanceof S3FPacketCustomPayload)
        {
            handled = handleClientSideCustomPacket((S3FPacketCustomPayload)msg);
        }
        else if (state != ConnectionState.CONNECTED)
        {
            FMLLog.info("Unexpected packet during modded negotiation - assuming vanilla");
            handleVanillaConnection();
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
            handleVanillaConnection();
        }
    }

    private void handleVanillaConnection()
    {
        state = ConnectionState.CONNECTED;
        connectionType = ConnectionType.VANILLA;
        if (side == Side.CLIENT)
        {
            completeClientSideConnection();
        }
        else
        {
            completeServerSideConnection();
        }
    }

    private boolean handleClientSideCustomPacket(S3FPacketCustomPayload msg)
    {
        if ("FML".equals(msg.func_149169_c()))
        {
            continueToClientPlayState();
            FMLLog.info("Client side modded connection established");
            return true;
        }
        return false;
    }

    private boolean handleServerSideCustomPacket(C17PacketCustomPayload msg)
    {
        if (state == ConnectionState.AWAITING_HANDSHAKE)
        {
            this.manager.channel().pipeline().remove("fml:vanilla_detector");
            state = ConnectionState.HANDSHAKING;
        }
        if ("FML".equals(msg.func_149559_c()))
        {
            FMLLog.info("Server side modded connection established");
            continueToServerPlayState();
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
            }, 10, TimeUnit.SECONDS);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
        {
            future.cancel(true);
        }
    }
}
