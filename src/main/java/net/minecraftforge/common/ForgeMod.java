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

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.IModelProperty;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.FMLWorldPersistenceHook;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.WorldPersistenceHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.server.command.ForgeCommand;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

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
        modEventBus.addGenericListener(Block.class, this::registerBlocks);
        modEventBus.addGenericListener(TileEntityType.class, this::registerTileEntities);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::playerLogin);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
        MinecraftForge.EVENT_BUS.addListener(this::modelBake);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.serverSpec);
        modEventBus.register(ForgeConfig.class);
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
    
    private static final IModelProperty<Boolean> MAGIC_PROP = new IModelProperty<Boolean>() {};
    
    public void modelBake(ModelBakeEvent event)
    {
        final IBakedModel stone = event.getModelRegistry().get(new ModelResourceLocation("minecraft:stone"));
        final IBakedModel dirt = event.getModelRegistry().get(new ModelResourceLocation("minecraft:dirt"));
        final IBakedModel old = event.getModelRegistry().get(new ModelResourceLocation("forge:modeltest"));
        event.getModelRegistry().put(new ModelResourceLocation("forge:modeltest"), new IDynamicBakedModel()
        {
            @Override
            public boolean isGui3d()
            {
                return false;
            }
            
            @Override
            public boolean isBuiltInRenderer()
            {
                return false;
            }
            
            @Override
            public boolean isAmbientOcclusion()
            {
                return true;
            }
            
            @Override
            public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand, IModelData modelData)
            {
                return modelData.getData(MAGIC_PROP) ? stone.getQuads(state, side, rand, modelData) : dirt.getQuads(state, side, rand, modelData);
            }
            
            @Override
            public TextureAtlasSprite getParticleTexture()
            {
                return MissingTextureSprite.getSprite();
            }
            
            @Override
            public ItemOverrideList getOverrides()
            {
                return null;
            }
            
            @Override
            @Nonnull
            public IModelData getModelData(@Nonnull IWorldReader world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull IModelData tileData)
            {
                if (world.getBlockState(pos.down()).getBlock() == Blocks.AIR)
                {
                    tileData.setData(MAGIC_PROP, !tileData.getData(MAGIC_PROP));
                }
                return tileData;
            }
        });
    }
    
    private static final TileEntityType<Tile> TILE_TYPE = TileEntityType.Builder.create(Tile::new).build(null);
    private static class Tile extends TileEntity implements ITickable {

        public Tile()
        {
            super(TILE_TYPE);
        }
        
        private int counter;

        @Override
        public void tick()
        {
            if (world.isRemote && counter++ == 100)
            {
                ModelDataManager.requestModelDataRefresh(this);
                world.markBlockRangeForRenderUpdate(getPos(), getPos());
            }
        }
        
        @Override
        public IModelData getModelData()
        {
            return new ModelDataMap.Builder().withInitial(MAGIC_PROP, counter < 100).build();
        }
    }
    
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new BlockContainer(Block.Builder.create(Material.ROCK, MapColor.AIR))
        {
            
            @Override
            @Nullable
            public TileEntity createNewTileEntity(IBlockReader worldIn)
            {
                return new Tile();
            }
            
            @Override
            public EnumBlockRenderType getRenderType(IBlockState state)
            {
                return EnumBlockRenderType.MODEL;
            }
        }.setRegistryName("forge:modeltest"));
    }
    
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().register(TILE_TYPE.setRegistryName("forge:modeltest"));
    }

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
    }

    public void serverStopping(FMLServerStoppingEvent evt)
    {
        WorldWorkerManager.clear();
    }

    @Override
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info)
    {
        NBTTagCompound forgeData = new NBTTagCompound();
        NBTTagCompound dims = new NBTTagCompound();
        DimensionManager.writeRegistry(dims);
        if (!dims.isEmpty())
            forgeData.setTag("dims", dims);
        // TODO fluids FluidRegistry.writeDefaultFluidList(forgeData);
        return forgeData;
    }

    @Override
    public void readData(SaveHandler handler, WorldInfo info, NBTTagCompound tag)
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
}
