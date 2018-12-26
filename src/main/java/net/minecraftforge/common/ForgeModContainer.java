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

/*
public class ForgeModContainer extends FMLModContainer
{
    static final Logger log = LogManager.getLogger(ForgeVersion.MOD_ID);

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
        File cfgFile = new File(FMLPaths.FMLCONFIG.get().toFile(), "forge.cfg");
        config = new Configuration(cfgFile);

        syncConfig(true);

        INSTANCE = this;
    }

    @Override
    public String getGuiClassName()
    {
        return "net.minecraftforge.client.gui.ForgeGuiFactory";
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
    public ForgeModContainer(ModLoadingClassLoader classLoader)
    {
        super(DefaultModInfos.forgeModInfo, "net.minecraftforge.common.ForgeMod", classLoader, null);
        ForgeConfig.load();
    }

}

*/
