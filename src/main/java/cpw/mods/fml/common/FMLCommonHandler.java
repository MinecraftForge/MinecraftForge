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

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
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

    private Class<?> forge;
    private boolean noForge;
    private List<String> brandings;
    private List<String> brandingsNoMC;
    private List<ICrashCallable> crashCallables = Lists.newArrayList(Loader.instance().getCallableCrashInformation());
    private Set<SaveHandler> handlerSet = Sets.newSetFromMap(new MapMaker().weakKeys().<SaveHandler,Boolean>makeMap());
    private EventBus eventBus = new EventBus();
    /**
     * The FML event bus. Subscribe here for FML related events
     *
     * @return the event bus
     */
    public EventBus bus()
    {
        return eventBus;
    }

    public void beginLoading(IFMLSidedHandler handler)
    {
        sidedDelegate = handler;
        FMLLog.log("MinecraftForge", Level.INFO, "Attempting early MinecraftForge initialization");
        callForgeMethod("initialize");
        callForgeMethod("registerCrashCallable");
        FMLLog.log("MinecraftForge", Level.INFO, "Completed early MinecraftForge initialization");
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
        if (mod instanceof String)
        {
            return Loader.instance().getIndexedModList().get(mod);
        }
        else
        {
            return Loader.instance().getReversedModObjectList().get(mod);
        }
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
        if ((thr.getName().equals("Server thread")))
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
        FMLLog.log(Level.ERROR, exception, "Something raised an exception. The message was '%s'. 'stopGame' is %b", message, stopGame);
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
            Builder<String> brd = ImmutableList.<String>builder();
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
            brandingsNoMC = brandings.subList(1, brandings.size());
        }
    }
    public List<String> getBrandings(boolean includeMC)
    {
        if (brandings == null)
        {
            computeBranding();
        }
        return includeMC ? ImmutableList.copyOf(brandings) : ImmutableList.copyOf(brandingsNoMC);
    }

    public IFMLSidedHandler getSidedDelegate()
    {
        return sidedDelegate;
    }

    public void onPostServerTick()
    {
        bus().post(new TickEvent.ServerTickEvent(Phase.END));
    }

    /**
     * Every tick just after world and other ticks occur
     */
    public void onPostWorldTick(World world)
    {
        bus().post(new TickEvent.WorldTickEvent(Side.SERVER, Phase.END, world));
    }

    public void onPreServerTick()
    {
        bus().post(new TickEvent.ServerTickEvent(Phase.START));
    }

    /**
     * Every tick just before world and other ticks occur
     */
    public void onPreWorldTick(World world)
    {
        bus().post(new TickEvent.WorldTickEvent(Side.SERVER, Phase.START, world));
    }

    public boolean handleServerAboutToStart(MinecraftServer server)
    {
        return Loader.instance().serverAboutToStart(server);
    }

    public boolean handleServerStarting(MinecraftServer server)
    {
        sidedDelegate.serverLoadedSuccessfully();
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

    public void onServerStart(MinecraftServer dedicatedServer)
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
        bus().post(new TickEvent.ClientTickEvent(Phase.START));
    }

    public void onPostClientTick()
    {
        bus().post(new TickEvent.ClientTickEvent(Phase.END));
    }

    public void onRenderTickStart(float timer)
    {
        bus().post(new TickEvent.RenderTickEvent(Phase.START, timer));
    }

    public void onRenderTickEnd(float timer)
    {
        bus().post(new TickEvent.RenderTickEvent(Phase.END, timer));
    }

    public void onPlayerPreTick(EntityPlayer player)
    {
        bus().post(new TickEvent.PlayerTickEvent(Phase.START, player));
    }

    public void onPlayerPostTick(EntityPlayer player)
    {
        bus().post(new TickEvent.PlayerTickEvent(Phase.END, player));
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
                    tagCompound.func_74782_a(mc.getModId(), dataForWriting);
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
                    try
                    {
                        wac.readData(handler, worldInfo, additionalProperties, tagCompound.func_74775_l(mc.getModId()));
                    }
                    catch (RuntimeException ex)
                    {
                        sidedDelegate.failedServerLoading(ex, wac);
                        throw ex;
                    }
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

    public void bootstrap()
    {
    }

    public NetworkManager getClientToServerNetworkManager()
    {
        return sidedDelegate.getClientToServerNetworkManager();
    }

    public void fireMouseInput()
    {
        bus().post(new InputEvent.MouseInputEvent());
    }

    public void fireKeyInput()
    {
        bus().post(new InputEvent.KeyInputEvent());
    }

    public void firePlayerChangedDimensionEvent(EntityPlayer player, int fromDim, int toDim)
    {
        bus().post(new PlayerEvent.PlayerChangedDimensionEvent(player, fromDim, toDim));
    }

    public void firePlayerLoggedIn(EntityPlayer player)
    {
        bus().post(new PlayerEvent.PlayerLoggedInEvent(player));
    }

    public void firePlayerLoggedOut(EntityPlayer player)
    {
        bus().post(new PlayerEvent.PlayerLoggedOutEvent(player));
    }

    public void firePlayerRespawnEvent(EntityPlayer player)
    {
        bus().post(new PlayerEvent.PlayerRespawnEvent(player));
    }

    public void firePlayerItemPickupEvent(EntityPlayer player, EntityItem item)
    {
        bus().post(new PlayerEvent.ItemPickupEvent(player, item));
    }

    public void firePlayerCraftingEvent(EntityPlayer player, ItemStack crafted, IInventory craftMatrix)
    {
        bus().post(new PlayerEvent.ItemCraftedEvent(player, crafted, craftMatrix));
    }

    public void firePlayerSmeltedEvent(EntityPlayer player, ItemStack smelted)
    {
        bus().post(new PlayerEvent.ItemSmeltedEvent(player, smelted));
    }

    public INetHandler getClientPlayHandler()
    {
        return sidedDelegate.getClientPlayHandler();
    }

    public void waitForPlayClient()
    {
        sidedDelegate.waitForPlayClient();
    }

    public void fireNetRegistrationEvent(NetworkManager manager, Set<String> channelSet, String channel, Side side)
    {
        sidedDelegate.fireNetRegistrationEvent(bus(), manager, channelSet, channel, side);
    }

    public FMLMissingMappingsEvent.Action getDefaultMissingAction()
    {
        return sidedDelegate.getDefaultMissingAction();
    }
}
