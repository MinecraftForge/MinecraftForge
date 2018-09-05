/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import static net.minecraftforge.common.config.Configuration.CATEGORY_CLIENT;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.WorldPersistenceHooks;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import net.minecraftforge.server.command.ForgeCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fluids.UniversalBucket;

import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ForgeMod implements WorldPersistenceHooks.WorldPersistenceHook
{
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEMOD = MarkerManager.getMarker("FORGEMOD");
    public static int clumpingThreshold = 64;
    public static boolean removeErroringEntities = false;
    public static boolean removeErroringTileEntities = false;
    public static boolean fullBoundingBoxLadders = false;
    public static double zombieSummonBaseChance = 0.1;
    public static int[] blendRanges = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 };
    public static float zombieBabyChance = 0.05f;
    public static boolean shouldSortRecipies = true;
    public static boolean disableVersionCheck = false;
    public static boolean forgeLightPipelineEnabled = true;
    @Deprecated // TODO remove in 1.13
    public static boolean replaceVanillaBucketModel = true;
    public static boolean zoomInMissingModelTextInGui = false;
    public static boolean forgeCloudsEnabled = true;
    public static boolean disableStairSlabCulling = false; // Also known as the "DontCullStairsBecauseIUseACrappyTexturePackThatBreaksBasicBlockShapesSoICantTrustBasicBlockCulling" flag
    public static boolean alwaysSetupTerrainOffThread = false; // In RenderGlobal.setupTerrain, always force the chunk render updates to be queued to the thread
    public static int dimensionUnloadQueueDelay = 0;
    public static boolean logCascadingWorldGeneration = true; // see Chunk#logCascadingWorldGeneration()
    public static boolean fixVanillaCascading = false; // There are various places in vanilla that cause cascading worldgen. Enabling this WILL change where blocks are placed to prevent this.
                                                       // DO NOT contact Forge about worldgen not 'matching' vanilla if this flag is set.

    static final Logger log = LogManager.getLogger(ForgeVersion.MOD_ID);

    private static Configuration config;
    private static ForgeMod INSTANCE;
    public static ForgeMod getInstance()
    {
        return INSTANCE;
    }

    public UniversalBucket universalBucket;

    public ForgeMod()
    {
        INSTANCE = this;
        FMLModLoadingContext.get().getModEventBus().addListener(this::preInit);
        FMLModLoadingContext.get().getModEventBus().addListener(this::postInit);
        FMLModLoadingContext.get().getModEventBus().addListener(this::onAvailable);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::playerLogin);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
    }

//    public static Configuration getConfig()
//    {
//        return config;
//    }

    private static void remapGeneralPropertyToClient(String key)
    {
        ConfigCategory GENERAL = config.getCategory(CATEGORY_GENERAL);
        if (GENERAL.containsKey(key))
        {
            LOGGER.debug(FORGEMOD, "Remapping property {} from category general to client",key);
            Property property = GENERAL.get(key);
            GENERAL.remove(key);
            config.getCategory(CATEGORY_CLIENT).put(key, property);
        }
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

        // clean up old properties that are not used anymore
        if (config.getCategory(CATEGORY_GENERAL).containsKey("defaultSpawnFuzz")) config.getCategory(CATEGORY_GENERAL).remove("defaultSpawnFuzz");
        if (config.getCategory(CATEGORY_GENERAL).containsKey("spawnHasFuzz")) config.getCategory(CATEGORY_GENERAL).remove("spawnHasFuzz");
        if (config.getCategory(CATEGORY_GENERAL).containsKey("disableStitchedFileSaving")) config.getCategory(CATEGORY_GENERAL).remove("disableStitchedFileSaving");
        if (config.getCategory(CATEGORY_CLIENT).containsKey("java8Reminder")) config.getCategory(CATEGORY_CLIENT).remove("java8Reminder");
        if (config.getCategory(CATEGORY_CLIENT).containsKey("replaceVanillaBucketModel")) config.getCategory(CATEGORY_CLIENT).remove("replaceVanillaBucketModel");

        // remap properties wrongly listed as general properties to client properties
        remapGeneralPropertyToClient("biomeSkyBlendRange");
        remapGeneralPropertyToClient("forgeLightPipelineEnabled");

        prop = config.get(CATEGORY_GENERAL, "disableVersionCheck", false);
        prop.setComment("Set to true to disable Forge's version check mechanics. Forge queries a small json file on our server for version information. For more details see the ForgeVersion class in our github.");
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
        prop.setComment("Set to true to enable the post initialization sorting of crafting recipes using Forge's sorter. May cause desyncing on conflicting recipes. MUST RESTART MINECRAFT IF CHANGED FROM THE CONFIG GUI.");
        prop.setLanguageKey("forge.configgui.sortRecipies").setRequiresMcRestart(true);
        shouldSortRecipies = prop.getBoolean(true);
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "removeErroringEntities", false);
        prop.setComment("Set this to true to remove any Entity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.");
        prop.setLanguageKey("forge.configgui.removeErroringEntities").setRequiresWorldRestart(true);
        removeErroringEntities = prop.getBoolean(false);
        propOrder.add(prop.getName());

        if (removeErroringEntities)
        {
            LOGGER.warn(FORGEMOD, "Enabling removal of erroring Entities - USE AT YOUR OWN RISK");
        }

        prop = config.get(Configuration.CATEGORY_GENERAL, "removeErroringTileEntities", false);
        prop.setComment("Set this to true to remove any TileEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.");
        prop.setLanguageKey("forge.configgui.removeErroringTileEntities").setRequiresWorldRestart(true);
        removeErroringTileEntities = prop.getBoolean(false);
        propOrder.add(prop.getName());

        if (removeErroringTileEntities)
        {
            LOGGER.warn(FORGEMOD, "Enabling removal of erroring Tile Entities - USE AT YOUR OWN RISK");
        }

        prop = config.get(Configuration.CATEGORY_GENERAL, "fullBoundingBoxLadders", false);
        prop.setComment("Set this to true to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticeable differences in mechanics so default is vanilla behavior. Default: false");
        prop.setLanguageKey("forge.configgui.fullBoundingBoxLadders").setRequiresWorldRestart(true);
        fullBoundingBoxLadders = prop.getBoolean(false);
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

        prop = config.get(Configuration.CATEGORY_GENERAL, "logCascadingWorldGeneration", true,
                "Log cascading chunk generation issues during terrain population.");
        logCascadingWorldGeneration = prop.getBoolean();
        prop.setLanguageKey("forge.configgui.logCascadingWorldGeneration");
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "fixVanillaCascading", false,
                "Fix vanilla issues that cause worldgen cascading. This DOES change vanilla worldgen so DO NOT report bugs related to world differences if this flag is on.");
        fixVanillaCascading = prop.getBoolean();
        prop.setLanguageKey("forge.configgui.fixVanillaCascading");
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_GENERAL, "dimensionUnloadQueueDelay", 0,
                "The time in ticks the server will wait when a dimension was queued to unload. " +
                        "This can be useful when rapidly loading and unloading dimensions, like e.g. throwing items through a nether portal a few time per second.");
        dimensionUnloadQueueDelay = prop.getInt(0);
        prop.setLanguageKey("forge.configgui.dimensionUnloadQueueDelay");
        propOrder.add(prop.getName());

        config.setCategoryPropertyOrder(CATEGORY_GENERAL, propOrder);

        propOrder = new ArrayList<String>();
        prop = config.get(VERSION_CHECK_CAT, "Global", true, "Enable the entire mod update check system. This only applies to mods using the Forge system.");
        propOrder.add("Global");

        config.setCategoryPropertyOrder(VERSION_CHECK_CAT, propOrder);

        // Client-Side only properties
        propOrder = new ArrayList<String>();

        prop = config.get(Configuration.CATEGORY_CLIENT, "zoomInMissingModelTextInGui", false,
        "Toggle off to make missing model text in the gui fit inside the slot.");
        zoomInMissingModelTextInGui = prop.getBoolean(false);
        prop.setLanguageKey("forge.configgui.zoomInMissingModelTextInGui");
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_CLIENT, "forgeCloudsEnabled", true,
                "Enable uploading cloud geometry to the GPU for faster rendering.");
        prop.setLanguageKey("forge.configgui.forgeCloudsEnabled");
        forgeCloudsEnabled = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_CLIENT, "disableStairSlabCulling", false,
                "Disable culling of hidden faces next to stairs and slabs. Causes extra rendering, but may fix some resource packs that exploit this vanilla mechanic.");
        disableStairSlabCulling = prop.getBoolean(false);
        prop.setLanguageKey("forge.configgui.disableStairSlabCulling");
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_CLIENT, "alwaysSetupTerrainOffThread", false,
                "Enable forge to queue all chunk updates to the Chunk Update thread. May increase FPS significantly, but may also cause weird rendering lag. Not recommended for computers " +
                "without a significant number of cores available.");
        alwaysSetupTerrainOffThread = prop.getBoolean(false);
        prop.setLanguageKey("forge.configgui.alwaysSetupTerrainOffThread");
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_CLIENT, "biomeSkyBlendRange", new int[] { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 });
        prop.setComment("Control the range of sky blending for colored skies in biomes.");
        prop.setLanguageKey("forge.configgui.biomeSkyBlendRange");
        blendRanges = prop.getIntList();
        propOrder.add(prop.getName());

        prop = config.get(Configuration.CATEGORY_CLIENT, "forgeLightPipelineEnabled", true,
                "Enable the forge block rendering pipeline - fixes the lighting of custom models.");
        forgeLightPipelineEnabled = prop.getBoolean(true);
        prop.setLanguageKey("forge.configgui.forgeLightPipelineEnabled");
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
    public void onConfigChanged(OnConfigChangedEvent event)
    {
        if (getModId().equals(event.getModID()))
        {
            if ("chunkLoader".equals(event.getConfigID()))
            {
                ForgeChunkManager.syncConfigDefaults();
                ForgeChunkManager.loadConfiguration();
            }
            else
            {
                boolean tmpStairs = disableStairSlabCulling;

                syncConfig(false);

                if (event.isWorldRunning() && tmpStairs != disableStairSlabCulling)
                {
                    DistExecutor.runWhenOn(Dist.CLIENT,()->()-> Minecraft.getMinecraft().renderGlobal.loadRenderers());
                }

            }
        }
    }

/*
    public void missingMapping(RegistryEvent.MissingMappings<Item> event)
    {
        for (MissingMappings.Mapping<Item> entry : event.getAllMappings())
        {
            if (entry.key.toString().equals("minecraft:totem")) //This item changed from 1.11 -> 1.11.2
            {
                ResourceLocation newTotem = new ResourceLocation("minecraft:totem_of_undying");
                entry.remap(ForgeRegistries.ITEMS.getValue(newTotem));
            }
        }
    }
*/

    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        UsernameCache.setUsername(event.player.getUniqueID(), event.player.getGameProfile().getName());
    }


    public void preInit(FMLPreInitializationEvent evt)
    {
        CapabilityItemHandler.register();
        CapabilityFluidHandler.register();
        CapabilityAnimation.register();
        CapabilityEnergy.register();
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
//        ForgeChunkManager.captureConfig(evt.getModConfigurationDirectory());
        MinecraftForge.EVENT_BUS.register(this);

        if (!ForgeMod.disableVersionCheck)
        {
            VersionChecker.startVersionCheck();
        }
    }

/*
    public void registrItems(RegistryEvent.Register<Item> event)
    {
        // Add and register the forge universal bucket, if it's enabled
        if(FluidRegistry.isUniversalBucketEnabled())
        {
            universalBucket = new UniversalBucket();
            universalBucket.setUnlocalizedName("forge.bucketFilled");
            event.getRegistry().register(universalBucket.setRegistryName(ForgeVersion.MOD_ID, "bucketFilled"));
            MinecraftForge.EVENT_BUS.register(universalBucket);
        }
    }
*/

    public void postInit(FMLPostInitializationEvent evt)
    {
        registerAllBiomesAndGenerateEvents();
        //ForgeChunkManager.loadConfiguration();
    }

    private static void registerAllBiomesAndGenerateEvents()
    {
/*
        for (Biome biome : ForgeRegistries.BIOMES.getValuesCollection())
        {
            if (biome.decorator instanceof DeferredBiomeDecorator)
            {
                DeferredBiomeDecorator decorator = (DeferredBiomeDecorator)biome.decorator;
                decorator.fireCreateEventAndReplace(biome);
            }

            BiomeDictionary.ensureHasTypes(biome);
        }
*/
    }

    public void onAvailable(FMLLoadCompleteEvent evt)
    {
//        FluidRegistry.validateFluidRegistry();
    }

    public void serverStarting(FMLServerStartingEvent evt)
    {
        new ForgeCommand(evt.getCommandDispatcher());
    }

    public void serverStopping(FMLServerStoppingEvent evt)
    {
        WorldWorkerManager.clear();
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
    public void readData(SaveHandler handler, WorldInfo info, NBTTagCompound tag)
    {
        DimensionManager.loadDimensionDataMap(tag.hasKey("DimensionData") ? tag.getCompoundTag("DimensionData") : null);
        FluidRegistry.loadFluidDefaults(tag);
    }

    public void mappingChanged(FMLModIdMappingEvent evt)
    {
    }

    @Override
    public String getModId()
    {
        return ForgeVersion.MOD_ID;
    }
}
