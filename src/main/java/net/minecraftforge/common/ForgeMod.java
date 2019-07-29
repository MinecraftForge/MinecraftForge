/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.ForgeCommand;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod("forge")
public class ForgeMod implements WorldPersistenceHooks.WorldPersistenceHook
{
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEMOD = MarkerManager.getMarker("FORGEMOD");
    public static int[] blendRanges = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 };
    public static boolean disableVersionCheck = false;
    public static boolean forgeLightPipelineEnabled = true;
    public static boolean zoomInMissingModelTextInGui = false;
    public static boolean disableStairSlabCulling = false; // Also known as the "DontCullStairsBecauseIUseACrappyTexturePackThatBreaksBasicBlockShapesSoICantTrustBasicBlockCulling" flag
    public static boolean alwaysSetupTerrainOffThread = false; // In WorldRenderer.setupTerrain, always force the chunk render updates to be queued to the thread
    public static boolean logCascadingWorldGeneration = true; // see Chunk#logCascadingWorldGeneration()
    public static boolean fixVanillaCascading = false; // There are various places in vanilla that cause cascading worldgen. Enabling this WILL change where blocks are placed to prevent this.
                                                       // DO NOT contact Forge about worldgen not 'matching' vanilla if this flag is set.

    private static ForgeMod INSTANCE;
    public static ForgeMod getInstance()
    {
        return INSTANCE;
    }

    public UniversalBucket universalBucket;

    public ForgeMod()
    {
        LOGGER.info(FORGEMOD,"Forge mod loading, version {}, for MC {} with MCP {}", ForgeVersion.getVersion(), MCPVersion.getMCVersion(), MCPVersion.getMCPVersion());
        INSTANCE = this;
        MinecraftForge.initialize();
        WorldPersistenceHooks.addHook(this);
        WorldPersistenceHooks.addHook(new FMLWorldPersistenceHook());
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::postInit);
        modEventBus.addListener(this::onAvailable);
        modEventBus.addListener(this::gatherData);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::playerLogin);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.serverSpec);
        modEventBus.register(ForgeConfig.class);
        // Forge does not display problems when the remote is not matching.
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, ()-> Pair.of(()->"ANY", (remote, isServer)-> true));
        StartupMessageManager.addModMessage("Forge version "+ForgeVersion.getVersion());
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
        UsernameCache.setUsername(event.getPlayer().getUniqueID(), event.getPlayer().getGameProfile().getName());
    }


    public void preInit(FMLCommonSetupEvent evt)
    {
        CapabilityItemHandler.register();
        CapabilityFluidHandler.register();
        CapabilityAnimation.register();
        CapabilityEnergy.register();
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
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
            event.getRegistry().register(universalBucket.setRegistryName(ForgeVersion.MOD_ID, "bucket_filled"));
            MinecraftForge.EVENT_BUS.register(universalBucket);
        }
    }
*/

    public void postInit(InterModProcessEvent evt)
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
        ConfigCommand.register(evt.getCommandDispatcher());
    }

    public void serverStopping(FMLServerStoppingEvent evt)
    {
        WorldWorkerManager.clear();
    }

    @Override
    public CompoundNBT getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        CompoundNBT forgeData = new CompoundNBT();
        CompoundNBT dims = new CompoundNBT();
        DimensionManager.writeRegistry(dims);
        if (!dims.isEmpty())
            forgeData.put("dims", dims);
        // TODO fluids FluidRegistry.writeDefaultFluidList(forgeData);
        return forgeData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, CompoundNBT tag)
    {
        if (tag.contains("dims", 10))
            DimensionManager.readRegistry(tag.getCompound("dims"));
        // TODO fluids FluidRegistry.loadFluidDefaults(tag);
    }

    public void mappingChanged(FMLModIdMappingEvent evt)
    {
    }

    @Override
    public String getModId()
    {
        return ForgeVersion.MOD_ID;
    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        if (event.includeServer())
        {
            gen.addProvider(new ForgeBlockTagsProvider(gen));
            gen.addProvider(new ForgeItemTagsProvider(gen));
            gen.addProvider(new ForgeRecipeProvider(gen));
        }
    }
}
