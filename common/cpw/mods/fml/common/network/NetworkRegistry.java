package cpw.mods.fml.common.network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.src.EntityPlayer;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

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
     * A map of mods to their network channels - the list is a set
     */
    private SetMultimap<ModContainer, String> channelList = LinkedHashMultimap.create();

    public static NetworkRegistry instance()
    {
        return INSTANCE;
    }
    /**
     * Get the channel list for a mod
     * @param modLoaderModContainer
     * @return
     */
    public Set<String> getChannelListFor(ModContainer container)
    {
        return channelList.get(container);
    }
    /**
     * Get the packet 250 channel registration string
     * @return
     */
    public byte[] getPacketRegistry()
    {
        return Joiner.on('\0').join(channelList.values()).getBytes(Charsets.UTF_8);
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
    public void registerChannel(ModContainer container, String channelName)
    {
        channelList.put(container, channelName);
//        invertedChannelList = Multimaps.invertFrom(channelList, ArrayListMultimap.<String, ModContainer>create());
    }
    /**
     * Activate the channel for the player
     * @param player
     */
    public void activateChannel(Player player, String channel)
    {
        activeChannels.put(player, channel);
    }
    /**
     * Deactivate the channel for the player
     * @param player
     * @param channel
     */
    public void deactivateChannel(Player player, String channel)
    {
        activeChannels.remove(player, channel);
    }
}
