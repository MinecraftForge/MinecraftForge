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

package net.minecraftforge.common;

import net.minecraftforge.fml.ModLoadingClassLoader;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.DefaultModInfos;

public class ForgeModContainer extends FMLModContainer
{
/*
    public static final String VERSION_CHECK_CAT = "version_checking";
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
    public static boolean selectiveResourceReloadEnabled = false;
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
        meta.modId       = ForgeVersion.MOD_ID;
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

    private static void remapGeneralPropertyToClient(String key)
    {
        ConfigCategory GENERAL = config.getCategory(CATEGORY_GENERAL);
        if (GENERAL.containsKey(key))
        {
            FMLLog.log.debug("Remapping property {} from category general to client", key);
            Property property = GENERAL.get(key);
            GENERAL.remove(key);
            config.getCategory(CATEGORY_CLIENT).put(key, property);
        }
    }

    /**
     * Synchronizes the local fields with the values in the Configuration object.
     * /
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
            FMLLog.log.warn("Enabling removal of erroring Entities - USE AT YOUR OWN RISK");
        }

        prop = config.get(Configuration.CATEGORY_GENERAL, "removeErroringTileEntities", false);
        prop.setComment("Set this to true to remove any TileEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.");
        prop.setLanguageKey("forge.configgui.removeErroringTileEntities").setRequiresWorldRestart(true);
        removeErroringTileEntities = prop.getBoolean(false);
        propOrder.add(prop.getName());

        if (removeErroringTileEntities)
        {
            FMLLog.log.warn("Enabling removal of erroring Tile Entities - USE AT YOUR OWN RISK");
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

        prop = config.get(Configuration.CATEGORY_CLIENT, "selectiveResourceReloadEnabled", false,
                "When enabled, makes specific reload tasks such as language changing quicker to run.");
        selectiveResourceReloadEnabled = prop.getBoolean(false);
        prop.setLanguageKey("forge.configgui.selectiveResourceReloadEnabled");
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
     * /
    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event)
    {
        if (getMetadata().modId.equals(event.getModID()))
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
                    FMLCommonHandler.instance().reloadRenderers();
                }

            }
        }
    }

    @SubscribeEvent
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

    @net.minecraftforge.eventbus.api.SubscribeEvent
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
        InputStream is = ForgeModContainer.class.getResourceAsStream("/META-INF/vanilla_annotations.json");
        if (is != null)
            JsonAnnotationLoader.loadJson(is, null, evt.getASMHarvestedData());
        log.debug("Loading Vanilla annotations: " + is);

        List<String> all = Lists.newArrayList();
        for (ASMData asm : evt.getASMHarvestedData().getAll(ICrashReportDetail.class.getName().replace('.', '/')))
            all.add(asm.getClassName());
        for (ASMData asm : evt.getASMHarvestedData().getAll(ICrashCallable.class.getName().replace('.', '/')))
            all.add(asm.getClassName());
        // Add table classes for mod list tabulation
        all.add("net/minecraftforge/common/util/TextTable");
        all.add("net/minecraftforge/common/util/TextTable$Column");
        all.add("net/minecraftforge/common/util/TextTable$Row");
        all.add("net/minecraftforge/common/util/TextTable$Alignment");

        all.removeIf(cls -> !cls.startsWith("net/minecraft/") && !cls.startsWith("net/minecraftforge/"));

        log.debug("Preloading CrashReport Classes");
        Collections.sort(all); //Sort it because I like pretty output ;)
        for (String name : all)
        {
            log.debug("\t{}", name);
            try
            {
                Class.forName(name.replace('/', '.'), false, MinecraftForge.class.getClassLoader());
            }
            catch (Exception e)
            {
                log.error("Could not find class for name '{}'.", name, e);
            }
        }

        NetworkRegistry.INSTANCE.register(this, this.getClass(), "*", evt.getASMHarvestedData());
        ForgeNetworkHandler.registerChannel(this, evt.getSide());
        ConfigManager.sync(this.getModId(), Config.Type.INSTANCE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt)
    {
        CapabilityItemHandler.register();
        CapabilityFluidHandler.register();
        CapabilityAnimation.register();
        CapabilityEnergy.register();
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(ForgeClientHandler.class);
        }
        ForgeChunkManager.captureConfig(evt.getModConfigurationDirectory());
        MinecraftForge.EVENT_BUS.register(this);

        if (!ForgeModContainer.disableVersionCheck)
        {
            ForgeVersion.startVersionCheck();
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
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

    @Subscribe
    public void postInit(FMLPostInitializationEvent evt)
    {
        registerAllBiomesAndGenerateEvents();
        ForgeChunkManager.loadConfiguration();
    }

    private static void registerAllBiomesAndGenerateEvents()
    {
        for (Biome biome : ForgeRegistries.BIOMES.getValuesCollection())
        {
            if (biome.decorator instanceof DeferredBiomeDecorator)
            {
                DeferredBiomeDecorator decorator = (DeferredBiomeDecorator)biome.decorator;
                decorator.fireCreateEventAndReplace(biome);
            }

            BiomeDictionary.ensureHasTypes(biome);
        }
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
        evt.registerServerCommand(new ForgeCommand());
    }

    @Subscribe
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
    public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
    {
        DimensionManager.loadDimensionDataMap(tag.hasKey("DimensionData") ? tag.getCompoundTag("DimensionData") : null);
        FluidRegistry.loadFluidDefaults(tag);
    }

    @Subscribe
    public void mappingChanged(FMLModIdMappingEvent evt)
    {
        OreDictionary.rebakeMap();
        StatList.reinit();
        Ingredient.invalidateAll();
        FMLCommonHandler.instance().resetClientRecipeBook();
        FMLCommonHandler.instance().reloadSearchTrees();
        FMLCommonHandler.instance().reloadCreativeSettings();
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
    @Nullable
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

    */
    public ForgeModContainer(ModLoadingClassLoader classLoader)
    {
        super(DefaultModInfos.forgeModInfo, "net.minecraftforge.common.ForgeMod", classLoader, null);
    }

}
