/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.thread.SidedThreadGroup;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;

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

    private boolean noForge;
    private List<String> brandings;
    private List<String> brandingsNoMC;
    private List<ICrashCallable> crashCallables = Lists.newArrayList(Loader.instance().getCallableCrashInformation());
    private Set<SaveHandler> handlerSet = Collections.newSetFromMap(new MapMaker().weakKeys().<SaveHandler,Boolean>makeMap());
    private WeakReference<SaveHandler> handlerToCheck;
    private EventBus eventBus = MinecraftForge.EVENT_BUS;
    private volatile CountDownLatch exitLatch = null;

    private FMLCommonHandler()
    {
        registerCrashCallable(new ICrashCallable()
        {
            @Override
            public String call() throws Exception
            {
                StringBuilder builder = new StringBuilder();
                Joiner joiner = Joiner.on("\n  ");
                for(String coreMod : CoreModManager.getTransformers().keySet())
                {
                    builder.append("\n" + coreMod + "\n  ").append(joiner.join(CoreModManager.getTransformers().get(coreMod)));
                }
                return builder.toString();
            }

            @Override
            public String getLabel()
            {
                return "Loaded coremods (and transformers)";
            }
        });
    }
    /**
     * The FML event bus. Subscribe here for FML related events
     *
     * @Deprecated Use {@link MinecraftForge#EVENT_BUS} they're the same thing now
     * @return the event bus
     */
    @Deprecated
    public EventBus bus()
    {
        return eventBus;
    }

    public List<String> beginLoading(IFMLSidedHandler handler)
    {
        sidedDelegate = handler;
        MinecraftForge.initialize();
//        MinecraftForge.registerCrashCallable();
        return ImmutableList.<String>of();
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
     *
     * @deprecated Not used in FML, Mods use your own logger, see {@link FMLPreInitializationEvent#getModLog()}
     */
    @Deprecated
    public Logger getFMLLogger()
    {
        return FMLLog.log;
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
        final ThreadGroup group = Thread.currentThread().getThreadGroup();
        return group instanceof SidedThreadGroup ? ((SidedThreadGroup) group).getSide() : Side.CLIENT;
    }
    /**
     * Raise an exception
     */
    public void raiseException(Throwable exception, String message, boolean stopGame)
    {
        FMLLog.log.error("Something raised an exception. The message was '{}'. 'stopGame' is {}", stopGame, exception);
        if (stopGame)
        {
            getSidedDelegate().haltGame(message,exception);
        }
    }


    public void computeBranding()
    {
        if (brandings == null)
        {
            Builder<String> brd = ImmutableList.builder();
            brd.add(Loader.instance().getMCVersionString());
            brd.add(Loader.instance().getMCPVersionString());
            brd.add("Powered by Forge " + ForgeVersion.getVersion());
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
        return Loader.instance().serverStarting(server);
    }

    public void handleServerStarted()
    {
        Loader.instance().serverStarted();
        sidedDelegate.allowLogins();
    }

    public void handleServerStopping()
    {
        Loader.instance().serverStopping();
    }

    public File getSavesDirectory() {
        return sidedDelegate.getSavesDirectory();
    }

    public MinecraftServer getMinecraftServerInstance()
    {
        return sidedDelegate.getServer();
    }

    public void showGuiScreen(Object clientGuiElement)
    {
        sidedDelegate.showGuiScreen(clientGuiElement);
    }

    public void queryUser(StartupQuery query) throws InterruptedException
    {
        sidedDelegate.queryUser(query);
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
        Animation.setClientPartialTickTime(timer);
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
            category.addDetail(call.getLabel(), call);
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
                    tagCompound.setTag(mc.getModId(), dataForWriting);
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
        handlerToCheck = new WeakReference<SaveHandler>(handler); // for confirmBackupLevelDatUse
        Map<String,NBTBase> additionalProperties = Maps.newHashMap();
        worldInfo.setAdditionalProperties(additionalProperties);
        for (ModContainer mc : Loader.instance().getModList())
        {
            if (mc instanceof InjectedModContainer)
            {
                WorldAccessContainer wac = ((InjectedModContainer)mc).getWrappedWorldAccessContainer();
                if (wac != null)
                {
                    wac.readData(handler, worldInfo, additionalProperties, tagCompound.getCompoundTag(mc.getModId()));
                }
            }
        }
    }

    public void confirmBackupLevelDatUse(SaveHandler handler)
    {
        if (handlerToCheck == null || handlerToCheck.get() != handler) {
            // only run if the save has been initially loaded
            handlerToCheck = null;
            return;
        }

        String text = "Forge Mod Loader detected that the backup level.dat is being used.\n\n" +
                "This may happen due to a bug or corruption, continuing can damage\n" +
                "your world beyond repair or lose data / progress.\n\n" +
                "It's recommended to create a world backup before continuing.";

        boolean confirmed = StartupQuery.confirm(text);
        if (!confirmed) StartupQuery.abort();
    }

    public boolean isDisplayCloseRequested()
    {
        return sidedDelegate != null && sidedDelegate.isDisplayCloseRequested();
    }

    public boolean shouldServerBeKilledQuietly()
    {
        if (sidedDelegate == null)
        {
            return false;
        }
        return sidedDelegate.shouldServerShouldBeKilledQuietly();
    }

    /**
     * Make handleExit() wait for handleServerStopped().
     *
     * For internal use only!
     */
    public void expectServerStopped()
    {
        exitLatch = new CountDownLatch(1);
    }

    /**
     * Delayed System.exit() until the server is actually stopped/done saving.
     *
     * For internal use only!
     *
     * @param retVal Exit code for System.exit()
     */
    public void handleExit(int retVal)
    {
        CountDownLatch latch = exitLatch;

        if (latch != null)
        {
            try
            {
                FMLLog.log.info("Waiting for the server to terminate/save.");
                if (!latch.await(10, TimeUnit.SECONDS))
                {
                    FMLLog.log.warn("The server didn't stop within 10 seconds, exiting anyway.");
                }
                else
                {
                    FMLLog.log.info("Server terminated.");
                }
            }
            catch (InterruptedException e)
            {
                FMLLog.log.warn("Interrupted wait, exiting.");
            }
        }

        System.exit(retVal);
    }

    public void handleServerStopped()
    {
        sidedDelegate.serverStopped();
        MinecraftServer server = getMinecraftServerInstance();
        Loader.instance().serverStopped();
        // FORCE the internal server to stop: hello optifine workaround!
        if (server!=null) ObfuscationReflectionHelper.setPrivateValue(MinecraftServer.class, server, false, "field_71316"+"_v", "u", "serverStopped");

        // allow any pending exit to continue, clear exitLatch
        CountDownLatch latch = exitLatch;

        if (latch != null)
        {
            latch.countDown();
            exitLatch = null;
        }
    }

    public String getModName()
    {
        List<String> modNames = Lists.newArrayListWithExpectedSize(3);
        modNames.add("fml");
        if (!noForge)
        {
            modNames.add(ForgeVersion.MOD_ID);
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

    public void firePlayerRespawnEvent(EntityPlayer player, boolean endConquered)
    {
        bus().post(new PlayerEvent.PlayerRespawnEvent(player, endConquered));
    }

    public void firePlayerItemPickupEvent(EntityPlayer player, EntityItem item, ItemStack clone)
    {
        bus().post(new PlayerEvent.ItemPickupEvent(player, item, clone));
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

    public void fireNetRegistrationEvent(NetworkManager manager, Set<String> channelSet, String channel, Side side)
    {
        sidedDelegate.fireNetRegistrationEvent(bus(), manager, channelSet, channel, side);
    }

    public boolean shouldAllowPlayerLogins()
    {
        return sidedDelegate.shouldAllowPlayerLogins();
    }

    public void fireServerConnectionEvent(NetworkManager manager)
    {
        bus().post(new FMLNetworkEvent.ServerConnectionFromClientEvent(manager));
    }

    /**
     * Process initial Handshake packet, kicks players from the server if they are connecting while we are starting up.
     * Also verifies the client has the FML marker.
     *
     * @param packet Handshake Packet
     * @param manager Network connection
     * @return True to allow connection, otherwise False.
     */
    public boolean handleServerHandshake(C00Handshake packet, NetworkManager manager)
    {
        if (!shouldAllowPlayerLogins())
        {
            TextComponentString text = new TextComponentString("Server is still starting! Please wait before reconnecting.");
            FMLLog.log.info("Disconnecting Player: {}", text.getUnformattedText());
            manager.sendPacket(new SPacketDisconnect(text));
            manager.closeChannel(text);
            return false;
        }

        if (packet.getRequestedState() == EnumConnectionState.LOGIN && (!NetworkRegistry.INSTANCE.isVanillaAccepted(Side.CLIENT) && !packet.hasFMLMarker()))
        {
            manager.setConnectionState(EnumConnectionState.LOGIN);
            TextComponentString text = new TextComponentString("This server has mods that require FML/Forge to be installed on the client. Contact your server admin for more details.");
            Collection<String> modNames = NetworkRegistry.INSTANCE.getRequiredMods(Side.CLIENT);
            FMLLog.log.info("Disconnecting Player: This server has mods that require FML/Forge to be installed on the client: {}", modNames);
            manager.sendPacket(new SPacketDisconnect(text));
            manager.closeChannel(text);
            return false;
        }

        manager.channel().attr(NetworkRegistry.FML_MARKER).set(packet.hasFMLMarker());
        return true;
    }

    public void processWindowMessages()
    {
        if (sidedDelegate == null) return;
        sidedDelegate.processWindowMessages();
    }

    /**
     * Used to exit from java, with system exit preventions in place. Will be tidy about it and just log a message,
     * unless debugging is enabled
     *
     * @param exitCode The exit code
     * @param hardExit Perform a halt instead of an exit (only use when the world is unsavable) - read the warnings at {@link Runtime#halt(int)}
     */
    public void exitJava(int exitCode, boolean hardExit)
    {
        FMLLog.log.warn("Java has been asked to exit (code {})", exitCode);
        if (hardExit)
        {
            FMLLog.log.warn("This is an abortive exit and could cause world corruption or other things");
        }
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        FMLLog.log.warn("Exit trace:");
        //The first 2 elements are Thread#getStackTrace and FMLCommonHandler#exitJava and aren't relevant
        for (int i = 2; i < stack.length; i++)
        {
            FMLLog.log.warn("\t{}", stack[i]);
        }
        if (hardExit)
        {
            Runtime.getRuntime().halt(exitCode);
        }
        else
        {
            Runtime.getRuntime().exit(exitCode);
        }
    }

    public IThreadListener getWorldThread(INetHandler net)
    {
        return sidedDelegate.getWorldThread(net);
    }

    public static void callFuture(FutureTask<?> task)
    {
        try
        {
            task.run();
            task.get(); // Forces the exception to be thrown if any
        }
        catch (InterruptedException | ExecutionException e)
        {
            FMLLog.log.fatal("Exception caught executing FutureTask: {}", e.toString(), e);
        }
    }

    /**
     * Loads a lang file, first searching for a marker to enable the 'extended' format {escape characters}
     * If the marker is not found it simply returns and let the vanilla code load things.
     * The Marker is 'PARSE_ESCAPES' by itself on a line starting with '#' as such:
     * #PARSE_ESCAPES
     *
     * @param table The Map to load each key/value pair into.
     * @param inputstream Input stream containing the lang file.
     * @return A new InputStream that vanilla uses to load normal Lang files, Null if this is a 'enhanced' file and loading is done.
     */
    @Nullable
    public InputStream loadLanguage(Map<String, String> table, InputStream inputstream) throws IOException
    {
        byte[] data = IOUtils.toByteArray(inputstream);

        boolean isEnhanced = false;
        for (String line : IOUtils.readLines(new ByteArrayInputStream(data), StandardCharsets.UTF_8))
        {
            if (!line.isEmpty() && line.charAt(0) == '#')
            {
                line = line.substring(1).trim();
                if (line.equals("PARSE_ESCAPES"))
                {
                    isEnhanced = true;
                    break;
                }
            }
        }

        if (!isEnhanced)
            return new ByteArrayInputStream(data);

        Properties props = new Properties();
        props.load(new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8));
        for (Entry<Object, Object> e : props.entrySet())
        {
            table.put((String)e.getKey(), (String)e.getValue());
        }
        props.clear();
        return null;
    }
    public String stripSpecialChars(String message)
    {
        return sidedDelegate != null ? sidedDelegate.stripSpecialChars(message) : message;
    }

    public void reloadRenderers() {
        sidedDelegate.reloadRenderers();
    }

    public void fireSidedRegistryEvents()
    {
        sidedDelegate.fireSidedRegistryEvents();
    }

    public CompoundDataFixer getDataFixer()
    {
        return (CompoundDataFixer)sidedDelegate.getDataFixer();
    }

    public boolean isDisplayVSyncForced() { return sidedDelegate.isDisplayVSyncForced(); }
    public void resetClientRecipeBook() {
        this.sidedDelegate.resetClientRecipeBook();
    }

    public void reloadSearchTrees() {
        this.sidedDelegate.reloadSearchTrees();
    }

    public void reloadCreativeSettings()
    {
        this.sidedDelegate.reloadCreativeSettings();
    }
}
