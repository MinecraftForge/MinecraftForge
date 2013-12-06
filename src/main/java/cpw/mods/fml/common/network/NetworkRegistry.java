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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.AttributeKey;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;

/**
 * @author cpw
 *
 */
public enum NetworkRegistry
{
    INSTANCE;
    private Map<String,FMLEmbeddedChannel> channels = Maps.newConcurrentMap();
    private Map<ModContainer, IGuiHandler> serverGuiHandlers = Maps.newHashMap();
    private Map<ModContainer, IGuiHandler> clientGuiHandlers = Maps.newHashMap();

    /**
     * Set in the {@link ChannelHandlerContext}
     */
    public static final AttributeKey<String> FML_CHANNEL = new AttributeKey<String>("fml:channelName");
    public static final AttributeKey<OutboundTarget> FML_MESSAGETARGET = new AttributeKey<OutboundTarget>("fml:outboundTarget");
    public static final AttributeKey<Object> FML_MESSAGETARGETARGS = new AttributeKey<Object>("fml:outboundTargetArgs");

    public static final byte FML_PROTOCOL = 1;

    private NetworkRegistry()
    {
    }

    public enum OutboundTarget {
        PLAYER, ALL, DIMENSION, ALLAROUNDPOINT;
    }

    static class FMLEmbeddedChannel extends EmbeddedChannel {
        private final String channelName;
        public FMLEmbeddedChannel(String channelName)
        {
            super();
            this.channelName = channelName;
        }
    }

    public EmbeddedChannel newChannel(String name)
    {
        if (channels.containsKey(name) || name.startsWith("MC|") || name.startsWith("\u0001"))
        {
            throw new RuntimeException("That channel is already registered");
        }
        FMLEmbeddedChannel channel = new FMLEmbeddedChannel(name);
        channels.put(name,channel);
        return channel;
    }

    public EmbeddedChannel getChannel(String name)
    {
        return channels.get(name);
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
            FMLLog.log(Level.SEVERE, "Mod %s attempted to register a gui network handler during a construction phase", mc.getModId());
            throw new RuntimeException("Invalid attempt to create a GUI during mod construction. Use an EventHandler instead");
        }
        NetworkModHolder nmh = mc.getNetworkModHolder();
        if (nmh == null)
        {
            FMLLog.log(Level.FINE, "The mod %s needs to be a @NetworkMod to register a Networked Gui Handler", mc.getModId());
        }
        else
        {
            serverGuiHandlers.put(mc, handler);
        }
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

    public boolean hasChannel(String channelName)
    {
        return channels.containsKey(channelName);
    }
}
