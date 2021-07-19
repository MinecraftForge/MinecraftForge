/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.ITag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features.Placements;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.SimplePlacement;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ResourceKeyTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonDataProvider;
import net.minecraftforge.common.data.ResourceKeyTagsProvider;
import net.minecraftforge.common.world.BiomeAmbienceBuilder;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.IBiomeParameters;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.DynamicRegistriesLoadedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(DynamicRegistriesLoadedEventTest.MODID)
public class DynamicRegistriesLoadedEventTest
{
    public static final String MODID = "dynamic_registries_loaded_event_test";

    public static final ITag.INamedTag<RegistryKey<Biome>> OPTIONAL_BIOMES_TEST_TAG = ResourceKeyTags.makeKeyTagWrapper(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "optional_biomes_test"));
    public static final ITag.INamedTag<RegistryKey<Biome>> EXTRA_BIOMES_TAG = ResourceKeyTags.makeKeyTagWrapper(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "extra_biomes"));
    public static final ITag.INamedTag<RegistryKey<Biome>> TESTING_BIOMES_TAG = ResourceKeyTags.makeKeyTagWrapper(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "testing_biomes"));

    public static final RegistryKey<Biome> MOUNTAINS = Biomes.MOUNTAINS;
    public static final RegistryKey<Biome> SNOWY_TUNDRA = Biomes.SNOWY_TUNDRA;
    public static final RegistryKey<Biome> TEST_BIOME = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "test_biome"));
    public static final Set<RegistryKey<Biome>> TESTING_BIOMES = ImmutableSet.of(MOUNTAINS, SNOWY_TUNDRA, TEST_BIOME);
    
    public static final RegistryKey<ConfiguredFeature<?,?>> RED_WOOL_CONFIGUREDFEATURE = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(MODID, "red_wool"));
    public static final RegistryKey<ConfiguredFeature<?,?>> BLACK_WOOL_CONFIGUREDFEATURE = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(MODID, "block_wool"));
    public static final RegistryKey<StructureFeature<?,?>> GOLD_TOWER_STRUCTURE_FEATURE = RegistryKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(MODID, "gold_tower"));

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final RegistryObject<SingleBlockFeature> SINGLE_BLOCK = FEATURES.register("single_block", () -> new SingleBlockFeature(BlockStateFeatureConfig.CODEC));

    public static final DeferredRegister<Placement<?>> PLACEMENTS = DeferredRegister.create(ForgeRegistries.DECORATORS, MODID);
    public static final RegistryObject<LimitedSquarePlacement> LIMITED_SQUARE = PLACEMENTS.register("limited_square", () -> new LimitedSquarePlacement(NoPlacementConfig.CODEC));
    
    public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, MODID);
    public static final RegistryObject<LoadableJigsawStructure> GOLD_TOWER = STRUCTURES.register("gold_tower", () -> new LoadableJigsawStructure(LoadableJigsawConfig.CODEC, new StructureSeparationSettings(4,2,-1757510426)));

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
    static
    {
        BIOMES.register("test_biome", () -> BiomeMaker.theVoidBiome());
    } // register dummy biome, json will override it

    // BiomeLoadingEvent configuredfeatures must be created and registered in java -- we'll do one of those to make sure BiomeLoadingEvent still works
    public static ConfiguredFeature<?, ?> blackWoolFeature;
    // DynamicRegistriesLoadedEvent can retrieve configuredfeatures from dynamic registries, we will register a separate feature by having a json for it

    public DynamicRegistriesLoadedEventTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // register deferred registers
        FEATURES.register(modBus);
        PLACEMENTS.register(modBus);
        BIOMES.register(modBus);
        STRUCTURES.register(modBus);

        // subscribe events
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onGatherData);

        forgeBus.addListener(this::onBiomeLoading);
        forgeBus.addListener(this::onDynamicRegistriesLoaded);
    }

    void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(this::afterCommonSetup);
    }

    // main thread
    void afterCommonSetup()
    {
        // add test biome to overworld
        BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(TEST_BIOME, 10));
        this.registerToVanillaRegistries();
    }

    // rundata doesn't fire common setup event -- do stuff here if it's needed in both
    void registerToVanillaRegistries()
    {
        // register a feature the old way to make sure BiomeLoadingEvent still works
        blackWoolFeature = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_FEATURE, BLACK_WOOL_CONFIGUREDFEATURE.location(),
            SINGLE_BLOCK.get().configured(new BlockStateFeatureConfig(Blocks.BLACK_WOOL.defaultBlockState()))
            .decorated(Placements.HEIGHTMAP)
            .decorated(LIMITED_SQUARE.get().configured(NoPlacementConfig.INSTANCE)));
        
        // structures need to have names registered to this map
        Structure.STRUCTURES_REGISTRY.put(GOLD_TOWER.getId().toString(), GOLD_TOWER.get());
    }

    void onGatherData(GatherDataEvent event)
    {
        this.registerToVanillaRegistries();

        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        // generate our configuredfeature json to retrieve from our dynamic registries loaded event
        Map<ResourceLocation, ConfiguredFeature<?, ?>> generatedFeatures = new HashMap<>();
        generatedFeatures.put(RED_WOOL_CONFIGUREDFEATURE.location(),
            SINGLE_BLOCK.get().configured(new BlockStateFeatureConfig(Blocks.RED_WOOL.defaultBlockState()))
            .decorated(Placements.HEIGHTMAP)
            .decorated(LIMITED_SQUARE.get().configured(NoPlacementConfig.INSTANCE)));
        generator
            .addProvider(new JsonDataProvider<>(gson, generator, ResourcePackType.SERVER_DATA, "worldgen/configured_feature", ConfiguredFeature.DIRECT_CODEC, generatedFeatures));

        // generate our test biome
        Map<ResourceLocation, Biome> generatedBiomes = new HashMap<>();
        generatedBiomes.put(TEST_BIOME.location(),
            new Biome.Builder()
                .specialEffects(new BiomeAmbience.Builder()
                    .fogColor(0xFF0000)
                    .skyColor(0xFF0000)
                    .waterColor(0xFF0000)
                    .waterFogColor(0xFF0000)
                    .grassColorOverride(0xFF0000)
                    .foliageColorOverride(0xFF0000).build())
                .depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F).biomeCategory(Biome.Category.PLAINS)
                .generationSettings(new BiomeGenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS).build()).precipitation(RainType.NONE)
                .mobSpawnSettings(new MobSpawnInfo.Builder().build()).build());
        generator.addProvider(new JsonDataProvider<>(gson, generator, ResourcePackType.SERVER_DATA, "worldgen/biome", Biome.DIRECT_CODEC, generatedBiomes));
                
        // generate template pools
        Map<ResourceLocation, JigsawPattern> generatedPools = new HashMap<>();
        generatedPools.put(GOLD_TOWER_STRUCTURE_FEATURE.location(), new JigsawPattern(GOLD_TOWER_STRUCTURE_FEATURE.location(), new ResourceLocation("empty"),
            ImmutableList.of(Pair.of(JigsawPiece.single(GOLD_TOWER.getId().toString()), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        generator.addProvider(new JsonDataProvider<>(gson, generator, ResourcePackType.SERVER_DATA, "worldgen/template_pool", JigsawPattern.DIRECT_CODEC, generatedPools));
        
        // generate structure features
        Map<ResourceLocation, StructureFeature<?,?>> generatedStructureFeatures = new HashMap<>();
        generatedStructureFeatures.put(GOLD_TOWER.getId(),
            new StructureFeature<>(GOLD_TOWER.get(), new LoadableJigsawConfig(GOLD_TOWER_STRUCTURE_FEATURE.location(), 1, 0, true)));
        generator.addProvider(new JsonDataProvider<>(gson, generator, ResourcePackType.SERVER_DATA, "worldgen/configured_structure_feature", StructureFeature.DIRECT_CODEC, generatedStructureFeatures));
        
        // generate biome tags
        generator.addProvider(new ResourceKeyTagsProvider<Biome>(generator, fileHelper, MODID, Registry.BIOME_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(OPTIONAL_BIOMES_TEST_TAG).addOptionalTags(new ResourceLocation("jumbo_biomes:jumbo_desert")).replace();
                this.tag(EXTRA_BIOMES_TAG).add(TEST_BIOME);
                this.tag(TESTING_BIOMES_TAG).add(Biomes.MOUNTAINS, Biomes.SNOWY_TUNDRA).addTags(EXTRA_BIOMES_TAG);
            }
        });
    }

    void onBiomeLoading(BiomeLoadingEvent event)
    {
        // test the BiomeLoadingEvent to ensure it still works
        if (TESTING_BIOMES.contains(RegistryKey.create(Registry.BIOME_REGISTRY, event.getName())))
        {
            // replace sky, fog, water color, leave everything else as-is, also add the black wool feature we registered to WorldGenRegistries earlier
            event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, blackWoolFeature);
            BiomeAmbience oldEffects = event.getEffects();
            BiomeAmbienceBuilder newEffects = BiomeAmbienceBuilder.copyFrom(oldEffects);
            newEffects.fogColor(0);
            newEffects.skyColor(0);
            newEffects.waterFogColor(0);
            newEffects.waterColor(0);
            event.setEffects(newEffects.build());
        }
    }
    
    void onDynamicRegistriesLoaded(DynamicRegistriesLoadedEvent event)
    {
        DynamicRegistries registries = event.getDataRegistries();
        Map<RegistryKey<Biome>,IBiomeParameters> biomeModifiers = event.getBiomeModifiers();
        
        // registryOrThrow is reasonably safe to call for vanilla's registries as vanilla uses it everywhere too
        // (i.e. if somebody removes the biome registry for some reason then vanilla will crash first anyway)
        Registry<ConfiguredFeature<?,?>> featureRegistry = registries.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
        Registry<StructureFeature<?,?>> structureFeatureRegistry = registries.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        
        // for each biome in this tag we have, modify it and add a json feature to it
        TESTING_BIOMES_TAG.getValues().forEach(biomeKey -> {
            // set biome sky color to green, add wool feature
            IBiomeParameters biomeParameters = biomeModifiers.get(biomeKey);
            if (biomeParameters == null)
                return;
            biomeParameters.getEffectsBuilder().skyColor(0x00FF00);
            BiomeGenerationSettingsBuilder builder = biomeParameters.getGenerationBuilder();
            builder.addFeature(Decoration.VEGETAL_DECORATION, featureRegistry.get(RED_WOOL_CONFIGUREDFEATURE));
            // let's add the gold tower structures too
            builder.addStructureStart(structureFeatureRegistry.get(GOLD_TOWER_STRUCTURE_FEATURE));
        });
        
        // then let's add the gold tower structure to the overworld noise settings
        Map<RegistryKey<DimensionSettings>, Map<Structure<?>, StructureSeparationSettings>> structureConfigs = event.getStructureSeparations();
        Tags.NoiseSettings.OVERWORLD.getValues().forEach(noiseKey ->
        {
            Map<Structure<?>, StructureSeparationSettings> structureConfig = structureConfigs.get(noiseKey);
            if (structureConfig == null)
                return;
            // we previously put the seperation setting in the structure instance
            // we could also theoretically define it in a data loader or a config
            structureConfig.put(GOLD_TOWER.get(), GOLD_TOWER.get().getSeparation());
        });
    }

    /**
     * Worldgen feature type that places a single given blockstate at a given
     * position The closest things to this in vanilla have requirements on where the
     * block can be placed, which we'd like to ignore so we can be guaranteed to see
     * the block
     */
    public static class SingleBlockFeature extends Feature<BlockStateFeatureConfig>
    {

        public SingleBlockFeature(Codec<BlockStateFeatureConfig> codec)
        {
            super(codec);
        }

        @Override
        public boolean place(ISeedReader world, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, BlockStateFeatureConfig config)
        {
            world.setBlock(pos, config.state, 2);
            return true;
        }
    }

    /**
     * Feature decorator type that randomly translates a block's x and z coordinates
     * by positive values in the range [4,11] (if transforming a location in the
     * corner of a chunk, it moves it to its xz plane's 8x8 region in the center of
     * its chunk) This makes it clear which chunk the feature's in (so we can test
     * to make sure features aren't being added to biomes twice)
     */
    public static class LimitedSquarePlacement extends SimplePlacement<NoPlacementConfig>
    {
        public LimitedSquarePlacement(Codec<NoPlacementConfig> codec)
        {
            super(codec);
        }

        @Override
        protected Stream<BlockPos> place(Random rand, NoPlacementConfig config, BlockPos pos)
        {
            int x = pos.getX() + 4 + rand.nextInt(8);
            int y = pos.getY();
            int z = pos.getZ() + 4 + rand.nextInt(8);
            return Stream.of(new BlockPos(x, y, z));
        }
    }
    
    private static class LoadableJigsawStructure extends Structure<LoadableJigsawConfig>
    {
        private final StructureSeparationSettings separation; public StructureSeparationSettings getSeparation() { return this.separation;}
        public LoadableJigsawStructure(Codec<LoadableJigsawConfig> codec, StructureSeparationSettings separation)
        {
            super(codec);
            this.separation = separation;
        }

        @Override
        public IStartFactory<LoadableJigsawConfig> getStartFactory()
        {
            return Start::new;
        }
        
        // either this needs to be overridden or your structure type instance needs to be added to the generation stage map in Structure
        // if you don't do either then biomes crash when constructed
        @Override
        public GenerationStage.Decoration step()
        {
            return GenerationStage.Decoration.SURFACE_STRUCTURES;
        }
        
        private static class Start extends StructureStart<LoadableJigsawConfig>
        {

            public Start(Structure<LoadableJigsawConfig> structure, int chunkX, int chunkZ, MutableBoundingBox mutabox, int refCount, long seed)
            {
                super(structure, chunkX, chunkZ, mutabox, refCount, seed);
            }

            @Override
            public void generatePieces(DynamicRegistries dynreg, ChunkGenerator generator, TemplateManager templates, int chunkX, int chunkZ, Biome biome, LoadableJigsawConfig config)
            {
                BlockPos startPos = new BlockPos(chunkX*16, config.getStartY(), chunkZ*16);
                JigsawPattern startPool = Objects.requireNonNull(dynreg.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(config.getStartPool()));
                VillageConfig jigsawConfig = new VillageConfig(() -> startPool, config.getSize());
                IPieceFactory pieceFactory = AbstractVillagePiece::new;
                JigsawManager.addPieces(dynreg, jigsawConfig, pieceFactory, generator, templates, startPos, this.pieces, this.random, false, config.snapToHeightMap);
                this.calculateBoundingBox();
            }
            
        }
        
    }
    
    private static class LoadableJigsawConfig implements IFeatureConfig
    {
        public static final Codec<LoadableJigsawConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("start_pool").forGetter(LoadableJigsawConfig::getStartPool),
                Codec.INT.fieldOf("size").forGetter(LoadableJigsawConfig::getSize),
                Codec.INT.optionalFieldOf("start_y",0).forGetter(LoadableJigsawConfig::getStartY),
                Codec.BOOL.optionalFieldOf("snap_to_height_map", true).forGetter(LoadableJigsawConfig::getSnapToHeightMap)
            ).apply(instance, LoadableJigsawConfig::new));
        
        private final ResourceLocation startPool; public ResourceLocation getStartPool() {return this.startPool;}
        private final int size; public int getSize() { return this.size;}
        private final int startY; public int getStartY() {return this.startY;}
        private final boolean snapToHeightMap; public boolean getSnapToHeightMap() {return this.snapToHeightMap;}

        public LoadableJigsawConfig(ResourceLocation startPool, int size, int startY, boolean snapToHeightMap)
        {
            this.startPool = startPool;
            this.size = size;
            this.startY = startY;
            this.snapToHeightMap = snapToHeightMap;
        }
    }
}
