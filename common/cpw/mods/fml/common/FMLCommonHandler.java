/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.src.EntityPlayer;



public class FMLCommonHandler
{
    private static final FMLCommonHandler INSTANCE = new FMLCommonHandler();
    private Map<ModContainer, Set<String>> channelList = new HashMap<ModContainer, Set<String>>();
    private Map<String, ModContainer> modChannels = new HashMap<String, ModContainer>();
    private Map<Object, Set<String>> activeChannels = new HashMap<Object, Set<String>>();
    private IFMLSidedHandler sidedDelegate;
    
    private FMLCommonHandler() {
        try {
            Class.forName("net.minecraft.client.Minecraft");
            sidedDelegate=(IFMLSidedHandler) Class.forName("cpw.mods.fml.client.FMLClientHandler").newInstance();
        } catch (Exception ex) {
            try {
                sidedDelegate=(IFMLSidedHandler) Class.forName("cpw.mods.fml.server.FMLServerHandler").newInstance();
            } catch (Exception ex2) {
                Loader.log.severe("A severe installation issue has occured with FML, we cannot continue");
                throw new LoaderException(ex2);
            }
        }
    }
    
    public void gameTickStart()
    {
        for (ModContainer mod : Loader.getModList())
        {
            mod.tickStart();
        }
    }

    public void gameTickEnd()
    {
        for (ModContainer mod : Loader.getModList())
        {
            mod.tickEnd();
        }
    }

    /**
     * @return the instance
     */
    public static FMLCommonHandler instance()
    {
        return INSTANCE;
    }

    public ModContainer getModForChannel(String channel)
    {
        return modChannels.get(channel);
    }
    /**
     * @param modLoaderModContainer
     * @return
     */
    public Set<String> getChannelListFor(ModContainer container)
    {
        return channelList.get(container);
    }

    public void registerChannel(ModContainer container, String channelName)
    {
        if (modChannels.containsKey(channelName))
        {
            // NOOP
        }

        Set<String> list = channelList.get(container);

        if (list == null)
        {
            list = new HashSet<String>();
            channelList.put(container, list);
        }

        list.add(channelName);
    }

    /**
     * @param player
     */
    public void activateChannel(Object player, String channel)
    {
        Set<String> active = activeChannels.get(player);

        if (active == null)
        {
            active = new HashSet<String>();
            activeChannels.put(player, active);
        }

        active.add(channel);
    }

    /**
     * @param player
     * @param channel
     */
    public void deactivateChannel(EntityPlayer player, String channel)
    {
        Set<String> active = activeChannels.get(player);

        if (active == null)
        {
            active = new HashSet<String>();
            activeChannels.put(player, active);
        }

        active.remove(channel);
    }

    /**
     * @return
     */
    public byte[] getPacketRegistry()
    {
        StringBuffer sb = new StringBuffer();

        for (String chan : modChannels.keySet())
        {
            sb.append(chan).append("\0");
        }

        try
        {
            return sb.toString().getBytes("UTF8");
        }
        catch (UnsupportedEncodingException e)
        {
            Loader.log.warning("Error building registration list");
            Loader.log.throwing("FMLHooks", "getPacketRegistry", e);
            return new byte[0];
        }
    }

    /**
     * @param channel
     * @param player
     * @return
     */
    public boolean isChannelActive(String channel, Object player)
    {
        return activeChannels.get(player).contains(channel);
    }
    
    public Logger getFMLLogger() {
        return Loader.log;
    }
    
    public Logger getMinecraftLogger() {
        return sidedDelegate.getMinecraftLogger();
    }

    public boolean isModLoaderMod(Class<?> clazz)
    {
        return sidedDelegate.isModLoaderMod(clazz);
    }

    public ModContainer loadBaseModMod(Class<?> clazz, String canonicalPath)
    {
        return sidedDelegate.loadBaseModMod(clazz, canonicalPath);
    }
}
