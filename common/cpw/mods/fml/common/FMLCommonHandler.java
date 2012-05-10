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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import cpw.mods.fml.common.ModContainer.TickType;

import net.minecraft.src.StringTranslate;

/**
 * The main class for non-obfuscated hook handling code
 * 
 * Anything that doesn't require obfuscated or client/server specific code should 
 * go in this handler
 * 
 * It also contains a reference to the sided handler instance that is valid
 * allowing for common code to access specific properties from the obfuscated world
 * without a direct dependency
 * 
 * @author cpw
 *
 */
public class FMLCommonHandler
{
    /**
     * The singleton
     */
    private static final FMLCommonHandler INSTANCE = new FMLCommonHandler();
    /**
     * A map of mods to their network channels
     */
    private Map<ModContainer, Set<String>> channelList = new HashMap<ModContainer, Set<String>>();
    /**
     * A map of channels to mods
     */
    private Map<String, ModContainer> modChannels = new HashMap<String, ModContainer>();
    /**
     * A map of active channels per player
     */
    private Map<Object, Set<String>> activeChannels = new HashMap<Object, Set<String>>();
    /**
     * The delegate for side specific data and functions 
     */
    private IFMLSidedHandler sidedDelegate;
    
    private int uniqueEntityListId = 220;

    private List<ModContainer> extraTickers = new ArrayList<ModContainer>();

    /**
     * We register our delegate here
     * @param handler
     */
    public void registerSidedDelegate(IFMLSidedHandler handler)
    {
        sidedDelegate = handler;
    }

    /**
     * Pre-tick the mods 
     */
    public void worldTickStart()
    {
        tickStart(ModContainer.TickType.WORLD,0.0f);
    }

    /**
     * Post-tick the mods
     */
    public void worldTickEnd()
    {
        tickEnd(ModContainer.TickType.WORLD,0.0f);
    }

    public void tickStart(TickType type, Object ... data)
    {
        for (ModContainer mod : Loader.getModList())
        {
            mod.tickStart(type, data);
        }
        for (ModContainer mod : extraTickers)
        {
            mod.tickStart(type, data);
        }
    }
    
    public void tickEnd(TickType type, Object ... data)
    {
        for (ModContainer mod : Loader.getModList())
        {
            mod.tickEnd(type, data);
        }
        for (ModContainer mod : extraTickers)
        {
            mod.tickEnd(type, data);
        }
    }
    
    public List<IKeyHandler> gatherKeyBindings() {
        List<IKeyHandler> allKeys=new ArrayList<IKeyHandler>();
        for (ModContainer mod : Loader.getModList())
        {
            allKeys.addAll(mod.getKeys());
        }
        for (ModContainer mod : extraTickers)
        {
            allKeys.addAll(mod.getKeys());
        }
        return allKeys;
    }
    /**
     * @return the instance
     */
    public static FMLCommonHandler instance()
    {
        return INSTANCE;
    }

    /**
     * Lookup the mod for a channel
     * @param channel
     * @return
     */
    public ModContainer getModForChannel(String channel)
    {
        return modChannels.get(channel);
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
     * register a channel to a mod
     * @param container
     * @param channelName
     */
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
        modChannels.put(channelName, container);
    }

    /**
     * Activate the channel for the player
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
     * Deactivate the channel for the player
     * @param player
     * @param channel
     */
    public void deactivateChannel(Object player, String channel)
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
     * Get the packet 250 channel registration string
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
     * Is the specified channel active for the player?
     * @param channel
     * @param player
     * @return
     */
    public boolean isChannelActive(String channel, Object player)
    {
        return activeChannels.get(player).contains(channel);
    }

    /**
     * Get the forge mod loader logging instance (goes to the forgemodloader log file)
     * @return
     */
    public Logger getFMLLogger()
    {
        return Loader.log;
    }

    /**
     * Get the minecraft logger (goes to the server log file)
     * @return
     */
    public Logger getMinecraftLogger()
    {
        return sidedDelegate.getMinecraftLogger();
    }

    /**
     * Is this a modloader mod?
     * @param clazz
     * @return
     */
    public boolean isModLoaderMod(Class<?> clazz)
    {
        return sidedDelegate.isModLoaderMod(clazz);
    }

    /**
     * Load the modloader mod
     * @param clazz
     * @param canonicalPath
     * @return
     */
    public ModContainer loadBaseModMod(Class<?> clazz, File canonicalFile)
    {
        return sidedDelegate.loadBaseModMod(clazz, canonicalFile);
    }
    
    public File getMinecraftRootDirectory() {
        return sidedDelegate.getMinecraftRootDirectory();
    }

    /**
     * @return
     */
    public Object getMinecraftInstance()
    {
        return sidedDelegate.getMinecraftInstance();
    }

    /**
     * @return
     */
    public int nextUniqueEntityListId()
    {
        return uniqueEntityListId++;
    }

    private Map<String,Properties> modLanguageData=new HashMap<String,Properties>();
    /**
     * @param key
     * @param lang
     * @param value
     */
    public void addStringLocalization(String key, String lang, String value)
    {
        Properties langPack=modLanguageData.get(lang);
        if (langPack==null) {
            langPack=new Properties();
            modLanguageData.put(lang, langPack);
        }
        langPack.put(key,value);
        
        if (sidedDelegate.getCurrentLanguage().equals(lang)) {
            handleLanguageLoad(langPack, lang);
        }
    }

    /**
     * @param languagePack
     * @param lang
     */
    public void handleLanguageLoad(Properties languagePack, String lang)
    {
        Properties langPack=modLanguageData.get(lang);
        if (langPack==null) {
            return;
        }
        languagePack.putAll(langPack);
    }

    /**
     * @return
     */
    public boolean isServer()
    {
        return sidedDelegate.isServer();
    }

    /**
     * @return
     */
    public boolean isClient()
    {
        return sidedDelegate.isClient();
    }
    
    public void registerTicker(ModContainer ticker)
    {
        extraTickers.add(ticker);
    }
}
