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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import cpw.mods.fml.common.ModContainer.SourceType;

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

    private PriorityQueue<TickQueueElement> tickHandlers = new PriorityQueue<TickQueueElement>();

    private List<IScheduledTickHandler> scheduledTicks = new ArrayList<IScheduledTickHandler>();

    private Set<IWorldGenerator> worldGenerators = new HashSet<IWorldGenerator>();
    /**
     * We register our delegate here
     * @param handler
     */

    private static class TickQueueElement implements Comparable<TickQueueElement>
    {
        static long tickCounter = 0;
        public TickQueueElement(IScheduledTickHandler ticker)
        {
            this.ticker = ticker;
            update();
        }
        @Override
        public int compareTo(TickQueueElement o)
        {
            return (int)(next - o.next);
        }

        public void update()
        {
            next = tickCounter + Math.max(ticker.nextTickSpacing(),1);
        }

        private long next;
        private IScheduledTickHandler ticker;

        public boolean scheduledNow()
        {
            return tickCounter >= next;
        }
    }

    public void beginLoading(IFMLSidedHandler handler)
    {
        sidedDelegate = handler;
        getFMLLogger().info("Attempting early MinecraftForge initialization");
        callForgeMethod("initialize");
        getFMLLogger().info("Completed early MinecraftForge initialization");
    }

    public void rescheduleTicks()
    {
        sidedDelegate.profileStart("modTickScheduling");
        TickQueueElement.tickCounter++;
        scheduledTicks.clear();
        while (true)
        {
            if (tickHandlers.size()==0 || !tickHandlers.peek().scheduledNow())
            {
                break;
            }
            TickQueueElement tickQueueElement  = tickHandlers.poll();
            tickQueueElement.update();
            tickHandlers.offer(tickQueueElement);
            scheduledTicks.add(tickQueueElement.ticker);
        }
        sidedDelegate.profileEnd();
    }
    public void tickStart(EnumSet<TickType> ticks, Object ... data)
    {
        if (scheduledTicks.size()==0)
        {
            return;
        }
        sidedDelegate.profileStart("modTickStart$"+ticks);
        for (IScheduledTickHandler ticker : scheduledTicks)
        {
            EnumSet<TickType> ticksToRun = EnumSet.copyOf(ticker.ticks());
            ticksToRun.removeAll(EnumSet.complementOf(ticks));
            if (!ticksToRun.isEmpty())
            {
                sidedDelegate.profileStart(ticker.getLabel());
                ticker.tickStart(ticksToRun, data);
                sidedDelegate.profileEnd();
            }
        }
        sidedDelegate.profileEnd();
    }

    public void tickEnd(EnumSet<TickType> ticks, Object ... data)
    {
        if (scheduledTicks.size()==0)
        {
            return;
        }
        sidedDelegate.profileStart("modTickEnd$"+ticks);
        for (IScheduledTickHandler ticker : scheduledTicks)
        {
            EnumSet<TickType> ticksToRun = EnumSet.copyOf(ticker.ticks());
            ticksToRun.removeAll(EnumSet.complementOf(ticks));
            if (!ticksToRun.isEmpty())
            {
                sidedDelegate.profileStart(ticker.getLabel());
                ticker.tickEnd(ticksToRun, data);
                sidedDelegate.profileEnd();
            }
        }
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
     * Find the container that associates with the supplied mod object
     * @param mod
     * @return
     */
    public ModContainer findContainerFor(Object mod)
    {
        for (ModContainer mc : Loader.getModList())
        {
            if (mc.matches(mod))
            {
                return mc;
            }
        }
        return null;
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
        if (sidedDelegate == null)
        {
            throw new RuntimeException("sidedDelegate null when attempting to getMinecraftLogger, this is generally caused by you not installing FML properly, " +
            "or installing some other mod that edits Minecraft.class on top of FML such as ModLoader, do not do this. Reinstall FML properly and try again.");
        }
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

    public Side getSide()
    {
        return sidedDelegate.getSide();
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
        if (stopGame)
        {
            getSidedDelegate().haltGame(message,exception);
        }
    }


    private Class<?> forge;
    private boolean noForge;

    private Class<?> findMinecraftForge()
    {
        if (forge==null && !noForge)
        {
            try {
                forge = Class.forName("forge.MinecraftForge");
            } catch (Exception ex) {
                try {
                    forge = Class.forName("net.minecraft.src.forge.MinecraftForge");
                } catch (Exception ex2) {
                    // Ignore- forge isn't loaded
                    noForge = true;
                }
            }
        }
        return forge;
    }

    private Object callForgeMethod(String method)
    {
        if (noForge)
            return null;
        try
        {
            return findMinecraftForge().getMethod(method).invoke(null);
        }
        catch (Exception e)
        {
            // No Forge installation
            return null;
        }
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
        String forgeVersion = (String)callForgeMethod("getVersionString");
        if (forgeVersion != null)
        {
            brandings.add(forgeVersion);
        }
        brandings.addAll(sidedDelegate.getAdditionalBrandingInformation());
        try {
            Properties props=new Properties();
            props.load(FMLCommonHandler.class.getClassLoader().getResourceAsStream("fmlbranding.properties"));
            brandings.add(props.getProperty("fmlbranding"));
        } catch (Exception ex) {
            // Ignore - no branding file found
        }
        brandings.add(String.format("%d mod%s loaded",Loader.getModList().size(), Loader.getModList().size()!=1?"s":""));
        Collections.reverse(brandings);
        return brandings.toArray(new String[brandings.size()]);
    }

    /**
     * @param mod
     */
    public void loadMetadataFor(ModContainer mod)
    {
        if (mod.getSourceType()==SourceType.JAR) {
            ZipFile jar = null;
            try
            {
                jar = new ZipFile(mod.getSource());
                ZipEntry infoFile=jar.getEntry("mcmod.info");
                if (infoFile!=null) {
                    InputStream input=jar.getInputStream(infoFile);
                    ModMetadata data=sidedDelegate.readMetadataFrom(input, mod);
                    mod.setMetadata(data);
                } else {
                    getFMLLogger().fine(String.format("Failed to find mcmod.info file in %s for %s", mod.getSource().getName(), mod.getName()));
                }
            }
            catch (Exception e)
            {
                // Something wrong but we don't care
                getFMLLogger().fine(String.format("Failed to find mcmod.info file in %s for %s", mod.getSource().getName(), mod.getName()));
                getFMLLogger().throwing("FMLCommonHandler", "loadMetadataFor", e);
            }
            finally
            {
                if (jar!=null)
                {
                    try
                    {
                        jar.close();
                    }
                    catch (IOException e)
                    {
                        // GO AWAY
                    }
                }
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
                getFMLLogger().fine(String.format("Failed to find %s.info file in %s for %s", mod.getName(), mod.getSource().getName(), mod.getName()));
                getFMLLogger().throwing("FMLCommonHandler", "loadMetadataFor", e);
            }
        }
    }

    /**
     * @return
     */
    public IFMLSidedHandler getSidedDelegate()
    {
        return sidedDelegate;
    }

    /**
     * @param mod
     */
    public void injectSidedProxyDelegate(ModContainer mod)
    {
        ProxyInjector injector = mod.findSidedProxy();
        if (injector != null)
        {
            injector.inject(mod, sidedDelegate.getSide());
        }
    }

    public void handleWorldGeneration(int chunkX, int chunkZ, long worldSeed, Object... data)
    {
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        fmlRandom.setSeed((xSeed * chunkX + zSeed * chunkZ) ^ worldSeed);

        for (IWorldGenerator generator : worldGenerators)
        {
            generator.generate(fmlRandom, chunkX, chunkZ, data);
        }
    }

    public void registerTickHandler(ITickHandler handler)
    {
        registerScheduledTickHandler(new SingleIntervalHandler(handler));
    }

    public void registerScheduledTickHandler(IScheduledTickHandler handler)
    {
        tickHandlers.add(new TickQueueElement(handler));
    }

    public void registerWorldGenerator(IWorldGenerator generator)
    {
        worldGenerators.add(generator);
    }
}
