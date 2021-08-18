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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.data.worldgen.biome.VanillaBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement.PieceFactory;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.SquareDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ResourceKeyTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ResourceKeyTagsProvider;
import net.minecraftforge.common.world.BiomeSpecialEffectsBuilder;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.IBiomeParameters;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.DynamicRegistriesLoadedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(DynamicRegistriesLoadedEventTest.MODID)
public class DynamicRegistriesLoadedEventTest
{
    public static final String MODID = "dynamic_registries_loaded_event_test";

    public static final Tag.Named<ResourceKey<Biome>> OPTIONAL_BIOMES_TEST_TAG = ResourceKeyTags.makeKeyTagWrapper(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "optional_biomes_test"));
    public static final Tag.Named<ResourceKey<Biome>> EXTRA_BIOMES_TAG = ResourceKeyTags.makeKeyTagWrapper(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "extra_biomes"));
    public static final Tag.Named<ResourceKey<Biome>> TESTING_BIOMES_TAG = ResourceKeyTags.makeKeyTagWrapper(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "testing_biomes"));

    public static final ResourceKey<Biome> MOUNTAINS = Biomes.MOUNTAINS;
    public static final ResourceKey<Biome> SNOWY_TUNDRA = Biomes.SNOWY_TUNDRA;
    public static final ResourceKey<Biome> TEST_BIOME = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "test_biome"));
    public static final Set<ResourceKey<Biome>> TESTING_BIOMES = ImmutableSet.of(MOUNTAINS, SNOWY_TUNDRA, TEST_BIOME);
    
    public static final ResourceKey<ConfiguredFeature<?,?>> RED_WOOL_CONFIGUREDFEATURE = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(MODID, "red_wool"));
    public static final ResourceKey<ConfiguredFeature<?,?>> BLACK_WOOL_CONFIGUREDFEATURE = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(MODID, "block_wool"));
    public static final ResourceKey<ConfiguredStructureFeature<?,?>> GOLD_TOWER_STRUCTURE_FEATURE = ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(MODID, "gold_tower"));

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final RegistryObject<SingleBlockFeature> SINGLE_BLOCK = FEATURES.register("single_block", () -> new SingleBlockFeature(BlockStateConfiguration.CODEC));

    public static final DeferredRegister<FeatureDecorator<?>> PLACEMENTS = DeferredRegister.create(ForgeRegistries.DECORATORS, MODID);
    public static final RegistryObject<LimitedSquarePlacement> LIMITED_SQUARE = PLACEMENTS.register("limited_square", () -> new LimitedSquarePlacement(NoneDecoratorConfiguration.CODEC));
    
    public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, MODID);
    public static final RegistryObject<LoadableJigsawStructure> GOLD_TOWER = STRUCTURES.register("gold_tower", () -> new LoadableJigsawStructure(LoadableJigsawConfig.CODEC, new StructureFeatureConfiguration(4,2,-1757510426)));

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
    static
    {
        BIOMES.register("test_biome", () -> VanillaBiomes.theVoidBiome());
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
        blackWoolFeature = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, BLACK_WOOL_CONFIGUREDFEATURE.location(),
            SINGLE_BLOCK.get().configured(new BlockStateConfiguration(Blocks.BLACK_WOOL.defaultBlockState()))
            .decorated(Features.Decorators.HEIGHTMAP)
            .decorated(LIMITED_SQUARE.get().configured(NoneDecoratorConfiguration.INSTANCE)));
        
        // structures need to have names registered to this map
        StructureFeature.STRUCTURES_REGISTRY.put(GOLD_TOWER.getId().toString(), GOLD_TOWER.get());
    }

    void onGatherData(GatherDataEvent event)
    {
        this.registerToVanillaRegistries();

        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        
        // generate biome tags
        generator.addProvider(new ResourceKeyTagsProvider<Biome>(generator, fileHelper, MODID, Registry.BIOME_REGISTRY)
        {
            @Override
            public void addTags()
            {
                this.tag(OPTIONAL_BIOMES_TEST_TAG).addOptionalTags(new ResourceLocation("jumbo_biomes:jumbo_desert")).replace();
                this.tag(EXTRA_BIOMES_TAG).add(TEST_BIOME);
                this.tag(TESTING_BIOMES_TAG)
                    .add(Biomes.MOUNTAINS, Biomes.SNOWY_TUNDRA)
                    .addTags(EXTRA_BIOMES_TAG)
                    .remove(Biomes.DESERT)
                    .remove(Biomes.TAIGA.location())
                    .remove(Tags.Biomes.OCEANS);
            }
        });
    }

    void onBiomeLoading(BiomeLoadingEvent event)
    {
        // test the BiomeLoadingEvent to ensure it still works when the dynregloaded event is also being used
        if (TESTING_BIOMES.contains(ResourceKey.create(Registry.BIOME_REGISTRY, event.getName())))
        {
            // replace sky, fog, water color, leave everything else as-is, also add the black wool feature we registered to WorldGenRegistries earlier
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, blackWoolFeature);
            BiomeSpecialEffects oldEffects = event.getEffects();
            BiomeSpecialEffectsBuilder newEffects = BiomeSpecialEffectsBuilder.copyFrom(oldEffects);
            newEffects.fogColor(0);
            newEffects.skyColor(0);
            newEffects.waterFogColor(0);
            newEffects.waterColor(0);
            event.setEffects(newEffects.build());
        }
    }
    
    void onDynamicRegistriesLoaded(DynamicRegistriesLoadedEvent event)
    {
        RegistryAccess registries = event.getDataRegistries();
        Map<ResourceKey<Biome>,IBiomeParameters> biomeModifiers = event.getBiomeModifiers();
        
        // registryOrThrow is reasonably safe to call for vanilla's registries as vanilla uses it everywhere too
        // (i.e. if somebody removes the biome registry for some reason then vanilla will crash first anyway)
        Registry<ConfiguredFeature<?,?>> featureRegistry = registries.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
        Registry<ConfiguredStructureFeature<?,?>> structureFeatureRegistry = registries.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        
        // for each biome in this tag we have, modify it and add a json feature to it
        TESTING_BIOMES_TAG.getValues().forEach(biomeKey -> {
            // set biome sky color to green, add wool feature
            IBiomeParameters biomeParameters = biomeModifiers.get(biomeKey);
            if (biomeParameters == null)
                return;
            biomeParameters.getEffectsBuilder().skyColor(0x00FF00);
            BiomeGenerationSettingsBuilder builder = biomeParameters.getGenerationBuilder();
            builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, featureRegistry.get(RED_WOOL_CONFIGUREDFEATURE));
            // let's add the gold tower structures too
            builder.addStructureStart(structureFeatureRegistry.get(GOLD_TOWER_STRUCTURE_FEATURE));
        });
        
        // then let's add the gold tower structure to the overworld noise settings
        Map<ResourceKey<NoiseGeneratorSettings>, Map<StructureFeature<?>, StructureFeatureConfiguration>> structureConfigs = event.getStructureSeparations();
        Tags.NoiseSettings.OVERWORLD.getValues().forEach(noiseKey ->
        {
            Map<StructureFeature<?>, StructureFeatureConfiguration> structureConfig = structureConfigs.get(noiseKey);
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
    public static class SingleBlockFeature extends Feature<BlockStateConfiguration>
    {

        public SingleBlockFeature(Codec<BlockStateConfiguration> codec)
        {
            super(codec);
        }

        @Override
        
        public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
        {
            WorldGenLevel world = context.level();
            BlockPos pos = context.origin();
            BlockStateConfiguration config = context.config();
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
    public static class LimitedSquarePlacement extends SquareDecorator
    {
        public LimitedSquarePlacement(Codec<NoneDecoratorConfiguration> codec)
        {
            super(codec);
        }

        @Override
        public Stream<BlockPos> getPositions(DecorationContext context, Random rand, NoneDecoratorConfiguration config, BlockPos pos)
        {
            int x = pos.getX() + 4 + rand.nextInt(8);
            int y = pos.getY();
            int z = pos.getZ() + 4 + rand.nextInt(8);
            return Stream.of(new BlockPos(x, y, z));
        }
    }
    
    private static class LoadableJigsawStructure extends StructureFeature<LoadableJigsawConfig>
    {
        private final StructureFeatureConfiguration separation; public StructureFeatureConfiguration getSeparation() { return this.separation;}
        public LoadableJigsawStructure(Codec<LoadableJigsawConfig> codec, StructureFeatureConfiguration separation)
        {
            super(codec);
            this.separation = separation;
        }

        @Override
        public StructureStartFactory<LoadableJigsawConfig> getStartFactory()
        {
            return Start::new;
        }
        
        // either this needs to be overridden or your structure type instance needs to be added to the generation stage map in Structure
        // if you don't do either then biomes crash when constructed
        @Override
        public GenerationStep.Decoration step()
        {
            return GenerationStep.Decoration.SURFACE_STRUCTURES;
        }
        
        private static class Start extends StructureStart<LoadableJigsawConfig>
        {

            public Start(StructureFeature<LoadableJigsawConfig> structure, ChunkPos chunkPos, int refCount, long seed)
            {
                super(structure, chunkPos, refCount, seed);
            }

            @Override
            public void generatePieces(RegistryAccess dynreg, ChunkGenerator generator, StructureManager templates, ChunkPos chunkPos, Biome biome, LoadableJigsawConfig config, LevelHeightAccessor heightAccess)
            {
                int chunkX = chunkPos.x;
                int chunkZ = chunkPos.z;
                BlockPos startPos = new BlockPos(chunkX*16, config.getStartY(), chunkZ*16);
                StructureTemplatePool startPool = Objects.requireNonNull(dynreg.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(config.getStartPool()));
                JigsawConfiguration jigsawConfig = new JigsawConfiguration(() -> startPool, config.getSize());
                PieceFactory pieceFactory = PoolElementStructurePiece::new;
                JigsawPlacement.addPieces(dynreg, jigsawConfig, pieceFactory, generator, templates, startPos, this, this.random, false, config.snapToHeightMap, heightAccess);
                this.getBoundingBox();
            }
            
        }
        
    }
    
    private static class LoadableJigsawConfig implements FeatureConfiguration
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
