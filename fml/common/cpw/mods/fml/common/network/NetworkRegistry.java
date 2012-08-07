package cpw.mods.fml.common.network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;

public class NetworkRegistry
{

    private static final NetworkRegistry INSTANCE = new NetworkRegistry();
    /**
     * A map of active channels per player
     */
    private Multimap<Player, String> activeChannels = ArrayListMultimap.create();
    /**
     * A map of the packet handlers for packets
     */
    private Multimap<String, IPacketHandler> packetHandlers = ArrayListMultimap.create();
    /**
     * A linked set of registered connection handlers
     */
    private Set<IConnectionHandler> connectionHandlers = Sets.newLinkedHashSet();

    public static NetworkRegistry instance()
    {
        return INSTANCE;
    }
    /**
     * Get the packet 250 channel registration string
     * @return
     */
    byte[] getPacketRegistry()
    {
        return Joiner.on('\0').join(packetHandlers.keySet()).getBytes(Charsets.UTF_8);
    }
    /**
     * Is the specified channel active for the player?
     * @param channel
     * @param player
     * @return
     */
    public boolean isChannelActive(String channel, Player player)
    {
        return activeChannels.containsEntry(player,channel);
    }
    /**
     * register a channel to a mod
     * @param container
     * @param channelName
     */
    public void registerChannel(IPacketHandler handler, String channelName)
    {
        if ("FML".equals(channelName) || (channelName != null && channelName.startsWith("MC|")))
        {
            FMLLog.severe("Illegal attempt to register a special channel %s", channelName);
            throw new FMLNetworkException();
        }

        packetHandlers.put(channelName, handler);
//        invertedChannelList = Multimaps.invertFrom(channelList, ArrayListMultimap.<String, ModContainer>create());
    }
    /**
     * Activate the channel for the player
     * @param player
     */
    void activateChannel(Player player, String channel)
    {
        activeChannels.put(player, channel);
    }
    /**
     * Deactivate the channel for the player
     * @param player
     * @param channel
     */
    void deactivateChannel(Player player, String channel)
    {
        activeChannels.remove(player, channel);
    }
    /**
     * Register a connection handler
     *
     * @param handler
     */
    public void registerConnectionHandler(IConnectionHandler handler)
    {
        connectionHandlers.add(handler);
    }

    void playerLoggedIn(EntityPlayerMP player, NetServerHandler netHandler, NetworkManager manager)
    {
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.playerLoggedIn((Player)player, netHandler, manager);
        }
        generateChannelRegistration(player, netHandler, manager);
    }

    void generateChannelRegistration(EntityPlayer player, NetHandler netHandler, NetworkManager manager)
    {
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.field_73630_a = "REGISTER";
        pkt.field_73629_c = getPacketRegistry();
        pkt.field_73628_b = pkt.field_73629_c.length;
        manager.func_74429_a(pkt);
    }

    void handleCustomPacket(Packet250CustomPayload packet, NetworkManager network, NetHandler handler)
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


    private void handlePacket(Packet250CustomPayload packet, NetworkManager network, Player player)
    {
        if (activeChannels.containsEntry(player, packet.field_73630_a))
        {
            for (IPacketHandler handler : packetHandlers.get(packet.field_73630_a))
            {
                handler.onPacketData(network, packet, player);
            }
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
    /**
     * @param packet
     * @return
     */
    private List<String> extractChannelList(Packet250CustomPayload packet)
    {
        String request = new String(packet.field_73629_c, Charsets.UTF_8);
        List<String> channels = Lists.newArrayList(Splitter.on('\0').split(request));
        return channels;
    }
}
