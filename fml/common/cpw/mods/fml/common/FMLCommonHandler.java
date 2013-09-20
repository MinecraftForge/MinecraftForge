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

package cpw.mods.fml.common;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerListenThread;
import net.minecraft.server.ThreadMinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.ItemData;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.server.FMLServerHandler;


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

    private List<IScheduledTickHandler> scheduledClientTicks = Lists.newArrayList();
    private List<IScheduledTickHandler> scheduledServerTicks = Lists.newArrayList();
    private Class<?> forge;
    private boolean noForge;
    private List<String> brandings;
    private List<ICrashCallable> crashCallables = Lists.newArrayList(Loader.instance().getCallableCrashInformation());
    private Set<SaveHandler> handlerSet = Sets.newSetFromMap(new MapMaker().weakKeys().<SaveHandler,Boolean>makeMap());



    public void beginLoading(IFMLSidedHandler handler)
    {
        sidedDelegate = handler;
        FMLLog.log("MinecraftForge", Level.INFO, "Attempting early MinecraftForge initialization");
        callForgeMethod("initialize");
        callForgeMethod("registerCrashCallable");
        FMLLog.log("MinecraftForge", Level.INFO, "Completed early MinecraftForge initialization");
    }

    public void rescheduleTicks(Side side)
    {
        TickRegistry.updateTickQueue(side.isClient() ? scheduledClientTicks : scheduledServerTicks, side);
    }
    public void tickStart(EnumSet<TickType> ticks, Side side, Object ... data)
    {
        List<IScheduledTickHandler> scheduledTicks = side.isClient() ? scheduledClientTicks : scheduledServerTicks;

        if (scheduledTicks.size()==0)
        {
            return;
        }
        for (IScheduledTickHandler ticker : scheduledTicks)
        {
            EnumSet<TickType> ticksToRun = EnumSet.copyOf(Objects.firstNonNull(ticker.ticks(), EnumSet.noneOf(TickType.class)));
            ticksToRun.retainAll(ticks);
            if (!ticksToRun.isEmpty())
            {
                ticker.tickStart(ticksToRun, data);
            }
        }
    }

    public void tickEnd(EnumSet<TickType> ticks, Side side, Object ... data)
    {
        List<IScheduledTickHandler> scheduledTicks = side.isClient() ? scheduledClientTicks : scheduledServerTicks;

        if (scheduledTicks.size()==0)
        {
            return;
        }
        for (IScheduledTickHandler ticker : scheduledTicks)
        {
            EnumSet<TickType> ticksToRun = EnumSet.copyOf(Objects.firstNonNull(ticker.ticks(), EnumSet.noneOf(TickType.class)));
            ticksToRun.retainAll(ticks);
            if (!ticksToRun.isEmpty())
            {
                ticker.tickEnd(ticksToRun, data);
            }
        }
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
     */
    public ModContainer findContainerFor(Object mod)
    {
        return Loader.instance().getReversedModObjectList().get(mod);
    }
    /**
     * Get the forge mod loader logging instance (goes to the forgemodloader log file)
     * @return The log instance for the FML log file
     */
    public Logger getFMLLogger()
    {
        return FMLLog.getLogger();
    }

    public Side getSide()
    {
        return sidedDelegate.getSide();
    }

    /**
     * Return the effective side for the context in the game. This is dependent
     * on thread analysis to try and determine whether the code is running in the
     * server or not. Use at your own risk
     */
    public Side getEffectiveSide()
    {
        Thread thr = Thread.currentThread();
        if ((thr instanceof ThreadMinecraftServer) || (thr instanceof ServerListenThread))
        {
            return Side.SERVER;
        }

        return Side.CLIENT;
    }
    /**
     * Raise an exception
     */
    public void raiseException(Throwable exception, String message, boolean stopGame)
    {
        FMLLog.log(Level.SEVERE, exception, "Something raised an exception. The message was '%s'. 'stopGame' is %b", message, stopGame);
        if (stopGame)
        {
            getSidedDelegate().haltGame(message,exception);
        }
    }


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

    public void computeBranding()
    {
        if (brandings == null)
        {
            Builder brd = ImmutableList.<String>builder();
            brd.add(Loader.instance().getMCVersionString());
            brd.add(Loader.instance().getMCPVersionString());
            brd.add("FML v"+Loader.instance().getFMLVersionString());
            String forgeBranding = (String) callForgeMethod("getBrandingVersion");
            if (!Strings.isNullOrEmpty(forgeBranding))
            {
                brd.add(forgeBranding);
            }
            if (sidedDelegate!=null)
            {
            	brd.addAll(sidedDelegate.getAdditionalBrandingInformation());
            }
            if (Loader.instance().getFMLBrandingProperties().containsKey("fmlbranding"))
            {
                brd.add(Loader.instance().getFMLBrandingProperties().get("fmlbranding"));
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

    public IFMLSidedHandler getSidedDelegate()
    {
        return sidedDelegate;
    }

    public void onPostServerTick()
    {
        tickEnd(EnumSet.of(TickType.SERVER), Side.SERVER);
    }

    /**
     * Every tick just after world and other ticks occur
     */
    public void onPostWorldTick(Object world)
    {
        tickEnd(EnumSet.of(TickType.WORLD), Side.SERVER, world);
    }

    public void onPreServerTick()
    {
        tickStart(EnumSet.of(TickType.SERVER), Side.SERVER);
    }

    /**
     * Every tick just before world and other ticks occur
     */
    public void onPreWorldTick(Object world)
    {
        tickStart(EnumSet.of(TickType.WORLD), Side.SERVER, world);
    }

    public void onWorldLoadTick(World[] worlds)
    {
        rescheduleTicks(Side.SERVER);
        for (World w : worlds)
        {
            tickStart(EnumSet.of(TickType.WORLDLOAD), Side.SERVER, w);
        }
    }

    public boolean handleServerAboutToStart(MinecraftServer server)
    {
        return Loader.instance().serverAboutToStart(server);
    }

    public boolean handleServerStarting(MinecraftServer server)
    {
        return Loader.instance().serverStarting(server);
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
        return sidedDelegate.getServer();
    }

    public void showGuiScreen(Object clientGuiElement)
    {
        sidedDelegate.showGuiScreen(clientGuiElement);
    }

    public Entity spawnEntityIntoClientWorld(EntityRegistration registration, EntitySpawnPacket entitySpawnPacket)
    {
        return sidedDelegate.spawnEntityIntoClientWorld(registration, entitySpawnPacket);
    }

    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket)
    {
        sidedDelegate.adjustEntityLocationOnClient(entitySpawnAdjustmentPacket);
    }

    public void onServerStart(DedicatedServer dedicatedServer)
    {
        FMLServerHandler.instance();
        sidedDelegate.beginServerLoading(dedicatedServer);
    }

    public void onServerStarted()
    {
        sidedDelegate.finishServerLoading();
    }


    public void onPreClientTick()
    {
        tickStart(EnumSet.of(TickType.CLIENT), Side.CLIENT);

    }

    public void onPostClientTick()
    {
        tickEnd(EnumSet.of(TickType.CLIENT), Side.CLIENT);
    }

    public void onRenderTickStart(float timer)
    {
        tickStart(EnumSet.of(TickType.RENDER), Side.CLIENT, timer);
    }

    public void onRenderTickEnd(float timer)
    {
        tickEnd(EnumSet.of(TickType.RENDER), Side.CLIENT, timer);
    }

    public void onPlayerPreTick(EntityPlayer player)
    {
        Side side = player instanceof EntityPlayerMP ? Side.SERVER : Side.CLIENT;
        tickStart(EnumSet.of(TickType.PLAYER), side, player);
    }

    public void onPlayerPostTick(EntityPlayer player)
    {
        Side side = player instanceof EntityPlayerMP ? Side.SERVER : Side.CLIENT;
        tickEnd(EnumSet.of(TickType.PLAYER), side, player);
    }

    public void registerCrashCallable(ICrashCallable callable)
    {
        crashCallables.add(callable);
    }

    public void enhanceCrashReport(CrashReport crashReport, CrashReportCategory category)
    {
        for (ICrashCallable call: crashCallables)
        {
            category.func_71500_a(call.getLabel(), call);
        }
    }

    public void handleTinyPacket(NetHandler handler, Packet131MapData mapData)
    {
        sidedDelegate.handleTinyPacket(handler, mapData);
    }

    public void handleWorldDataSave(SaveHandler handler, WorldInfo worldInfo, NBTTagCompound tagCompound)
    {
        for (ModContainer mc : Loader.instance().getModList())
        {
            if (mc instanceof InjectedModContainer)
            {
                WorldAccessContainer wac = ((InjectedModContainer)mc).getWrappedWorldAccessContainer();
                if (wac != null)
                {
                    NBTTagCompound dataForWriting = wac.getDataForWriting(handler, worldInfo);
                    tagCompound.func_74766_a(mc.getModId(), dataForWriting);
                }
            }
        }
    }

    public void handleWorldDataLoad(SaveHandler handler, WorldInfo worldInfo, NBTTagCompound tagCompound)
    {
        if (getEffectiveSide()!=Side.SERVER)
        {
            return;
        }
        if (handlerSet.contains(handler))
        {
            return;
        }
        handlerSet.add(handler);
        Map<String,NBTBase> additionalProperties = Maps.newHashMap();
        worldInfo.setAdditionalProperties(additionalProperties);
        for (ModContainer mc : Loader.instance().getModList())
        {
            if (mc instanceof InjectedModContainer)
            {
                WorldAccessContainer wac = ((InjectedModContainer)mc).getWrappedWorldAccessContainer();
                if (wac != null)
                {
                    wac.readData(handler, worldInfo, additionalProperties, tagCompound.func_74775_l(mc.getModId()));
                }
            }
        }
    }

    public boolean shouldServerBeKilledQuietly()
    {
        if (sidedDelegate == null)
        {
            return false;
        }
        return sidedDelegate.shouldServerShouldBeKilledQuietly();
    }

    public void disconnectIDMismatch(MapDifference<Integer, ItemData> serverDifference, NetHandler toKill, INetworkManager network)
    {
        sidedDelegate.disconnectIDMismatch(serverDifference, toKill, network);
    }

    public void handleServerStopped()
    {
        sidedDelegate.serverStopped();
        MinecraftServer server = getMinecraftServerInstance();
        Loader.instance().serverStopped();
        // FORCE the internal server to stop: hello optifine workaround!
        if (server!=null) ObfuscationReflectionHelper.setPrivateValue(MinecraftServer.class, server, false, "field_71316"+"_v", "u", "serverStopped");
    }

    public String getModName()
    {
        List<String> modNames = Lists.newArrayListWithExpectedSize(3);
        modNames.add("fml");
        if (!noForge)
        {
            modNames.add("forge");
        }

        if (Loader.instance().getFMLBrandingProperties().containsKey("snooperbranding"))
        {
            modNames.add(Loader.instance().getFMLBrandingProperties().get("snooperbranding"));
        }
        return Joiner.on(',').join(modNames);
    }

    public void addModToResourcePack(ModContainer container)
    {
        sidedDelegate.addModAsResource(container);
    }

    public void updateResourcePackList()
    {
        sidedDelegate.updateResourcePackList();
    }

    public String getCurrentLanguage()
    {

        return sidedDelegate.getCurrentLanguage();
    }
}
