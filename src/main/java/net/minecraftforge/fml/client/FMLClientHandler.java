/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
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
package net.minecraftforge.fml.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.DuplicateModsFoundException;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLContainerHolder;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.IFMLSidedHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.WrongMinecraftVersionException;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;
import net.minecraftforge.fml.common.toposort.ModSortingException;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * Handles primary communication from hooked code into the system
 *
 * The FML entry point is {@link #beginMinecraftLoading(Minecraft, List)} called from
 * {@link Minecraft}
 *
 * Obfuscated code should focus on this class and other members of the "server"
 * (or "client") code
 *
 * The actual mod loading is handled at arms length by {@link Loader}
 *
 * It is expected that a similar class will exist for each target environment:
 * Bukkit and Client side.
 *
 * It should not be directly modified.
 *
 * @author cpw
 *
 */
public class FMLClientHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLClientHandler INSTANCE = new FMLClientHandler();

    /**
     * A reference to the server itself
     */
    private Minecraft client;

    private DummyModContainer optifineContainer;

    private MissingModsException modsMissing;

    private ModSortingException modSorting;

    private boolean loading = true;

    private WrongMinecraftVersionException wrongMC;

    private CustomModLoadingErrorDisplayException customError;

    private DuplicateModsFoundException dupesFound;

    private boolean serverShouldBeKilledQuietly;

    private List<IResourcePack> resourcePackList;

    private Map<String, IResourcePack> resourcePackMap;

    private BiMap<ModContainer, IModGuiFactory> guiFactories;

    private Map<ServerStatusResponse,JsonObject> extraServerListData;
    private Map<ServerData, ExtendedServerListData> serverDataTag;

    private WeakReference<NetHandlerPlayClient> currentPlayClient;

    /**
     * Called to start the whole game off
     *
     * @param minecraft The minecraft instance being launched
     * @param resourcePackList The resource pack list we will populate with mods
     * @param resourceManager The resource manager
     */
    @SuppressWarnings("unchecked")
    public void beginMinecraftLoading(Minecraft minecraft, @SuppressWarnings("rawtypes") List resourcePackList, IReloadableResourceManager resourceManager)
    {
        detectOptifine();
        SplashProgress.start();
        client = minecraft;
        this.resourcePackList = resourcePackList;
        this.resourcePackMap = Maps.newHashMap();
        if (minecraft.isDemo())
        {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.");
            haltGame("FML will not run in demo mode", new RuntimeException());
            return;
        }

        resourceManager.registerReloadListener(Animation.INSTANCE);

        FMLCommonHandler.instance().beginLoading(this);
        try
        {
            Loader.instance().loadMods();
        }
        catch (WrongMinecraftVersionException wrong)
        {
            wrongMC = wrong;
        }
        catch (DuplicateModsFoundException dupes)
        {
            dupesFound = dupes;
        }
        catch (MissingModsException missing)
        {
            modsMissing = missing;
        }
        catch (ModSortingException sorting)
        {
            modSorting = sorting;
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        finally
        {
            client.refreshResources();
        }

        try
        {
            Loader.instance().preinitializeMods();
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        Map<String,Map<String,String>> sharedModList = (Map<String, Map<String, String>>) Launch.blackboard.get("modList");
        if (sharedModList == null)
        {
            sharedModList = Maps.newHashMap();
            Launch.blackboard.put("modList", sharedModList);
        }
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            Map<String,String> sharedModDescriptor = mc.getSharedModDescriptor();
            if (sharedModDescriptor != null)
            {
                String sharedModId = "fml:"+mc.getModId();
                sharedModList.put(sharedModId, sharedModDescriptor);
            }
        }
    }

    private void detectOptifine()
    {
        try
        {
            Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            String optifineVersion = (String) optifineConfig.getField("VERSION").get(null);
            Map<String,Object> dummyOptifineMeta = ImmutableMap.<String,Object>builder().put("name", "Optifine").put("version", optifineVersion).build();
            ModMetadata optifineMetadata = MetadataCollection.from(getClass().getResourceAsStream("optifinemod.info"),"optifine").getMetadataForId("optifine", dummyOptifineMeta);
            optifineContainer = new DummyModContainer(optifineMetadata);
            FMLLog.info("Forge Mod Loader has detected optifine %s, enabling compatibility features",optifineContainer.getVersion());
        }
        catch (Exception e)
        {
            optifineContainer = null;
        }
    }

    @Override
    public void haltGame(String message, Throwable t)
    {
        SplashProgress.finish();
        client.displayCrashReport(new CrashReport(message, t));
        throw Throwables.propagate(t);
    }
    /**
     * Called a bit later on during initialization to finish loading mods
     * Also initializes key bindings
     *
     */
    public void finishMinecraftLoading()
    {
        if (modsMissing != null || wrongMC != null || customError!=null || dupesFound!=null || modSorting!=null)
        {
            SplashProgress.finish();
            return;
        }
        try
        {
            Loader.instance().initializeMods();
        }
        catch (CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.ERROR, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
            SplashProgress.finish();
            return;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }

        // Reload resources
        client.refreshResources();
        RenderingRegistry.loadEntityRenderers(Minecraft.getMinecraft().getRenderManager().entityRenderMap);
        guiFactories = HashBiMap.create();
        for (ModContainer mc : Loader.instance().getActiveModList())
        {
            String className = mc.getGuiClassName();
            if (Strings.isNullOrEmpty(className))
            {
                continue;
            }
            try
            {
                Class<?> clazz = Class.forName(className, true, Loader.instance().getModClassLoader());
                Class<? extends IModGuiFactory> guiClassFactory = clazz.asSubclass(IModGuiFactory.class);
                IModGuiFactory guiFactory = guiClassFactory.newInstance();
                guiFactory.initialize(client);
                guiFactories.put(mc, guiFactory);
            } catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "A critical error occurred instantiating the gui factory for mod %s", mc.getModId());
            }
        }
        loading = false;
        client.gameSettings.loadOptions(); //Reload options to load any mod added keybindings.
    }

    public void extendModList()
    {
        @SuppressWarnings("unchecked")
        Map<String,Map<String,String>> modList = (Map<String, Map<String, String>>) Launch.blackboard.get("modList");
        if (modList != null)
        {
            for (Entry<String, Map<String, String>> modEntry : modList.entrySet())
            {
                String sharedModId = modEntry.getKey();
                String system = sharedModId.split(":")[0];
                if ("fml".equals(system))
                {
                    continue;
                }
                /*
                Map<String, String> mod = modEntry.getValue();
                String modSystem = mod.get("modsystem"); // the modsystem (FML uses FML or ModLoader)
                String modId = mod.get("id"); // unique ID
                String modVersion = mod.get("version"); // version
                String modName = mod.get("name"); // a human readable name
                String modURL = mod.get("url"); // a URL for the mod (can be empty string)
                String modAuthors = mod.get("authors"); // a csv of authors (can be empty string)
                String modDescription = mod.get("description"); // a (potentially) multiline description (can be empty string)
                */
            }
        }

    }
    public void onInitializationComplete()
    {
        // re-sync TEXTURE_2D, splash screen disables it with a direct GL call
        GlStateManager.disableTexture2D();
        GlStateManager.enableTexture2D();
        if (wrongMC != null)
        {
            showGuiScreen(new GuiWrongMinecraft(wrongMC));
        }
        else if (modsMissing != null)
        {
            showGuiScreen(new GuiModsMissing(modsMissing));
        }
        else if (dupesFound != null)
        {
            showGuiScreen(new GuiDupesFound(dupesFound));
        }
        else if (modSorting != null)
        {
            showGuiScreen(new GuiSortingProblem(modSorting));
        }
        else if (customError != null)
        {
            showGuiScreen(new GuiCustomModLoadingErrorScreen(customError));
        }
        else
        {
            Loader.instance().loadingComplete();
            SplashProgress.finish();
        }
        logMissingTextureErrors();
    }
    /**
     * Get the server instance
     */
    public Minecraft getClient()
    {
        return client;
    }

    /**
     * @return the instance
     */
    public static FMLClientHandler instance()
    {
        return INSTANCE;
    }

    /**
     * @param player
     * @param gui
     */
    public void displayGuiScreen(EntityPlayer player, GuiScreen gui)
    {
        if (client.thePlayer==player && gui != null) {
            client.displayGuiScreen(gui);
        }
    }

    /**
     * @param mods
     */
    public void addSpecialModEntries(ArrayList<ModContainer> mods)
    {
        if (optifineContainer!=null) {
            mods.add(optifineContainer);
        }
    }

    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        if (optifineContainer!=null)
        {
            return Arrays.asList(String.format("Optifine %s",optifineContainer.getVersion()));
        } else {
            return ImmutableList.<String>of();
        }
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }

    public boolean hasOptifine()
    {
        return optifineContainer!=null;
    }

    @Override
    public void showGuiScreen(Object clientGuiElement)
    {
        GuiScreen gui = (GuiScreen) clientGuiElement;
        client.displayGuiScreen(gui);
    }

    @Override
    public void queryUser(StartupQuery query) throws InterruptedException
    {
        if (query.getResult() == null)
        {
            client.displayGuiScreen(new GuiNotification(query));
        }
        else
        {
            client.displayGuiScreen(new GuiConfirmation(query));
        }

        if (query.isSynchronous())
        {
            while (client.currentScreen instanceof GuiNotification)
            {
                if (Thread.interrupted()) throw new InterruptedException();

                client.loadingScreen.displayLoadingString("");

                Thread.sleep(50);
            }

            client.loadingScreen.displayLoadingString(""); // make sure the blank screen is being drawn at the end
        }
    }

    public boolean handleLoadingScreen(ScaledResolution scaledResolution) throws IOException
    {
        if (client.currentScreen instanceof GuiNotification)
        {
            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();
            int mouseX = Mouse.getX() * width / client.displayWidth;
            int mouseZ = height - Mouse.getY() * height / client.displayHeight - 1;

            client.currentScreen.drawScreen(mouseX, mouseZ, 0);
            client.currentScreen.handleInput();

            return true;
        }
        else
        {
            return false;
        }
    }

    public WorldClient getWorldClient()
    {
        return client.theWorld;
    }

    public EntityPlayerSP getClientPlayerEntity()
    {
        return client.thePlayer;
    }

    @Override
    public void beginServerLoading(MinecraftServer server)
    {
        serverShouldBeKilledQuietly = false;
        // NOOP
    }

    @Override
    public void finishServerLoading()
    {
        // NOOP
    }

    @Override
    public File getSavesDirectory()
    {
        return ((SaveFormatOld) client.getSaveLoader()).savesDirectory;
    }

    @Override
    public MinecraftServer getServer()
    {
        return client.getIntegratedServer();
    }

    public void displayMissingMods(Object modMissingPacket)
    {
//        showGuiScreen(new GuiModsMissingForServer(modMissingPacket));
    }

    /**
     * If the client is in the midst of loading, we disable saving so that custom settings aren't wiped out
     */
    public boolean isLoading()
    {
        return loading;
    }

    @Override
    public boolean shouldServerShouldBeKilledQuietly()
    {
        return serverShouldBeKilledQuietly;
    }

    /**
     * Is this GUI type open?
     *
     * @param gui The type of GUI to test for
     * @return if a GUI of this type is open
     */
    public boolean isGUIOpen(Class<? extends GuiScreen> gui)
    {
        return client.currentScreen != null && client.currentScreen.getClass().equals(gui);
    }


    @Override
    public void addModAsResource(ModContainer container)
    {
        LanguageRegistry.instance().loadLanguagesFor(container, Side.CLIENT);
        Class<?> resourcePackType = container.getCustomResourcePackClass();
        if (resourcePackType != null)
        {
            try
            {
                IResourcePack pack = (IResourcePack) resourcePackType.getConstructor(ModContainer.class).newInstance(container);
                resourcePackList.add(pack);
                resourcePackMap.put(container.getModId(), pack);
            }
            catch (NoSuchMethodException e)
            {
                FMLLog.log(Level.ERROR, "The container %s (type %s) returned an invalid class for it's resource pack.", container.getName(), container.getClass().getName());
                return;
            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "An unexpected exception occurred constructing the custom resource pack for %s", container.getName());
                throw Throwables.propagate(e);
            }
        }
    }

    public IResourcePack getResourcePackFor(String modId)
    {
        return resourcePackMap.get(modId);
    }

    @Override
    public String getCurrentLanguage()
    {
        return client.getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

    @Override
    public void serverStopped()
    {
        // If the server crashes during startup, it might hang the client- reset the client so it can abend properly.
        MinecraftServer server = getServer();

        if (server != null && !server.serverIsInRunLoop())
        {
            ObfuscationReflectionHelper.setPrivateValue(MinecraftServer.class, server, true, "field_71296"+"_Q","serverIs"+"Running");
        }
    }

    @Override
    public INetHandler getClientPlayHandler()
    {
        return this.currentPlayClient == null ? null : this.currentPlayClient.get();
    }
    @Override
    public NetworkManager getClientToServerNetworkManager()
    {
        return this.client.getNetHandler()!=null ? this.client.getNetHandler().getNetworkManager() : null;
    }

    public void handleClientWorldClosing(WorldClient world)
    {
        NetworkManager client = getClientToServerNetworkManager();
        // ONLY revert a non-local connection
        if (client != null && !client.isLocalChannel())
        {
            PersistentRegistryManager.revertToFrozen();
        }
    }

    public void startIntegratedServer(String id, String name, WorldSettings settings)
    {
    }

    public File getSavesDir()
    {
        return new File(client.mcDataDir, "saves");
    }
    public void tryLoadExistingWorld(GuiSelectWorld selectWorldGUI, String dirName, String saveName)
    {
        File dir = new File(getSavesDir(), dirName);
        NBTTagCompound leveldat;
        try
        {
            leveldat = CompressedStreamTools.readCompressed(new FileInputStream(new File(dir, "level.dat")));
        }
        catch (Exception e)
        {
            try
            {
                leveldat = CompressedStreamTools.readCompressed(new FileInputStream(new File(dir, "level.dat_old")));
            }
            catch (Exception e1)
            {
                FMLLog.warning("There appears to be a problem loading the save %s, both level files are unreadable.", dirName);
                return;
            }
        }
        NBTTagCompound fmlData = leveldat.getCompoundTag("FML");
        if (fmlData.hasKey("ModItemData"))
        {
            showGuiScreen(new GuiOldSaveLoadConfirm(dirName, saveName, selectWorldGUI));
        }
        else
        {
            try
            {
                client.launchIntegratedServer(dirName, saveName, (WorldSettings)null);
            }
            catch (StartupQuery.AbortedException e)
            {
                // ignore
            }
        }
    }

    public void showInGameModOptions(GuiIngameMenu guiIngameMenu)
    {
        showGuiScreen(new GuiModList(guiIngameMenu));
    }

    public IModGuiFactory getGuiFactoryFor(ModContainer selectedMod)
    {
        return guiFactories.get(selectedMod);
    }


    public void setupServerList()
    {
        extraServerListData = Collections.synchronizedMap(Maps.<ServerStatusResponse,JsonObject>newHashMap());
        serverDataTag = Collections.synchronizedMap(Maps.<ServerData,ExtendedServerListData>newHashMap());
    }

    public void captureAdditionalData(ServerStatusResponse serverstatusresponse, JsonObject jsonobject)
    {
        if (jsonobject.has("modinfo"))
        {
            JsonObject fmlData = jsonobject.get("modinfo").getAsJsonObject();
            extraServerListData.put(serverstatusresponse, fmlData);
        }
    }
    public void bindServerListData(ServerData data, ServerStatusResponse originalResponse)
    {
        if (extraServerListData.containsKey(originalResponse))
        {
            JsonObject jsonData = extraServerListData.get(originalResponse);
            String type = jsonData.get("type").getAsString();
            JsonArray modDataArray = jsonData.get("modList").getAsJsonArray();
            boolean moddedClientAllowed = jsonData.has("clientModsAllowed") ? jsonData.get("clientModsAllowed").getAsBoolean() : true;
            Builder<String, String> modListBldr = ImmutableMap.<String,String>builder();
            for (JsonElement obj : modDataArray)
            {
                JsonObject modObj = obj.getAsJsonObject();
                modListBldr.put(modObj.get("modid").getAsString(), modObj.get("version").getAsString());
            }

            Map<String,String> modListMap = modListBldr.build();
            serverDataTag.put(data, new ExtendedServerListData(type, FMLNetworkHandler.checkModList(modListMap, Side.SERVER) == null, modListMap, !moddedClientAllowed));
        }
        else
        {
            String serverDescription = data.serverMOTD;
            boolean moddedClientAllowed = true;
            if (!Strings.isNullOrEmpty(serverDescription))
            {
                moddedClientAllowed = !serverDescription.endsWith(":NOFML§r");
            }
            serverDataTag.put(data, new ExtendedServerListData("VANILLA", false, ImmutableMap.<String,String>of(), !moddedClientAllowed));
        }
        startupConnectionData.countDown();
    }

    private static final ResourceLocation iconSheet = new ResourceLocation("fml:textures/gui/icons.png");
    private static final CountDownLatch startupConnectionData = new CountDownLatch(1);

    public String enhanceServerListEntry(ServerListEntryNormal serverListEntry, ServerData serverEntry, int x, int width, int y, int relativeMouseX, int relativeMouseY)
    {
        String tooltip;
        int idx;
        boolean blocked = false;
        if (serverDataTag.containsKey(serverEntry))
        {
            ExtendedServerListData extendedData = serverDataTag.get(serverEntry);
            if ("FML".equals(extendedData.type) && extendedData.isCompatible)
            {
                idx = 0;
                tooltip = String.format("Compatible FML modded server\n%d mods present", extendedData.modData.size());
            }
            else if ("FML".equals(extendedData.type) && !extendedData.isCompatible)
            {
                idx = 16;
                tooltip = String.format("Incompatible FML modded server\n%d mods present", extendedData.modData.size());
            }
            else if ("BUKKIT".equals(extendedData.type))
            {
                idx = 32;
                tooltip = String.format("Bukkit modded server");
            }
            else if ("VANILLA".equals(extendedData.type))
            {
                idx = 48;
                tooltip = String.format("Vanilla server");
            }
            else
            {
                idx = 64;
                tooltip = String.format("Unknown server data");
            }
            blocked = extendedData.isBlocked;
        }
        else
        {
            return null;
        }
        this.client.getTextureManager().bindTexture(iconSheet);
        Gui.drawModalRectWithCustomSizedTexture(x + width - 18, y + 10, 0, (float)idx, 16, 16, 256.0f, 256.0f);
        if (blocked)
        {
            Gui.drawModalRectWithCustomSizedTexture(x + width - 18, y + 10, 0, 80, 16, 16, 256.0f, 256.0f);
        }

        return relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26 ? tooltip : null;
    }

    public String fixDescription(String description)
    {
        return description.endsWith(":NOFML§r") ? description.substring(0, description.length() - 8)+"§r" : description;
    }

    public void connectToServerAtStartup(String host, int port)
    {
        setupServerList();
        OldServerPinger osp = new OldServerPinger();
        ServerData serverData = new ServerData("Command Line", host+":"+port,false);
        try
        {
            osp.ping(serverData);
            startupConnectionData.await(30, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            showGuiScreen(new GuiConnecting(new GuiMainMenu(), client, host, port));
            return;
        }
        connectToServer(new GuiMainMenu(), serverData);
    }

    public void connectToServer(GuiScreen guiMultiplayer, ServerData serverEntry)
    {
        ExtendedServerListData extendedData = serverDataTag.get(serverEntry);
        if (extendedData != null && extendedData.isBlocked)
        {
            showGuiScreen(new GuiAccessDenied(guiMultiplayer, serverEntry));
        }
        else
        {
            showGuiScreen(new GuiConnecting(guiMultiplayer, client, serverEntry));
        }
    }

    public void connectToRealmsServer(String host, int port){}

    public void setPlayClient(NetHandlerPlayClient netHandlerPlayClient)
    {
        this.currentPlayClient = new WeakReference<NetHandlerPlayClient>(netHandlerPlayClient);
    }

    @Override
    public void fireNetRegistrationEvent(EventBus bus, NetworkManager manager, Set<String> channelSet, String channel, Side side)
    {
        if (side == Side.CLIENT)
        {
            bus.post(new FMLNetworkEvent.CustomPacketRegistrationEvent<NetHandlerPlayClient>(manager, channelSet, channel, side, NetHandlerPlayClient.class));
        }
        else
        {
            bus.post(new FMLNetworkEvent.CustomPacketRegistrationEvent<NetHandlerPlayServer>(manager, channelSet, channel, side, NetHandlerPlayServer.class));
        }
    }

    @Override
    public boolean shouldAllowPlayerLogins()
    {
        return true; //Always true as the server has to be started before clicking 'Open to lan'
    }

    @Override
    public void allowLogins() {
        // NOOP for integrated server
    }

    @Override
    public IThreadListener getWorldThread(INetHandler net)
    {
        if (net instanceof INetHandlerPlayClient ||
            net instanceof INetHandlerLoginClient ||
            net instanceof INetHandlerStatusClient) return getClient();
        if (net instanceof INetHandlerHandshakeServer ||
            net instanceof INetHandlerLoginServer ||
            net instanceof INetHandlerPlayServer ||
            net instanceof INetHandlerStatusServer) return getServer();
        throw new RuntimeException("Unknown INetHandler: " + net);
    }

    private SetMultimap<String,ResourceLocation> missingTextures = HashMultimap.create();
    private Set<String> badTextureDomains = Sets.newHashSet();
    private Table<String, String, Set<ResourceLocation>> brokenTextures = HashBasedTable.create();

    public void trackMissingTexture(ResourceLocation resourceLocation)
    {
        badTextureDomains.add(resourceLocation.getResourceDomain());
        missingTextures.put(resourceLocation.getResourceDomain(),resourceLocation);
    }

    public void trackBrokenTexture(ResourceLocation resourceLocation, String error)
    {
        badTextureDomains.add(resourceLocation.getResourceDomain());
        Set<ResourceLocation> badType = brokenTextures.get(resourceLocation.getResourceDomain(), error);
        if (badType == null)
        {
            badType = Sets.newHashSet();
            brokenTextures.put(resourceLocation.getResourceDomain(), Objects.firstNonNull(error, "Unknown error"), badType);
        }
        badType.add(resourceLocation);
    }

    public void logMissingTextureErrors()
    {
        if (missingTextures.isEmpty() && brokenTextures.isEmpty())
        {
            return;
        }
        Logger logger = LogManager.getLogger("TEXTURE ERRORS");
        logger.error(Strings.repeat("+=", 25));
        logger.error("The following texture errors were found.");
        Map<String,FallbackResourceManager> resManagers = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager(), "domainResourceManagers", "field_110548"+"_a");
        for (String resourceDomain : badTextureDomains)
        {
            Set<ResourceLocation> missing = missingTextures.get(resourceDomain);
            logger.error(Strings.repeat("=", 50));
            logger.error("  DOMAIN {}", resourceDomain);
            logger.error(Strings.repeat("-", 50));
            logger.error("  domain {} is missing {} texture{}",resourceDomain, missing.size(),missing.size()!=1 ? "s" : "");
            FallbackResourceManager fallbackResourceManager = resManagers.get(resourceDomain);
            if (fallbackResourceManager == null)
            {
                logger.error("    domain {} is missing a resource manager - it is probably a side-effect of automatic texture processing", resourceDomain);
            }
            else
            {
                List<IResourcePack> resPacks = ObfuscationReflectionHelper.getPrivateValue(FallbackResourceManager.class, fallbackResourceManager, "resourcePacks","field_110540"+"_a");
                logger.error("    domain {} has {} location{}:",resourceDomain, resPacks.size(), resPacks.size() != 1 ? "s" :"");
                for (IResourcePack resPack : resPacks)
                {
                    if (resPack instanceof FMLContainerHolder) {
                        FMLContainerHolder containerHolder = (FMLContainerHolder) resPack;
                        ModContainer fmlContainer = containerHolder.getFMLContainer();
                        logger.error("      mod {} resources at {}", fmlContainer.getModId(), fmlContainer.getSource().getPath());
                    }
                    else if (resPack instanceof AbstractResourcePack)
                    {
                        AbstractResourcePack resourcePack = (AbstractResourcePack) resPack;
                        File resPath = ObfuscationReflectionHelper.getPrivateValue(AbstractResourcePack.class, resourcePack, "resourcePackFile","field_110597"+"_b");
                        logger.error("      resource pack at path {}",resPath.getPath());
                    }
                    else
                    {
                        logger.error("      unknown resourcepack type {} : {}", resPack.getClass().getName(), resPack.getPackName());
                    }
                }
            }
            logger.error(Strings.repeat("-", 25));
            if (missingTextures.containsKey(resourceDomain)) {
                logger.error("    The missing resources for domain {} are:", resourceDomain);
                for (ResourceLocation rl : missing) {
                    logger.error("      {}", rl.getResourcePath());
                }
                logger.error(Strings.repeat("-", 25));
            }
            if (!brokenTextures.containsRow(resourceDomain))
            {
                logger.error("    No other errors exist for domain {}", resourceDomain);
            }
            else
            {
                logger.error("    The following other errors were reported for domain {}:",resourceDomain);
                Map<String, Set<ResourceLocation>> resourceErrs = brokenTextures.row(resourceDomain);
                for (String error: resourceErrs.keySet())
                {
                    logger.error(Strings.repeat("-", 25));
                    logger.error("    Problem: {}", error);
                    for (ResourceLocation rl : resourceErrs.get(error))
                    {
                        logger.error("      {}",rl.getResourcePath());
                    }
                }
            }
            logger.error(Strings.repeat("=", 50));
        }
        logger.error(Strings.repeat("+=", 25));
    }

    @Override
    public void processWindowMessages()
    {
        // workaround for windows requiring messages being processed on the main thread
        if (LWJGLUtil.getPlatform() != LWJGLUtil.PLATFORM_WINDOWS) return;
        // If we can't grab the mutex, the update call is blocked, probably in native code, just skip it and carry on
        // We'll get another go next time
        if (!SplashProgress.mutex.tryAcquire()) return;
        Display.processMessages();
        SplashProgress.mutex.release();
    }
    // From FontRenderer.renderCharAtPos
    private static final String ALLOWED_CHARS = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    @Override
    public String stripSpecialChars(String message)
    {
        // We can't handle many unicode points in the splash renderer
        return CharMatcher.anyOf(ALLOWED_CHARS).retainFrom(StringUtils.stripControlCodes(message));
    }
}
