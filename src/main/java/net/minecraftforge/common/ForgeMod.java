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

import net.minecraftforge.fml.FMLWorldPersistenceHook;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.WorldPersistenceHooks;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.command.ForgeCommand;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

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
    public static boolean alwaysSetupTerrainOffThread = false; // In RenderGlobal.setupTerrain, always force the chunk render updates to be queued to the thread
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
        WorldPersistenceHooks.addHook(this);
        WorldPersistenceHooks.addHook(new FMLWorldPersistenceHook());
        FMLModLoadingContext.get().getModEventBus().addListener(this::preInit);
        FMLModLoadingContext.get().getModEventBus().addListener(this::postInit);
        FMLModLoadingContext.get().getModEventBus().addListener(this::onAvailable);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::playerLogin);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
        /*
        FMLModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.spec);
        FMLModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.chunk_spec);
        FMLModLoadingContext.get().getModEventBus().register(ForgeConfig.class);
        */
        //Temporary, until I can talk to CPW about how certian types of config setups
        loadConfig(ForgeConfig.spec, FMLPaths.CONFIGDIR.get().resolve("forge.toml"));
        loadConfig(ForgeConfig.chunk_spec, FMLPaths.CONFIGDIR.get().resolve("forge_chunk.toml"));
    }

    private void loadConfig(ForgeConfigSpec spec, Path path) {
        LOGGER.debug(FORGEMOD, "Loading config file {}", path);
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
            .sync()
            .autosave()
            .writingMode(WritingMode.REPLACE)
            .build();
        LOGGER.debug(FORGEMOD, "Built TOML config for {}", path.toString());
        configData.load();
        LOGGER.debug(FORGEMOD, "Loaded TOML config file {}", path.toString());
        spec.setConfig(configData);
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
        // TODO fluids FluidRegistry.writeDefaultFluidList(forgeData);
        return forgeData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, NBTTagCompound tag)
    {
        DimensionManager.loadDimensionDataMap(tag.hasKey("DimensionData") ? tag.getCompound("DimensionData") : null);
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
}
