/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;

/**
 * @author cpw
 *
 */
public enum NetworkRegistry
{
    INSTANCE;
    private EnumMap<Side,Map<String,FMLEmbeddedChannel>> channels = Maps.newEnumMap(Side.class);
    private Map<ModContainer, NetworkModHolder> registry = Maps.newHashMap();
    private Map<ModContainer, IGuiHandler> serverGuiHandlers = Maps.newHashMap();
    private Map<ModContainer, IGuiHandler> clientGuiHandlers = Maps.newHashMap();

    /**
     * Set in the {@link ChannelHandlerContext}
     */
    public static final AttributeKey<String> FML_CHANNEL = new AttributeKey<String>("fml:channelName");
    public static final AttributeKey<Side> CHANNEL_SOURCE = new AttributeKey<Side>("fml:channelSource");
    public static final AttributeKey<OutboundTarget> FML_MESSAGETARGET = new AttributeKey<OutboundTarget>("fml:outboundTarget");
    public static final AttributeKey<Object> FML_MESSAGETARGETARGS = new AttributeKey<Object>("fml:outboundTargetArgs");
    public static final AttributeKey<ModContainer> MOD_CONTAINER = new AttributeKey<ModContainer>("fml:modContainer");

    public static final byte FML_PROTOCOL = 1;

    private NetworkRegistry()
    {
        channels.put(Side.CLIENT, Maps.<String,FMLEmbeddedChannel>newConcurrentMap());
        channels.put(Side.SERVER, Maps.<String,FMLEmbeddedChannel>newConcurrentMap());
    }

    public class TargetPoint {
        public TargetPoint(int dimension, double x, double y, double z, double range)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.range = range;
            this.dimension = dimension;
        }
        public final double x;
        public final double y;
        public final double z;
        public final double range;
        public final int dimension;
    }
    public enum OutboundTarget {
        PLAYER
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof EntityPlayerMP))
                {
                    throw new RuntimeException("PLAYER target expects a Player arg");
                }
            }
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args)
            {
                EntityPlayerMP player = (EntityPlayerMP) args;
                NetworkDispatcher dispatcher = player.field_71135_a.field_147371_a.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                return ImmutableList.of(dispatcher);
            }
        },
        ALL
        {
            @Override
            public void validateArgs(Object args)
            {
            }
            @SuppressWarnings("unchecked")
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args)
            {
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.<NetworkDispatcher>builder();
                for (EntityPlayerMP player : (List<EntityPlayerMP>)FMLCommonHandler.instance().getMinecraftServerInstance().func_71203_ab().field_72404_b)
                {
                    NetworkDispatcher dispatcher = player.field_71135_a.field_147371_a.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                    builder.add(dispatcher);
                }
                return builder.build();
            }
        },
        DIMENSION
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof Integer))
                {
                    throw new RuntimeException("DIMENSION expects an integer argument");
                }
            }
            @SuppressWarnings("unchecked")
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args)
            {
                int dimension = (Integer)args;
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.<NetworkDispatcher>builder();
                for (EntityPlayerMP player : (List<EntityPlayerMP>)FMLCommonHandler.instance().getMinecraftServerInstance().func_71203_ab().field_72404_b)
                {
                    if (dimension == player.field_71093_bK)
                    {
                        NetworkDispatcher dispatcher = player.field_71135_a.field_147371_a.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                        builder.add(dispatcher);
                    }
                }
                return builder.build();
            }
        },
        ALLAROUNDPOINT
        {
            @Override
            public void validateArgs(Object args)
            {
                if (!(args instanceof TargetPoint))
                {
                    throw new RuntimeException("ALLAROUNDPOINT expects a TargetPoint argument");
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args)
            {
                TargetPoint tp = (TargetPoint)args;
                ImmutableList.Builder<NetworkDispatcher> builder = ImmutableList.<NetworkDispatcher>builder();
                for (EntityPlayerMP player : (List<EntityPlayerMP>)FMLCommonHandler.instance().getMinecraftServerInstance().func_71203_ab().field_72404_b)
                {
                    if (player.field_71093_bK == tp.dimension)
                    {
                        double d4 = tp.x - player.field_70165_t;
                        double d5 = tp.y - player.field_70163_u;
                        double d6 = tp.z - player.field_70161_v;

                        if (d4 * d4 + d5 * d5 + d6 * d6 < tp.range * tp.range)
                        {
                            NetworkDispatcher dispatcher = player.field_71135_a.field_147371_a.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
                            builder.add(dispatcher);
                        }
                    }
                }
                return builder.build();
            }
        },
        TOSERVER
        {
            @Override
            public void validateArgs(Object args)
            {
                throw new RuntimeException("Cannot set TOSERVER as a target on the server");
            }
            @Override
            public List<NetworkDispatcher> selectNetworks(Object args)
            {
                NetworkManager clientConnection = FMLCommonHandler.instance().getClientToServerNetworkManager();
                return clientConnection == null ? ImmutableList.<NetworkDispatcher>of() : ImmutableList.of(clientConnection.channel().attr(NetworkDispatcher.FML_DISPATCHER).get());
            }
        };

        public abstract void validateArgs(Object args);
        public abstract List<NetworkDispatcher> selectNetworks(Object args);
    }

    static class FMLEmbeddedChannel extends EmbeddedChannel {
        public FMLEmbeddedChannel(String channelName, Side source, ChannelHandler... handlers)
        {
            this(Loader.instance().activeModContainer(), channelName, source, handlers);
        }
        public FMLEmbeddedChannel(ModContainer container, String channelName, Side source, ChannelHandler... handlers)
        {
            super(handlers);
            this.attr(FML_CHANNEL).set(channelName);
            this.attr(CHANNEL_SOURCE).set(source);
            this.attr(MOD_CONTAINER).setIfAbsent(container);
            this.pipeline().addFirst(new FMLOutboundHandler());
        }
    }

    /**
     * Create a new synchronous message channel pair based on netty.
     * There are two channels created : one for each logical side (considered as the source of an outbound message)
     * The returned map will contain a value for each logical side, though both will only be working in the
     * integrated server case.
     *
     * The channel expects to read and write using {@link FMLProxyPacket}. All operation is synchronous, as the
     * asynchronous behaviour occurs at a lower level in netty.
     *
     * The first handler in the pipeline is special and should not be removed or moved from the head - it transforms
     * packets from the outbound of this pipeline into custom packets, based on the current {@link AttributeKey} value
     * {@link NetworkRegistry#FML_MESSAGETARGET} and {@link NetworkRegistry#FML_MESSAGETARGETARGS} set on the channel.
     * For the client to server channel (source side : CLIENT) this is fixed as "TOSERVER". For SERVER to CLIENT packets,
     * several possible values exist.
     *
     * Mod Messages should be transformed using a something akin to a {@link MessageToMessageCodec}. FML provides
     * a utility codec, {@link FMLIndexedMessageToMessageCodec} that transforms from {@link FMLProxyPacket} to a mod
     * message using a message discriminator byte. This is optional, but highly recommended for use.
     *
     * Note also that the handlers supplied need to be {@link ChannelHandler.Shareable} - they are injected into two
     * channels.
     *
     * @param name
     * @param handlers
     * @return
     */
    public EnumMap<Side,EmbeddedChannel> newChannel(String name, ChannelHandler... handlers)
    {
        if (channels.containsKey(name) || name.startsWith("MC|") || name.startsWith("\u0001") || name.startsWith("FML"))
        {
            throw new RuntimeException("That channel is already registered");
        }
        EnumMap<Side,EmbeddedChannel> result = Maps.newEnumMap(Side.class);

        for (Side side : Side.values())
        {
            FMLEmbeddedChannel channel = new FMLEmbeddedChannel(name, side, handlers);
            channels.get(side).put(name,channel);
            result.put(side, channel);
        }
        return result;
    }

    public EnumMap<Side,EmbeddedChannel> newChannel(ModContainer container, String name, ChannelHandler... handlers)
    {
        if (channels.containsKey(name) || name.startsWith("MC|") || name.startsWith("\u0001") || (name.startsWith("FML") && !("FML".equals(container.getModId()))))
        {
            throw new RuntimeException("That channel is already registered");
        }
        EnumMap<Side,EmbeddedChannel> result = Maps.newEnumMap(Side.class);

        for (Side side : Side.values())
        {
            FMLEmbeddedChannel channel = new FMLEmbeddedChannel(container, name, side, handlers);
            channels.get(side).put(name,channel);
            result.put(side, channel);
        }
        return result;
    }

    public EmbeddedChannel getChannel(String name, Side source)
    {
        return channels.get(source).get(name);
    }
/*
    *//**
     * Get the packet 250 channel registration string
     * @return the {@link Packet250CustomPayload} channel registration string
     *//*
    byte[] getPacketRegistry(Side side)
    {
        return Joiner.on('\0').join(Iterables.concat(Arrays.asList("FML"),universalPacketHandlers.keySet(), side.isClient() ? clientPacketHandlers.keySet() : serverPacketHandlers.keySet())).getBytes(Charsets.UTF_8);
    }
    *//**
     * Is the specified channel active for the player?
     * @param channel
     * @param player
     *//*
    public boolean isChannelActive(String channel, Player player)
    {
        return activeChannels.containsEntry(player,channel);
    }
    *//**
     * register a channel to a mod
     * @param handler the packet handler
     * @param channelName the channel name to register it with
     *//*
    public void registerChannel(IPacketHandler handler, String channelName)
    {
        if (Strings.isNullOrEmpty(channelName) || (channelName!=null && channelName.length()>16))
        {
            FMLLog.severe("Invalid channel name '%s' : %s", channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)");
            throw new RuntimeException("Channel name is invalid");

        }
        universalPacketHandlers.put(channelName, handler);
    }

    public void registerChannel(IPacketHandler handler, String channelName, Side side)
    {
        if (side == null)
        {
            registerChannel(handler, channelName);
            return;
        }
        if (Strings.isNullOrEmpty(channelName) || (channelName!=null && channelName.length()>16))
        {
            FMLLog.severe("Invalid channel name '%s' : %s", channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)");
            throw new RuntimeException("Channel name is invalid");

        }
        if (side.isClient())
        {
            clientPacketHandlers.put(channelName, handler);
        }
        else
        {
            serverPacketHandlers.put(channelName, handler);
        }
    }
    *//**
     * Activate the channel for the player
     * @param player
     *//*
    void activateChannel(Player player, String channel)
    {
        activeChannels.put(player, channel);
    }
    *//**
     * Deactivate the channel for the player
     * @param player
     * @param channel
     *//*
    void deactivateChannel(Player player, String channel)
    {
        activeChannels.remove(player, channel);
    }
    *//**
     * Register a connection handler
     *
     * @param handler
     *//*
    public void registerConnectionHandler(IConnectionHandler handler)
    {
        connectionHandlers.add(handler);
    }

    *//**
     * Register a chat listener
     * @param listener
     *//*
    public void registerChatListener(IChatListener listener)
    {
        chatListeners.add(listener);
    }

    void playerLoggedIn(EntityPlayerMP player, NetServerHandler netHandler, INetworkManager manager)
    {
        generateChannelRegistration(player, netHandler, manager);
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.playerLoggedIn((Player)player, netHandler, manager);
        }
    }

    String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            String kick = handler.connectionReceived(netHandler, manager);
            if (!Strings.isNullOrEmpty(kick))
            {
                return kick;
            }
        }
        return null;
    }

    void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager networkManager)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionOpened(netClientHandler, server, port, networkManager);
        }
    }

    void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager networkManager)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionOpened(netClientHandler, server, networkManager);
        }
    }

    void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
    {
        generateChannelRegistration(clientHandler.getPlayer(), clientHandler, manager);
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.clientLoggedIn(clientHandler, manager, login);
        }
    }

    void connectionClosed(INetworkManager manager, EntityPlayer player)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionClosed(manager);
        }
        activeChannels.removeAll(player);
    }

    void generateChannelRegistration(EntityPlayer player, NetHandler netHandler, INetworkManager manager)
    {
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.field_73630_a = "REGISTER";
        pkt.field_73629_c = getPacketRegistry(player instanceof EntityPlayerMP ? Side.SERVER : Side.CLIENT);
        pkt.field_73628_b = pkt.field_73629_c.length;
        manager.func_74429_a(pkt);
    }

    void handleCustomPacket(Packet250CustomPayload packet, INetworkManager network, NetHandler handler)
    {
        if ("REGISTER".equals(packet.field_73630_a))
        {
            handleRegistrationPacket(packet, (Player)handler.getPlayer());
        }
        else if ("UNREGISTER".equals(packet.field_73630_a))
        {
            handleUnregistrationPacket(packet, (Player)handler.getPlayer());
        }
        else
        {
            handlePacket(packet, network, (Player)handler.getPlayer());
        }
    }


    private void handlePacket(Packet250CustomPayload packet, INetworkManager network, Player player)
    {
        String channel = packet.field_73630_a;
        for (IPacketHandler handler : Iterables.concat(universalPacketHandlers.get(channel), player instanceof EntityPlayerMP ? serverPacketHandlers.get(channel) : clientPacketHandlers.get(channel)))
        {
            handler.onPacketData(network, packet, player);
        }
    }

    private void handleRegistrationPacket(Packet250CustomPayload packet, Player player)
    {
        List<String> channels = extractChannelList(packet);
        for (String channel : channels)
        {
            activateChannel(player, channel);
        }
    }
    private void handleUnregistrationPacket(Packet250CustomPayload packet, Player player)
    {
        List<String> channels = extractChannelList(packet);
        for (String channel : channels)
        {
            deactivateChannel(player, channel);
        }
    }

    private List<String> extractChannelList(Packet250CustomPayload packet)
    {
        String request = new String(packet.field_73629_c, Charsets.UTF_8);
        List<String> channels = Lists.newArrayList(Splitter.on('\0').split(request));
        return channels;
    }
*/
    public void registerGuiHandler(Object mod, IGuiHandler handler)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null)
        {
            FMLLog.log(Level.SEVERE, "Mod of type %s attempted to register a gui network handler during a construction phase", mod.getClass().getName());
            throw new RuntimeException("Invalid attempt to create a GUI during mod construction. Use an EventHandler instead");
        }
        serverGuiHandlers.put(mc, handler);
        clientGuiHandlers.put(mc, handler);
    }
/*    void openRemoteGui(ModContainer mc, EntityPlayerMP player, int modGuiId, World world, int x, int y, int z)
    {
        IGuiHandler handler = serverGuiHandlers.get(mc);
        NetworkModHolder nmh = mc.getNetworkModHolder();
        if (handler != null && nmh != null)
        {
            Container container = (Container)handler.getServerGuiElement(modGuiId, player, world, x, y, z);
            if (container != null)
            {
                player.func_71117_bO();
                player.func_71128_l();
                int windowId = player.field_71139_cq;
                Packet pkt = PacketManager.INSTANCE.makeGuiPacket(windowId, nmh.getNetworkId(), modGuiId, x, y, z);
                player.field_71135_a.func_72567_b(pkt);
                player.field_71070_bA = container;
                player.field_71070_bA.field_75152_c = windowId;
                player.field_71070_bA.func_75132_a(player);
            }
        }
    }
*/    void openLocalGui(ModContainer mc, EntityPlayer player, int modGuiId, World world, int x, int y, int z)
    {
        IGuiHandler handler = clientGuiHandlers.get(mc);
        FMLCommonHandler.instance().showGuiScreen(handler.getClientGuiElement(modGuiId, player, world, x, y, z));
    }

    public boolean hasChannel(String channelName, Side source)
    {
        return channels.get(source).containsKey(channelName);
    }

    public void register(ModContainer fmlModContainer, Class<?> clazz, String remoteVersionRange, ASMDataTable asmHarvestedData)
    {
        NetworkModHolder networkModHolder = new NetworkModHolder(fmlModContainer, clazz, remoteVersionRange, asmHarvestedData);
        registry.put(fmlModContainer, networkModHolder);
    }

    Map<ModContainer,NetworkModHolder> registry()
    {
        return registry;
    }
}
