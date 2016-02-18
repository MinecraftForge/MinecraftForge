/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import static net.minecraftforge.common.config.Configuration.CATEGORY_CLIENT;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.network.ForgeNetworkHandler;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.server.command.ForgeCommand;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.WorldAccessContainer;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ForgeModContainer extends DummyModContainer implements WorldAccessContainer
{
    public static final String VERSION_CHECK_CAT = "version_checking";
    public static int clumpingThreshold = 64;
    public static boolean removeErroringEntities = false;
    public static boolean removeErroringTileEntities = false;
    public static boolean disableStitchedFileSaving = false;
    public static boolean fullBoundingBoxLadders = false;
    public static double zombieSummonBaseChance = 0.1;
    public static int[] blendRanges = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 };
    public static float zombieBabyChance = 0.05f;
    public static boolean shouldSortRecipies = true;
    public static boolean disableVersionCheck = false;
    public static int defaultSpawnFuzz = 20;
    public static boolean defaultHasSpawnFuzz = true;
    public static boolean forgeLightPipelineEnabled = true;
    public static boolean replaceVanillaBucketModel = true;

    private static Configuration config;
    private static ForgeModContainer INSTANCE;
    public static ForgeModContainer getInstance()
    {
        return INSTANCE;
    }

    private URL updateJSONUrl = null;
    public UniversalBucket universalBucket;

    public ForgeModContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId       = "Forge";
        meta.name        = "Minecraft Forge";
        meta.version     = ForgeVersion.getVersion();
        meta.credits     = "Made possible with help from many people";
        meta.authorList  = Arrays.asList("LexManos", "cpw", "fry");
        meta.description = "Minecraft Forge is a common open source API allowing a broad range of mods " +
                           "to work cooperatively together. It allows many mods to be created without " +
                           "them editing the main Minecraft code.";
        meta.url         = "http://minecraftforge.net";
        meta.screenshots = new String[0];
        meta.logoFile    = "/forge_logo.png";
        try {
            updateJSONUrl    = new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/promotions_slim.json");
        } catch (MalformedURLException e) {}

        config = null;
        File cfgFile = new File(Loader.instance().getConfigDir(), "forge.cfg");
        config = new Configuration(cfgFile);

        syncConfig(true);

        INSTANCE = this;
    }

    @Override
    public String getGuiClassName()
    {
        return "net.minecraftforge.client.gui.ForgeGuiFactory";
    }

    public static Configuration getConfig()
    {
        return config;
    }

    /**
     * Synchronizes the local fields with the values in the Configuration object.
     */
    private static void syncConfig(boolean load)
    {
        // By adding a property order list we are defining the order that the properties will appear both in the config file and on the GUIs.
        // Property order lists are defined per-ConfigCategory.
        List<String> propOrder = new ArrayList<String>();

        if (!config.isChild)
        {
            if (load)
            {
                config.load();
            }
            Property enableGlobalCfg = config.get(Configuration.CATEGORY_GENERAL, "enableGlobalConfig", false).setShowInGui(false);
            if (enableGlobalCfg.getBoolean(false))
            {
                Configuration.enableGlobalConfig();
            }
        }

        Property prop;

        prop = config.get(CATEGORY_GENERAL, "disableVersionCheck", false);
        prop.comment = "Set to true to disable Forge's version check mechanics. Forge queries a small json file on our server for version information. For more details see the ForgeVersion class in our github.";
        // Language keys are a good idea to implement if you are using config GUIs. This allows you to use a .lang file that will hold the
        // "pretty" version of the property name as well as allow others to provide their own localizations.
        // This language key is also used to get the tooltip for a property. The tooltip language key is langKey + ".tooltip".
        // If no tooltip language key is defined in your .lang file, the tooltip will default to the property comment field.
        prop.setLanguageKey("forge.configgui.disableVersionCheck");
        disableVersionCheck = prop.getBoolean(disableVersionCheck);
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "clumpingThreshold", 64,
                "Controls the number threshold at which Packet51 is preferred over Packet52, default and minimum 64, maximum 1024", 64, 1024);
        prop.setLanguageKey("forge.configgui.clumpingThreshold").setRequiresWorldRestart(true);
        clumpingThreshold = prop.getInt(64);
        if (clumpingThreshold > 1024 || clumpingThreshold < 64)
        {
            clumpingThreshold = 64;
            prop.set(64);
        }
        propOrder.add(prop.getName());

        prop = config.get(CATEGORY_GENERAL, "sortRecipies", true);
        prop.comment = "Set to true to enable the post initialization sorting of crafting recipes using Forge's sorter. May cause desyncing on conflicting recipies. MUST RESTART MINECRAFT IF CHANGED FROM THE CONFIG GUI.";
        prop.setLanguageKey("forge.configgui.sortRecipies").setRequiresMcRestart(true);
        shouldSortRecipies = prop.getBoolean(shouldSortRecipies);
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "removeErroringEntities", false);
        prop.comment = "Set this to true to remove any Entity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.";
        prop.setLanguageKey("forge.configgui.removeErroringEntities").setRequiresWorldRestart(true);
        removeErroringEntities = prop.getBoolean(false);
        propOrder.add(prop.getName());

        if (removeErroringEntities)
        {
            FMLLog.warning("Enabling removal of erroring Entities - USE AT YOUR OWN RISK");
        }

        prop = config.get(Configuration.CATEGORY_GENERAL, "removeErroringTileEntities", false);
        prop.comment = "Set this to true to remove any TileEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.";
        prop.setLanguageKey("forge.configgui.removeErroringTileEntities").setRequiresWorldRestart(true);
        removeErroringTileEntities = prop.getBoolean(false);
        propOrder.add(prop.getName());

        if (removeErroringTileEntities)
        {
            FMLLog.warning("Enabling removal of erroring Tile Entities - USE AT YOUR OWN RISK");
        }

        prop = config.get(Configuration.CATEGORY_GENERAL, "disableStitchedFileSaving", true);
        prop.comment = "Set this to just disable the texture stitcher from writing the '{name}_{mipmap}.png files to disc. Just a small performance tweak. Default: true";
        disableStitchedFileSaving = prop.getBoolean(true);

        prop = config.get(Configuration.CATEGORY_GENERAL, "fullBoundingBoxLadders", false);
        prop.comment = "Set this to true to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticable differences in mechanics so default is vanilla behavior. Default: false";
        prop.setLanguageKey("forge.configgui.fullBoundingBoxLadders").setRequiresWorldRestart(true);
        fullBoundingBoxLadders = prop.getBoolean(false);
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "biomeSkyBlendRange", new int[] { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 });
        prop.comment = "Control the range of sky blending for colored skies in biomes.";
        prop.setLanguageKey("forge.configgui.biomeSkyBlendRange");
        blendRanges = prop.getIntList();
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "zombieBaseSummonChance", 0.1,
                "Base zombie summoning spawn chance. Allows changing the bonus zombie summoning mechanic.", 0.0D, 1.0D);
        prop.setLanguageKey("forge.configgui.zombieBaseSummonChance").setRequiresWorldRestart(true);
        zombieSummonBaseChance = prop.getDouble(0.1);
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "zombieBabyChance", 0.05,
                "Chance that a zombie (or subclass) is a baby. Allows changing the zombie spawning mechanic.", 0.0D, 1.0D);
        prop.setLanguageKey("forge.configgui.zombieBabyChance").setRequiresWorldRestart(true);
        zombieBabyChance = (float) prop.getDouble(0.05);
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "defaultSpawnFuzz", 20,
            "The spawn fuzz when a player respawns in the world, this is controlable by WorldType, this config option is for the default overworld.",
            1, Integer.MAX_VALUE);
        prop.setLanguageKey("forge.configgui.spawnfuzz").setRequiresWorldRestart(false);
        defaultSpawnFuzz = prop.getInt(20);
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "spawnHasFuzz", Boolean.TRUE,
                "If the overworld has ANY spawn fuzz at all. If not, the spawn will always be the exact same location.");
        prop.setLanguageKey("forge.configgui.hasspawnfuzz").setRequiresWorldRestart(false);
        defaultHasSpawnFuzz = prop.getBoolean(Boolean.TRUE);
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "forgeLightPipelineEnabled", Boolean.TRUE,
                "Enable the forge block rendering pipeline - fixes the lighting of custom models.");
        forgeLightPipelineEnabled = prop.getBoolean(Boolean.TRUE);
        propOrder.add(prop.getName());

        config.setCategoryPropertyOrder(CATEGORY_GENERAL, propOrder);

        propOrder = new ArrayList<String>();
        prop = config.get(VERSION_CHECK_CAT, "Global", true, "Enable the entire mod update check system. This only applies to mods using the Forge system.");
        propOrder.add("Global");

        config.setCategoryPropertyOrder(VERSION_CHECK_CAT, propOrder);

        // Client-Side only properties
        propOrder = new ArrayList<String>();
        prop = config.get(Configuration.CATEGORY_CLIENT, "replaceVanillaBucketModel", Boolean.FALSE,
                "Replace the vanilla bucket models with Forges own dynamic bucket model. Unifies bucket visuals if a mod uses the Forge bucket model.");
        prop.setLanguageKey("forge.configgui.replaceBuckets").setRequiresMcRestart(true);
        replaceVanillaBucketModel = prop.getBoolean(Boolean.FALSE);
        propOrder.add(prop.getName());

        config.setCategoryPropertyOrder(CATEGORY_CLIENT, propOrder);

        if (config.hasChanged())
        {
            config.save();
        }
    }

    /**
     * By subscribing to the OnConfigChangedEvent we are able to execute code when our config screens are closed.
     * This implementation uses the optional configID string to handle multiple Configurations using one event handler.
     */
    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event)
    {
        if (getMetadata().modId.equals(event.modID) && !event.isWorldRunning)
        {
            if (Configuration.CATEGORY_GENERAL.equals(event.configID))
            {
                syncConfig(false);
            }
            else if ("chunkLoader".equals(event.configID))
            {
                ForgeChunkManager.syncConfigDefaults();
                ForgeChunkManager.loadConfiguration();
            }
            else if (VERSION_CHECK_CAT.equals(event.configID))
            {
                syncConfig(false);
            }
        }
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        UsernameCache.setUsername(event.player.getPersistentID(), event.player.getGameProfile().getName());
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void modConstruction(FMLConstructionEvent evt)
    {
        NetworkRegistry.INSTANCE.register(this, this.getClass(), "*", evt.getASMHarvestedData());
        ForgeNetworkHandler.registerChannel(this, evt.getSide());
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt)
    {
        CapabilityItemHandler.register();
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
        ForgeChunkManager.captureConfig(evt.getModConfigurationDirectory());
        MinecraftForge.EVENT_BUS.register(this);

        if (!ForgeModContainer.disableVersionCheck)
        {
            ForgeVersion.startVersionCheck();
        }

        // Add and register the forge universal bucket, if it's enabled
        if(FluidRegistry.isUniversalBucketEnabled())
        {
            universalBucket = new UniversalBucket();
            universalBucket.setUnlocalizedName("forge.bucketFilled");
            GameRegistry.registerItem(universalBucket, "bucketFilled");
            MinecraftForge.EVENT_BUS.register(universalBucket);
        }
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent evt)
    {
        BiomeDictionary.registerAllBiomesAndGenerateEvents();
        ForgeChunkManager.loadConfiguration();
    }

    @Subscribe
    public void onAvailable(FMLLoadCompleteEvent evt)
    {
        if (shouldSortRecipies)
        {
            RecipeSorter.sortCraftManager();
        }
        FluidRegistry.validateFluidRegistry();
    }

    @Subscribe
    public void serverStarting(FMLServerStartingEvent evt)
    {
        evt.registerServerCommand(new ForgeCommand(evt.getServer()));
    }
    @Override
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        NBTTagCompound forgeData = new NBTTagCompound();
        NBTTagCompound dimData = DimensionManager.saveDimensionDataMap();
        forgeData.setTag("DimensionData", dimData);
        FluidRegistry.writeDefaultFluidList(forgeData);
        return forgeData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        DimensionManager.loadDimensionDataMap(tag.hasKey("DimensionData") ? tag.getCompoundTag("DimensionData") : null);
        FluidRegistry.loadFluidDefaults(tag);
    }

    @Subscribe
    public void mappingChanged(FMLModIdMappingEvent evt)
    {
        OreDictionary.rebakeMap();
    }


    @Override
    public File getSource()
    {
        return FMLForgePlugin.forgeLocation;
    }
    @Override
    public Class<?> getCustomResourcePackClass()
    {
        if (getSource().isDirectory())
        {
            return FMLFolderResourcePack.class;
        }
        else
        {
            return FMLFileResourcePack.class;
        }
    }

    @Override
    public List<String> getOwnedPackages()
    {
        // All the packages which are part of forge. Only needs updating if new logic is added
        // that requires event handlers
        return ImmutableList.of(
                "net.minecraftforge.classloading",
                "net.minecraftforge.client",
                "net.minecraftforge.client.event",
                "net.minecraftforge.client.event.sound",
                "net.minecraftforge.client.model",
                "net.minecraftforge.client.model.obj",
                "net.minecraftforge.client.model.techne",
                "net.minecraftforge.common",
                "net.minecraftforge.common.config",
                "net.minecraftforge.common.network",
                "net.minecraftforge.common.util",
                "net.minecraftforge.event",
                "net.minecraftforge.event.brewing",
                "net.minecraftforge.event.entity",
                "net.minecraftforge.event.entity.item",
                "net.minecraftforge.event.entity.living",
                "net.minecraftforge.event.entity.minecart",
                "net.minecraftforge.event.entity.player",
                "net.minecraftforge.event.terraingen",
                "net.minecraftforge.event.world",
                "net.minecraftforge.fluids",
                "net.minecraftforge.oredict",
                "net.minecraftforge.server",
                "net.minecraftforge.server.command",
                "net.minecraftforge.transformers"
                );
    }



    @Override
    public Certificate getSigningCertificate()
    {
        Certificate[] certificates = getClass().getProtectionDomain().getCodeSource().getCertificates();
        return certificates != null ? certificates[0] : null;
    }

    @Override
    public URL getUpdateUrl()
    {
        return updateJSONUrl;
    }
}
