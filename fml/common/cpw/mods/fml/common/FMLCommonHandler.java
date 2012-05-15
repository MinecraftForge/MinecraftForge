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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import cpw.mods.fml.common.ModContainer.SourceType;
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
    private static final Pattern metadataFile = Pattern.compile("$/modinfo.json$");;
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

    private List<ModContainer> auxilliaryContainers = new ArrayList<ModContainer>();

    private Map<String,Properties> modLanguageData=new HashMap<String,Properties>();
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
        sidedDelegate.profileStart("modTickStart");
        sidedDelegate.profileStart(type.name());
        for (ModContainer mod : Loader.getModList())
        {
            sidedDelegate.profileStart(mod.getName());
            mod.tickStart(type, data);
            sidedDelegate.profileEnd();
        }
        for (ModContainer mod : auxilliaryContainers)
        {
            sidedDelegate.profileStart(mod.getMod().getClass().getSimpleName());
            mod.tickStart(type, data);
            sidedDelegate.profileEnd();
        }
        sidedDelegate.profileEnd();
        sidedDelegate.profileEnd();
    }
    
    public void tickEnd(TickType type, Object ... data)
    {
        sidedDelegate.profileStart("modTickEnd");
        sidedDelegate.profileStart(type.name());
        for (ModContainer mod : Loader.getModList())
        {
            sidedDelegate.profileStart(mod.getName());
            mod.tickEnd(type, data);
            sidedDelegate.profileEnd();
        }
        for (ModContainer mod : auxilliaryContainers)
        {
            sidedDelegate.profileStart(mod.getMod().getClass().getSimpleName());
            mod.tickEnd(type, data);
            sidedDelegate.profileEnd();
        }
        sidedDelegate.profileEnd();
        sidedDelegate.profileEnd();
    }
    
    public List<IKeyHandler> gatherKeyBindings() {
        List<IKeyHandler> allKeys=new ArrayList<IKeyHandler>();
        for (ModContainer mod : Loader.getModList())
        {
            allKeys.addAll(mod.getKeys());
        }
        for (ModContainer mod : auxilliaryContainers)
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
        
        handleLanguageLoad(sidedDelegate.getCurrentLanguageTable(), lang);
    }

    /**
     * @param languagePack
     * @param lang
     */
    public void handleLanguageLoad(Properties languagePack, String lang)
    {
        Properties usPack=modLanguageData.get("en_US");
        if (usPack!=null) {
            languagePack.putAll(usPack);
        }
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
    
    public void addAuxilliaryModContainer(ModContainer ticker)
    {
        auxilliaryContainers.add(ticker);
    }

    /**
     * Called from the furnace to lookup fuel values
     * 
     * @param itemId
     * @param itemDamage
     * @return
     */
    public int fuelLookup(int itemId, int itemDamage)
    {
        int fv = 0;
    
        for (ModContainer mod : Loader.getModList())
        {
            fv = Math.max(fv, mod.lookupFuelValue(itemId, itemDamage));
        }
    
        return fv;
    }
    
    public void addNameForObject(Object minecraftObject, String lang, String name) {
        String label=sidedDelegate.getObjectName(minecraftObject);
        addStringLocalization(label, lang, name);
    }
    
    
    /**
     * Raise an exception
     * 
     * @param exception
     * @param message
     * @param stopGame
     */
    public void raiseException(Throwable exception, String message, boolean stopGame)
    {
        FMLCommonHandler.instance().getFMLLogger().throwing("FMLHandler", "raiseException", exception);
        throw new RuntimeException(exception);
    }

    /**
     * @param string
     * @return
     */
    public String[] getBrandingStrings(String mcVersion)
    {
        ArrayList<String> brandings=new ArrayList<String>();
        brandings.add(mcVersion);
        brandings.add(Loader.instance().getFMLVersionString());
        try {
            brandings.add((String)Class.forName("forge.MinecraftForge").getMethod("getVersionString").invoke(null));
        } catch (Exception ex) {
            // Ignore- forge isn't loaded
        }
        try {
            Properties props=new Properties();
            props.load(FMLCommonHandler.class.getClassLoader().getResourceAsStream("fmlbranding.properties"));
            brandings.add(props.getProperty("fmlbranding"));
        } catch (Exception ex) {
            // Ignore - no branding file found
        }
        brandings.add(String.format("%d mod%s loaded",Loader.getModList().size(), Loader.getModList().size()>1?"s":""));
        Collections.reverse(brandings);
        return brandings.toArray(new String[brandings.size()]);
    }

    /**
     * @param mod
     */
    public void loadMetadataFor(ModContainer mod)
    {
        if (mod.getSourceType()==SourceType.JAR) {
            try
            {
                ZipFile jar = new ZipFile(mod.getSource());
                ZipEntry infoFile=jar.getEntry("mcmod.info");
                if (infoFile!=null) {
                    InputStream input=jar.getInputStream(infoFile);
                    ModMetadata data=sidedDelegate.readMetadataFrom(input, mod);
                    mod.setMetadata(data);
                }
            }
            catch (Exception e)
            {
                // Something wrong but we don't care
            }
        } else {
            try
            {
                InputStream input=Loader.instance().getModClassLoader().getResourceAsStream(mod.getName()+".info");
                if (input==null) {
                    input=Loader.instance().getModClassLoader().getResourceAsStream("net/minecraft/src/"+mod.getName()+".info");
                }
                if (input!=null) {
                    ModMetadata data=sidedDelegate.readMetadataFrom(input, mod);
                    mod.setMetadata(data);
                }
            }
            catch (Exception e)
            {
                // Something wrong but we don't care
            }
        }
    }

}
