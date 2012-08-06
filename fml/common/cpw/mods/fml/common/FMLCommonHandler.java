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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import net.minecraft.server.MinecraftServer;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.discovery.ContainerType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


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
     * The delegate for side specific data and functions
     */
    private IFMLSidedHandler sidedDelegate;

    private int uniqueEntityListId = 220;

    private Map<String,Properties> modLanguageData=new HashMap<String,Properties>();

    private List<IScheduledTickHandler> scheduledTicks = new ArrayList<IScheduledTickHandler>();

    public void beginLoading(IFMLSidedHandler handler)
    {
        sidedDelegate = handler;
        FMLLog.info("Attempting early MinecraftForge initialization");
        callForgeMethod("initialize");
        FMLLog.info("Completed early MinecraftForge initialization");
    }

    public void rescheduleTicks()
    {
        sidedDelegate.profileStart("modTickScheduling");
        TickRegistry.updateTickQueue(scheduledTicks);
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
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            if (mc.matches(mod))
            {
                return mc;
            }
        }
        return null;
    }
    /**
     * Get the forge mod loader logging instance (goes to the forgemodloader log file)
     * @return
     */
    public Logger getFMLLogger()
    {
        return FMLLog.getLogger();
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
    private List<String> brandings;
    private List<ModContainer> auxilliaryContainers = new ArrayList<ModContainer>();

    private Class<?> findMinecraftForge()
    {
        if (forge==null && !noForge)
        {
            try {
                forge = Class.forName("net.minecraftforge.common.MinecraftForge");
            } catch (Exception ex) {
                noForge = true;
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
    public void computeBranding()
    {
        if (brandings == null)
        {
            Builder brd = ImmutableList.<String>builder();
            brd.add(Loader.instance().getMCVersionString());
            brd.add(Loader.instance().getFMLVersionString());
            brd.addAll(sidedDelegate.getAdditionalBrandingInformation());
            try {
                Properties props=new Properties();
                props.load(getClass().getClassLoader().getResourceAsStream("fmlbranding.properties"));
                brd.add(props.getProperty("fmlbranding"));
            } catch (Exception ex) {
                // Ignore - no branding file found
            }
            int tModCount = Loader.instance().getModList().size();
            int aModCount = Loader.instance().getActiveModList().size();
            brd.add(String.format("%d mod%s loaded, %d mod%s active", tModCount, tModCount!=1 ? "s" :"", aModCount, aModCount!=1 ? "s" :"" ));
            brandings = brd.build();
        }
    }
    public List<String> getBrandings()
    {
        if (brandings == null)
        {
            computeBranding();
        }
        return ImmutableList.copyOf(brandings);
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
        GameRegistry.generateWorld(chunkX, chunkZ, worldSeed, data);
    }

    public void onPostServerTick()
    {
        tickEnd(EnumSet.of(TickType.GAME));
    }

    /**
     * Every tick just after world and other ticks occur
     */
    public void onPostWorldTick(Object world)
    {
        tickEnd(EnumSet.of(TickType.WORLD), world);
    }

    public void onPreServerTick()
    {
        tickStart(EnumSet.of(TickType.GAME));
    }

    /**
     * Every tick just before world and other ticks occur
     */
    public void onPreWorldTick(Object world)
    {
        tickStart(EnumSet.of(TickType.WORLD), world);
    }

    public void onWorldLoadTick()
    {
        tickStart(EnumSet.of(TickType.WORLDLOAD));
    }

    public void handleServerStarting(MinecraftServer server)
    {
        Loader.instance().serverStarting(server);
    }

    public void handleServerStarted()
    {
        Loader.instance().serverStarted();
    }

    public void handleServerStopping()
    {
        Loader.instance().serverStopping();
    }

    public MinecraftServer getMinecraftServerInstance()
    {
        //TODO
        return null;
    }
}
