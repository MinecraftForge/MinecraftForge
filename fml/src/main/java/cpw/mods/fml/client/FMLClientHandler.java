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
package cpw.mods.fml.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import org.apache.logging.log4j.Level;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.DuplicateModsFoundException;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.WorldAccessContainer;
import cpw.mods.fml.common.WrongMinecraftVersionException;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.Action;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistryException;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.relauncher.Side;


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

    @SuppressWarnings("unused")
    private boolean guiLoaded;

    @SuppressWarnings("unused")
    private boolean serverIsRunning;

    private MissingModsException modsMissing;

    private ModSortingException modSorting;

    private boolean loading = true;

    private WrongMinecraftVersionException wrongMC;

    private CustomModLoadingErrorDisplayException customError;

	private DuplicateModsFoundException dupesFound;

    private boolean serverShouldBeKilledQuietly;

    private List<IResourcePack> resourcePackList;

    @SuppressWarnings("unused")
    private IReloadableResourceManager resourceManager;

    private Map<String, IResourcePack> resourcePackMap;

    private BiMap<ModContainer, IModGuiFactory> guiFactories;

    private Map<ServerStatusResponse,JsonObject> extraServerListData;
    private Map<ServerData, ExtendedServerListData> serverDataTag;

    private NetHandlerPlayClient currentPlayClient;

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
        client = minecraft;
        this.resourcePackList = resourcePackList;
        this.resourceManager = resourceManager;
        this.resourcePackMap = Maps.newHashMap();
        if (minecraft.func_71355_q())
        {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.");
            haltGame("FML will not run in demo mode", new RuntimeException());
            return;
        }

        FMLCommonHandler.instance().beginLoading(this);
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

    @Override
    public void haltGame(String message, Throwable t)
    {
        client.func_71377_b(new CrashReport(message, t));
        throw Throwables.propagate(t);
    }
    /**
     * Called a bit later on during initialization to finish loading mods
     * Also initializes key bindings
     *
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    public void finishMinecraftLoading()
    {
        if (modsMissing != null || wrongMC != null || customError!=null || dupesFound!=null || modSorting!=null)
        {
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
            return;
        }
        catch (LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }

        // Reload resources
        client.func_110436_a();
        RenderingRegistry.instance().loadEntityRenderers((Map<Class<? extends Entity>, Render>)RenderManager.field_78727_a.field_78729_o);
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
        client.field_71474_y.func_74300_a(); //Reload options to load any mod added keybindings.
    }

    @SuppressWarnings("unused")
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
                Map<String, String> mod = modEntry.getValue();
                String modSystem = mod.get("modsystem"); // the modsystem (FML uses FML or ModLoader)
                String modId = mod.get("id"); // unique ID
                String modVersion = mod.get("version"); // version
                String modName = mod.get("name"); // a human readable name
                String modURL = mod.get("url"); // a URL for the mod (can be empty string)
                String modAuthors = mod.get("authors"); // a csv of authors (can be empty string)
                String modDescription = mod.get("description"); // a (potentially) multiline description (can be empty string)
            }
        }

    }
    public void onInitializationComplete()
    {
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
        }
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
        if (client.field_71439_g==player && gui != null) {
            client.func_147108_a(gui);
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
        client.func_147108_a(gui);
    }

    public WorldClient getWorldClient()
    {
        return client.field_71441_e;
    }

    public EntityClientPlayerMP getClientPlayerEntity()
    {
        return client.field_71439_g;
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
    public MinecraftServer getServer()
    {
        return client.func_71401_C();
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
        return client.field_71462_r != null && client.field_71462_r.getClass().equals(gui);
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

    @Override
    public void updateResourcePackList()
    {
        client.func_110436_a();
    }

    public IResourcePack getResourcePackFor(String modId)
    {
        return resourcePackMap.get(modId);
    }

    @Override
    public String getCurrentLanguage()
    {
        return client.func_135016_M().func_135041_c().func_135034_a();
    }

    @Override
    public void serverStopped()
    {
        // If the server crashes during startup, it might hang the client- reset the client so it can abend properly.
        MinecraftServer server = getServer();

        if (server != null && !server.func_71200_ad())
        {
            ObfuscationReflectionHelper.setPrivateValue(MinecraftServer.class, server, true, "field_71296"+"_Q","serverIs"+"Running");
        }
    }

    @Override
    public INetHandler getClientPlayHandler()
    {
        return this.currentPlayClient;
    }
    @Override
    public NetworkManager getClientToServerNetworkManager()
    {
        return this.client.func_147114_u()!=null ? this.client.func_147114_u().func_147298_b() : null;
    }

    public void handleClientWorldClosing(WorldClient world)
    {
        NetworkManager client = getClientToServerNetworkManager();
        // ONLY revert a non-local connection
        if (client != null && !client.func_150731_c())
        {
            GameData.revertToFrozen();
        }
    }

    public void startIntegratedServer(String id, String name, WorldSettings settings)
    {
        playClientBlock = new CountDownLatch(1);
    }

    public File getSavesDir()
    {
        return new File(client.field_71412_D, "saves");
    }
    public void tryLoadExistingWorld(GuiSelectWorld selectWorldGUI, String dirName, String saveName)
    {
        File dir = new File(getSavesDir(), dirName);
        NBTTagCompound leveldat;
        try
        {
            leveldat = CompressedStreamTools.func_74796_a(new FileInputStream(new File(dir, "level.dat")));
        }
        catch (Exception e)
        {
            try
            {
                leveldat = CompressedStreamTools.func_74796_a(new FileInputStream(new File(dir, "level.dat_old")));
            }
            catch (Exception e1)
            {
                FMLLog.warning("There appears to be a problem loading the save %s, both level files are unreadable.", dirName);
                return;
            }
        }
        NBTTagCompound fmlData = leveldat.func_74775_l("FML");
        if (fmlData.func_74764_b("ModItemData"))
        {
            showGuiScreen(new GuiOldSaveLoadConfirm(dirName, saveName, selectWorldGUI));
        }
        else
        {

            launchIntegratedServerCallback(dirName, saveName);
        }
    }

    private CountDownLatch gameReleaseLatch;
    private Thread clientWaiter;
    private GameRegistryException gre;

    public void launchIntegratedServerCallback(String dirName, String saveName)
    {
        try
        {
            try
            {
                Thread.interrupted();
                gameReleaseLatch = new CountDownLatch(1);
                clientWaiter = Thread.currentThread();
                client.func_71371_a(dirName, saveName, (WorldSettings)null);
                System.out.printf("POKEE %b\n", Thread.currentThread().isInterrupted());
                gameReleaseLatch.await();
            }
            catch (InterruptedException ie)
            {
                Thread.interrupted();
                throw gre;
            }
        }
        catch (GameRegistryException gre)
        {
            client.func_71403_a(null);
            showGuiScreen(new GuiModItemsMissing(gre.getItems(), gre.getMessage()));
        }
        Thread.interrupted();
    }

    public void showInGameModOptions(GuiIngameMenu guiIngameMenu)
    {
        showGuiScreen(new GuiIngameModOptions(guiIngameMenu));
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

            serverDataTag.put(data, new ExtendedServerListData(type, true, modListBldr.build(), !moddedClientAllowed));
        }
        else
        {
            String serverDescription = data.field_78843_d;
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
        this.client.func_110434_K().func_110577_a(iconSheet);
        Gui.func_146110_a(x + width - 18, y + 10, 0, (float)idx, 16, 16, 256.0f, 256.0f);
        if (blocked)
        {
            Gui.func_146110_a(x + width - 18, y + 10, 0, 80, 16, 16, 256.0f, 256.0f);
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
        ServerData serverData = new ServerData("Command Line", host+":"+port);
        try
        {
            osp.func_147224_a(serverData);
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
        playClientBlock = new CountDownLatch(1);
    }

    private CountDownLatch playClientBlock;

    public void setPlayClient(NetHandlerPlayClient netHandlerPlayClient)
    {
        playClientBlock.countDown();
        this.currentPlayClient = netHandlerPlayClient;
    }

    @Override
    public void waitForPlayClient()
    {
        boolean gotIt = false;
        try
        {
            gotIt = playClientBlock.await(1,TimeUnit.SECONDS);
        } catch (InterruptedException e)
        {
        }
        if (!gotIt)
        {
            throw new RuntimeException("Timeout waiting for client thread to catch up!");
        }
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

    public void setDefaultMissingAction(FMLMissingMappingsEvent.Action action)
    {
        this.defaultMissingAction = action;
    }

    private Action defaultMissingAction = FMLMissingMappingsEvent.Action.FAIL;

    @Override
    public Action getDefaultMissingAction()
    {
        return defaultMissingAction;
    }

    @Override
    public void serverLoadedSuccessfully()
    {
        if (gameReleaseLatch!=null)
        {
            gameReleaseLatch.countDown();
        }
    }

    @Override
    public void failedServerLoading(RuntimeException ex, WorldAccessContainer wac)
    {
        if (wac instanceof FMLContainer && ex instanceof GameRegistryException)
        {
            try
            {
                gre = (GameRegistryException) ex;
                Executors.newSingleThreadExecutor().submit(new Callable<Void>()
                {
                    // This needs to happen in a separate thread so that the server can "crash"
                    // The three pokes are for the client - it's in a sleep loop, and needs to get
                    // out of it
                    @Override
                    public Void call() throws Exception
                    {
                        System.err.println("POKE");
                        clientWaiter.interrupt();
                        Thread.sleep(50);
                        System.err.println("POKE");
                        clientWaiter.interrupt();
                        Thread.sleep(50);
                        System.err.println("POKE");
                        clientWaiter.interrupt();
                        return null;
                    }

                });

            }
            catch (Throwable t)
            {
                FMLLog.log(Level.ERROR, t, "stuff");
            }
        }
    }

    public boolean handlingCrash(CrashReport report)
    {
        return report.func_71505_b() instanceof GameRegistryException;
    }
}
