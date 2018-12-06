/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.ForgeRegistry;

// TODO build test suites to validate the behaviour of this stuff and make it less annoyingly magical
public class NetworkDispatcher extends SimpleChannelInboundHandler<Packet<?>> implements ChannelOutboundHandler {
    private static boolean DEBUG_HANDSHAKE = Boolean.parseBoolean(System.getProperty("fml.debugNetworkHandshake", "false"));
    private static enum ConnectionState {
        OPENING, AWAITING_HANDSHAKE, HANDSHAKING, HANDSHAKECOMPLETE, FINALIZING, CONNECTED
    }

    public static enum ConnectionType {
        MODDED, BUKKIT, VANILLA
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

    public static NetworkDispatcher allocAndSet(NetworkManager manager, PlayerList scm)
    {
        NetworkDispatcher net = new NetworkDispatcher(manager, scm);
        manager.channel().attr(FML_DISPATCHER).getAndSet(net);
        return net;
    }

    public static final AttributeKey<NetworkDispatcher> FML_DISPATCHER = AttributeKey.valueOf("fml:dispatcher");
    public static final AttributeKey<Boolean> IS_LOCAL = AttributeKey.valueOf("fml:isLocal");
    public static final AttributeKey<Map<ResourceLocation, ForgeRegistry.Snapshot>> FML_GAMEDATA_SNAPSHOT = AttributeKey.valueOf("fml:gameDataSnapshot");
    public final NetworkManager manager;
    private final PlayerList scm;
    private EntityPlayerMP player;
    private ConnectionState state;
    private ConnectionType connectionType;
    private final Side side;
    private final EmbeddedChannel handshakeChannel;
    private NetHandlerPlayServer serverHandler;
    private INetHandler netHandler;
    private Map<String,String> modList = Collections.emptyMap();
    private int overrideLoginDim;

    public NetworkDispatcher(NetworkManager manager)
    {
        super(false);
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

    public NetworkDispatcher(NetworkManager manager, PlayerList scm)
    {
        super(false);
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
        Boolean fml = this.manager.channel().attr(NetworkRegistry.FML_MARKER).get();
        if (fml != null && fml)
        {
            //FML on client, send server hello
            //TODO: Make this cleaner as it uses netty magic 0.o
            insertIntoChannel();
        }
        else
        {
            serverInitiateHandshake();
            FMLLog.log.info("Connection received without FML marker, assuming vanilla.");
            insertIntoChannel();
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
        if (this.state != null) {
            FMLLog.log.info("Opening channel which already seems to have a state set. This is a vanilla connection. Handshake handler will stop now");
            this.manager.channel().config().setAutoRead(true);
            return;
        }
        FMLLog.log.trace("Handshake channel activating");
        this.state = ConnectionState.OPENING;
        // send ourselves as a user event, to kick the pipeline active
        this.handshakeChannel.pipeline().fireUserEventTriggered(this);
        this.manager.channel().config().setAutoRead(true);
    }

    public void clientToServerHandshake()
    {
        insertIntoChannel();
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
                // FORGE: sometimes the netqueue will tick while login is occurring, causing an NPE. We shouldn't tick until the connection is complete
                if (this.player.connection != this) return;
                super.update();
            }
        };
        this.netHandler = serverHandler;
        // NULL the play server here - we restore it further on. If not, there are packets sent before the login
        player.connection = null;
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
        FMLLog.log.info("[{}] Client side {} connection established", Thread.currentThread().getName(), this.connectionType.name().toLowerCase(Locale.ENGLISH));
        this.state = ConnectionState.CONNECTED;
        MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ClientConnectedToServerEvent(manager, this.connectionType.name()));
    }

    private synchronized void completeServerSideConnection(ConnectionType type)
    {
        this.connectionType = type;
        FMLLog.log.info("[{}] Server side {} connection established", Thread.currentThread().getName(), this.connectionType.name().toLowerCase(Locale.ENGLISH));
        this.state = ConnectionState.CONNECTED;
        if (DEBUG_HANDSHAKE)
            manager.closeChannel(new TextComponentString("Handshake Complete review log file for details."));
        scm.initializeConnectionToPlayer(manager, player, serverHandler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> msg) throws Exception
    {
        boolean handled = false;
        if (msg instanceof CPacketCustomPayload)
        {
            handled = handleServerSideCustomPacket((CPacketCustomPayload) msg, ctx);
        }
        else if (msg instanceof SPacketCustomPayload)
        {
            handled = handleClientSideCustomPacket((SPacketCustomPayload)msg, ctx);
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
        if (state == ConnectionState.AWAITING_HANDSHAKE && msg instanceof SPacketJoinGame)
        {
            handshakeChannel.pipeline().fireUserEventTriggered(msg);
        }
        else
        {
            FMLLog.log.info("Unexpected packet during modded negotiation - assuming vanilla or keepalives : {}", msg.getClass().getName());
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
            FMLLog.log.info("Timeout occurred, assuming a vanilla client");
            kickVanilla();
        }
    }

    private void kickVanilla()
    {
        kickWithMessage("This is modded. No modded response received. Bye!");
    }

    private void kickWithMessage(String message)
    {
        FMLLog.log.error("Network Disconnect: {}", message);
        final TextComponentString TextComponentString = new TextComponentString(message);
        if (side == Side.CLIENT)
        {
            manager.closeChannel(TextComponentString);
        }
        else
        {
            manager.sendPacket(new SPacketDisconnect(TextComponentString), new GenericFutureListener<Future<? super Void>>()
            {
                @Override
                public void operationComplete(Future<? super Void> result)
                {
                    manager.closeChannel(TextComponentString);
                }
            }, (GenericFutureListener<? extends Future<? super Void>>[])null);
        }
        manager.channel().config().setAutoRead(false);
    }

    private MultiPartCustomPayload multipart = null;

    private boolean handleClientSideCustomPacket(SPacketCustomPayload msg, ChannelHandlerContext context)
    {
        String channelName = msg.getChannelName();
        if ("FML|MP".equals(channelName))
        {
            boolean result = handleMultiPartCustomPacket(msg, context);
            if (result)
            {
                msg.getBufferData().release();
            }
            return result;
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

    private boolean handleMultiPartCustomPacket(SPacketCustomPayload msg, ChannelHandlerContext context)
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
            boolean result = handleClientSideCustomPacket(multipart, context);
            multipart = null;
            return result;
        }
        else
        {
            return true; // Haven't received all so return till we have.
        }
    }

    private boolean handleServerSideCustomPacket(CPacketCustomPayload msg, ChannelHandlerContext context)
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
                int sizeMinusOne = parts.size() - 1;
                for (int i = 0; i < sizeMinusOne; i++)
                {
                    ctx.write(parts.get(i), ctx.voidPromise());
                }
                ctx.write(parts.get(sizeMinusOne), promise);
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
            FMLLog.log.fatal("Attempt to double complete the network connection!");
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
        FMLLog.log.info("Aborting client handshake \"{}\"", type);
        //FMLCommonHandler.instance().waitForPlayClient();
        completeClientSideConnection(ConnectionType.valueOf(type));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        // Stop the epic channel closed spam at close
        if (!(cause instanceof ClosedChannelException))
        {
            // Mute the reset by peer exception - it's disconnection noise
            if (cause.getMessage() != null && cause.getMessage().contains("Connection reset by peer"))
            {
                FMLLog.log.debug("Muted NetworkDispatcher exception", cause);
            }
            else
            {
                FMLLog.log.error("NetworkDispatcher exception", cause);
            }
        }
        super.exceptionCaught(ctx, cause);
    }

    // If we add any attributes, we should force removal of them here so that
    // they do not hold references to the world and cause it to leak.
    private void cleanAttributes(ChannelHandlerContext ctx)
    {
        ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(null);
        ctx.channel().attr(NetworkRegistry.NET_HANDLER).set(null);
        ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).set(null);
        this.handshakeChannel.attr(FML_DISPATCHER).set(null);
        this.manager.channel().attr(FML_DISPATCHER).set(null);
        NetworkRegistry.INSTANCE.cleanAttributes();
    }

    public void setOverrideDimension(int overrideDim) {
        this.overrideLoginDim = overrideDim;
        FMLLog.log.debug("Received override dimension {}", overrideDim);
    }

    public int getOverrideDimension(SPacketJoinGame packetIn) {
        FMLLog.log.debug("Overriding dimension: using {}", this.overrideLoginDim);
        return this.overrideLoginDim != 0 ? this.overrideLoginDim : packetIn.getDimension();
    }

    private class MultiPartCustomPayload extends SPacketCustomPayload
    {
        private String channel;
        private byte[] data;
        private PacketBuffer data_buf = null;
        private int part_count = 0;
        private int part_expected = 0;
        private int offset = 0;

        private MultiPartCustomPayload(PacketBuffer preamble) throws IOException
        {
            channel = preamble.readString(20);
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
            int part = input.readByte() & 0xFF;
            if (part != part_expected)
            {
                throw new IOException("Received FML MultiPart packet out of order, Expected " + part_expected + " Got " + part);
            }
            int len = input.readableBytes();
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

    public ConnectionType getConnectionType()
    {
        return this.connectionType;
    }
}
