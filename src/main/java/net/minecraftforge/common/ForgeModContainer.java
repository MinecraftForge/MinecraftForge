/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import static net.minecraftforge.common.ForgeVersion.buildVersion;
import static net.minecraftforge.common.ForgeVersion.majorVersion;
import static net.minecraftforge.common.ForgeVersion.minorVersion;
import static net.minecraftforge.common.ForgeVersion.revisionVersion;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.network.ForgeNetworkHandler;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
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
    public static int clumpingThreshold = 64;
    public static boolean removeErroringEntities = false;
    public static boolean removeErroringTileEntities = false;
    public static boolean disableStitchedFileSaving = false;
    public static boolean forceDuplicateFluidBlockCrash = true;
    public static boolean fullBoundingBoxLadders = false;
    public static double zombieSummonBaseChance = 0.1;
    public static int[] blendRanges = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 };
    public static float zombieBabyChance = 0.05f;
    public static boolean shouldSortRecipies = true;
    public static boolean disableVersionCheck = false;
    public static int defaultSpawnFuzz = 20;

    private static Configuration config;

    public ForgeModContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId       = "Forge";
        meta.name        = "Minecraft Forge";
        meta.version     = String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
        meta.credits     = "Made possible with help from many people";
        meta.authorList  = Arrays.asList("LexManos", "Eloraam", "Spacetoad");
        meta.description = "Minecraft Forge is a common open source API allowing a broad range of mods " +
                           "to work cooperatively together. It allows many mods to be created without " +
                           "them editing the main Minecraft code.";
        meta.url         = "http://MinecraftForge.net";
        meta.updateUrl   = "http://MinecraftForge.net/forum/index.php/topic,5.0.html";
        meta.screenshots = new String[0];
        meta.logoFile    = "/forge_logo.png";

        config = null;
        File cfgFile = new File(Loader.instance().getConfigDir(), "forge.cfg");
        config = new Configuration(cfgFile);

        syncConfig(true);
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

        prop = config.get(Configuration.CATEGORY_GENERAL, "forceDuplicateFluidBlockCrash", true);
        prop.comment = "Set this to true to force a crash if more than one block attempts to link back to the same Fluid. Enabled by default.";
        prop.setLanguageKey("forge.configgui.forceDuplicateFluidBlockCrash").setRequiresMcRestart(true);
        forceDuplicateFluidBlockCrash = prop.getBoolean(true);
        propOrder.add(prop.getName());

        if (!forceDuplicateFluidBlockCrash)
        {
            FMLLog.warning("Disabling forced crashes on duplicate Fluid Blocks - USE AT YOUR OWN RISK");
        }

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

        config.setCategoryPropertyOrder(CATEGORY_GENERAL, propOrder);

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
        }
    }

    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        UsernameCache.setUsername(event.player.getGameProfile().getId(), event.player.getGameProfile().getName());
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
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
        ForgeChunkManager.captureConfig(evt.getModConfigurationDirectory());
        FMLCommonHandler.instance().bus().register(this);
        
        if (!ForgeModContainer.disableVersionCheck)
        {
            ForgeVersion.startVersionCheck();
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
        return forgeData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        DimensionManager.loadDimensionDataMap(tag.hasKey("DimensionData") ? tag.getCompoundTag("DimensionData") : null);
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
}
