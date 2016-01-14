package net.minecraftforge.fml.common.network.handshake;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.FMLNetworkException;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.PacketLoggingHandler;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Level;

@SuppressWarnings("rawtypes")
public class NetworkDispatcher extends SimpleChannelInboundHandler<Packet> implements ChannelOutboundHandler {
    private static boolean DEBUG_HANDSHAKE = Boolean.parseBoolean(System.getProperty("fml.debugNetworkHandshake", "false"));
    private static enum ConnectionState {
        OPENING, AWAITING_HANDSHAKE, HANDSHAKING, HANDSHAKECOMPLETE, FINALIZING, CONNECTED;
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

    public static final AttributeKey<NetworkDispatcher> FML_DISPATCHER = AttributeKey.valueOf("fml:dispatcher");
    public static final AttributeKey<Boolean> IS_LOCAL = AttributeKey.valueOf("fml:isLocal");
    public static final AttributeKey<PersistentRegistryManager.GameDataSnapshot> FML_GAMEDATA_SNAPSHOT = AttributeKey.valueOf("fml:gameDataSnapshot");
    public final NetworkManager manager;
    private final ServerConfigurationManager scm;
    private EntityPlayerMP player;
    private ConnectionState state;
    private ConnectionType connectionType;
    private final Side side;
    private final EmbeddedChannel handshakeChannel;
    private NetHandlerPlayServer serverHandler;
    private INetHandler netHandler;
    private Map<String,String> modList;
    private int overrideLoginDim;

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
        this.handshakeChannel.attr(IS_LOCAL).set(manager.isLocalChannel());
        if (DEBUG_HANDSHAKE)
            PacketLoggingHandler.register(manager);
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
        this.handshakeChannel.attr(IS_LOCAL).set(manager.isLocalChannel());
        if (DEBUG_HANDSHAKE)
            PacketLoggingHandler.register(manager);
    }

    public void serverToClientHandshake(EntityPlayerMP player)
    {
        this.player = player;
        insertIntoChannel();
        Boolean fml = this.manager.channel().attr(NetworkRegistry.FML_MARKER).get();
        if (fml != null && fml.booleanValue())
        {
            //FML on client, send server hello
            //TODO: Make this cleaner as it uses netty magic 0.o
        }
        else
        {
            serverInitiateHandshake();
            FMLLog.info("Connection received without FML marker, assuming vanilla.");
            this.completeServerSideConnection(ConnectionType.VANILLA);
        }
    }

    protected void setModList(Map<String,String> modList)
    {
        this.modList = modList;
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
        if (this.state != null) {
            FMLLog.getLogger().log(Level.INFO, "Opening channel which already seems to have a state set. This is a vanilla connection. Handshake handler will stop now");
            return;
        }
        FMLLog.getLogger().log(Level.TRACE, "Handshake channel activating");
        this.state = ConnectionState.OPENING;
        // send ourselves as a user event, to kick the pipeline active
        this.handshakeChannel.pipeline().fireUserEventTriggered(this);
        this.manager.channel().config().setAutoRead(true);
    }

    int serverInitiateHandshake()
    {
        // Send mod salutation to the client
        // This will be ignored by vanilla clients
        this.state = ConnectionState.AWAITING_HANDSHAKE;
        // Need to start the handler here, so we can send custompayload packets
        serverHandler = new NetHandlerPlayServer(scm.getServerInstance(), manager, player)
        {
            @Override
            public void update()
            {
                if (NetworkDispatcher.this.state == ConnectionState.FINALIZING)
                {
                    completeServerSideConnection(ConnectionType.MODDED);
                }
                super.update();
            }
        };
        this.netHandler = serverHandler;
        // NULL the play server here - we restore it further on. If not, there are packets sent before the login
        player.playerNetServerHandler = null;
        // manually for the manager into the PLAY state, so we can send packets later
        this.manager.setConnectionState(EnumConnectionState.PLAY);

        // Return the dimension the player is in, so it can be pre-sent to the client in the ServerHello v2 packet
        // Requires some hackery to the serverconfigmanager and stuff for this to work
        NBTTagCompound playerNBT = scm.getPlayerNBT(player);
        if (playerNBT!=null)
        {
            int dimension = playerNBT.getInteger("Dimension");
            if (DimensionManager.isDimensionRegistered(dimension))
            {
        	    return dimension;
            }
        }
        return 0;
    }

    void clientListenForServerHandshake()
    {
        manager.setConnectionState(EnumConnectionState.PLAY);
        //FMLCommonHandler.instance().waitForPlayClient();
        this.netHandler = FMLCommonHandler.instance().getClientPlayHandler();
        this.state = ConnectionState.AWAITING_HANDSHAKE;
    }

    private void completeClientSideConnection(ConnectionType type)
    {
        this.connectionType = type;
        FMLLog.info("[%s] Client side %s connection established", Thread.currentThread().getName(), this.connectionType.name().toLowerCase(Locale.ENGLISH));
        this.state = ConnectionState.CONNECTED;
        MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ClientConnectedToServerEvent(manager, this.connectionType.name()));
    }

    private synchronized void completeServerSideConnection(ConnectionType type)
    {
        this.connectionType = type;
        FMLLog.info("[%s] Server side %s connection established", Thread.currentThread().getName(), this.connectionType.name().toLowerCase(Locale.ENGLISH));
        this.state = ConnectionState.CONNECTED;
        MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ServerConnectionFromClientEvent(manager));
        if (DEBUG_HANDSHAKE)
            manager.closeChannel(new ChatComponentText("Handshake Complete review log file for details."));
        scm.initializeConnectionToPlayer(manager, player, serverHandler);
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

    private boolean handleVanilla(Packet<?> msg)
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

    /**
     * The mod list returned by this method is in no way reliable because it is provided by the client
     *
     * @return a map that will contain String keys and values listing all mods and their versions
     */
    public Map<String,String> getModList()
    {
        return Collections.unmodifiableMap(modList);
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
    @SuppressWarnings("unchecked")
	private void kickWithMessage(String message)
    {
        final ChatComponentText chatcomponenttext = new ChatComponentText(message);
        if (side == Side.CLIENT)
        {
            manager.closeChannel(chatcomponenttext);
        }
        else
        {
            manager.sendPacket(new S40PacketDisconnect(chatcomponenttext), new GenericFutureListener<Future<? super Void>>()
            {
                @Override
                public void operationComplete(Future<? super Void> result)
                {
                    manager.closeChannel(chatcomponenttext);
                }
            }, new GenericFutureListener[0]);
        }
        manager.channel().config().setAutoRead(false);
    }

    private MultiPartCustomPayload multipart = null;
    private boolean handleClientSideCustomPacket(S3FPacketCustomPayload msg, ChannelHandlerContext context)
    {
        String channelName = msg.getChannelName();
        if ("FML|MP".equals(channelName))
        {
            try
            {
                if (multipart == null)
                {
                    multipart = new MultiPartCustomPayload(msg.getBufferData());
                }
                else
                {
                    multipart.processPart(msg.getBufferData());
                }
            }
            catch (IOException e)
            {
                this.kickWithMessage(e.getMessage());
                multipart = null;
                return true;
            }

            if (multipart.isComplete())
            {
                msg = multipart;
                channelName = msg.getChannelName();
                multipart = null;
            }
            else
            {
                return true; // Haven't received all so return till we have.
            }
        }
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
            synchronized (this) { // guard from other threads changing the state on us
                if (state == ConnectionState.AWAITING_HANDSHAKE) {
                    state = ConnectionState.HANDSHAKING;
                }
            }
        }
        String channelName = msg.getChannelName();
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

    public void sendProxy(FMLProxyPacket msg)
    {
        if (!manager.isChannelOpen())
            msg = msg.copy();
        manager.sendPacket(msg);
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
        	MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ClientDisconnectionFromServerEvent(manager));
        }
        else
        {
        	MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ServerDisconnectionFromClientEvent(manager));
        }
        cleanAttributes(ctx);
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception
    {
        if (side == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ClientDisconnectionFromServerEvent(manager));
        }
        else
        {
        	MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ServerDisconnectionFromClientEvent(manager));
        }
        cleanAttributes(ctx);
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
            {
                //Client to server large packets are not supported to prevent client being bad.
                ctx.write(((FMLProxyPacket) msg).toC17Packet(), promise);
            }
            else
            {
                List<Packet<INetHandlerPlayClient>> parts = ((FMLProxyPacket)msg).toS3FPackets();
                for (Packet<INetHandlerPlayClient> pkt : parts)
                {
                    ctx.write(pkt, promise);
                }
            }
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
            this.state = ConnectionState.FINALIZING; //Delay and finalize in the world tick loop.
        }
    }

    public void completeClientHandshake()
    {
        state = ConnectionState.HANDSHAKECOMPLETE;
    }

    public void abortClientHandshake(String type)
    {
        FMLLog.log(Level.INFO, "Aborting client handshake \"%s\"", type);
        //FMLCommonHandler.instance().waitForPlayClient();
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

    // if we add any attributes, we should force removal of them here so that
    //they do not hold references to the world and causes it to leak.
    private void cleanAttributes(ChannelHandlerContext ctx)
    {
        ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).remove();
        ctx.channel().attr(NetworkRegistry.NET_HANDLER).remove();
        ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).remove();
        this.handshakeChannel.attr(FML_DISPATCHER).remove();
        this.manager.channel().attr(FML_DISPATCHER).remove();
    }

    public void setOverrideDimension(int overrideDim) {
        this.overrideLoginDim = overrideDim;
        FMLLog.fine("Received override dimension %d", overrideDim);
    }

    public int getOverrideDimension(S01PacketJoinGame packetIn) {
        FMLLog.fine("Overriding dimension: using %d", this.overrideLoginDim);
        return this.overrideLoginDim != 0 ? this.overrideLoginDim : packetIn.getDimension();
    }

    private class MultiPartCustomPayload extends S3FPacketCustomPayload
    {
        private String channel;
        private byte[] data;
        private PacketBuffer data_buf = null;
        private int part_count = 0;
        private int part_expected = 0;
        private int offset = 0;

        private MultiPartCustomPayload(PacketBuffer preamble) throws IOException
        {
            channel = preamble.readStringFromBuffer(20);
            part_count = preamble.readUnsignedByte();
            int length = preamble.readInt();
            if (length <= 0 || length >= FMLProxyPacket.MAX_LENGTH)
            {
                throw new IOException("The received FML MultiPart packet outside of valid length bounds, Max: " + FMLProxyPacket.MAX_LENGTH + ", Received: " + length);
            }
            data = new byte[length];
            data_buf = new PacketBuffer(Unpooled.wrappedBuffer(data));
        }

        public void processPart(PacketBuffer input) throws IOException
        {
            int part = (int)(input.readByte() & 0xFF);
            if (part != part_expected)
            {
                throw new IOException("Received FML MultiPart packet out of order, Expected " + part_expected + " Got " + part);
            }
            int len = input.readableBytes() - 1;
            input.readBytes(data, offset, len);
            part_expected++;
            offset += len;
        }

        public boolean isComplete()
        {
            return part_expected == part_count;
        }

        @Override
        public String getChannelName() // getChannel
        {
            return this.channel;
        }

        @Override
        public PacketBuffer getBufferData() // getData
        {
            return this.data_buf;
        }
    }
}
