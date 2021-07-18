package net.minecraftforge.debug.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features.Placements;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.SimplePlacement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.JsonDataProvider;
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

    public static final RegistryKey<Biome> MOUNTAINS = Biomes.MOUNTAINS;
    public static final RegistryKey<Biome> SNOWY_TUNDRA = Biomes.SNOWY_TUNDRA;
    public static final RegistryKey<Biome> TEST_BIOME = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(MODID, "test_biome"));
    public static final Set<RegistryKey<Biome>> TESTING_BIOMES = ImmutableSet.of(MOUNTAINS, SNOWY_TUNDRA, TEST_BIOME);
    
    public static final RegistryKey<ConfiguredFeature<?,?>> RED_WOOL_CONFIGUREDFEATURE = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(MODID, "red_wool"));
    public static final RegistryKey<ConfiguredFeature<?,?>> BLACK_WOOL_CONFIGUREDFEATURE = RegistryKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(MODID, "block_wool"));

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final RegistryObject<SingleBlockFeature> SINGLE_BLOCK = FEATURES.register("single_block", () -> new SingleBlockFeature(BlockStateFeatureConfig.CODEC));

    public static final DeferredRegister<Placement<?>> PLACEMENTS = DeferredRegister.create(ForgeRegistries.DECORATORS, MODID);
    public static final RegistryObject<LimitedSquarePlacement> LIMITED_SQUARE = PLACEMENTS.register("limited_square", () -> new LimitedSquarePlacement(NoPlacementConfig.CODEC));

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
    static
    {
        BIOMES.register("test_biome", () -> BiomeMaker.theVoidBiome());
    } // register dummy biome, json will override it

    // configuredfeatures will need to be created after registry events, during
    // common setup
    // BiomeLoadingEvent configuredfeatures must be created and registered in java
    public static ConfiguredFeature<?, ?> blackWoolFeature;
    // BiomeLoadedEvent can retrieve configuredfeatures from dynamic registries, we
    // will declare a json

    public DynamicRegistriesLoadedEventTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // register deferred registers
        FEATURES.register(modBus);
        PLACEMENTS.register(modBus);
        BIOMES.register(modBus);

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

    // rundata doesn't fire common setup event
    void registerToVanillaRegistries()
    {
        blackWoolFeature = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_FEATURE, BLACK_WOOL_CONFIGUREDFEATURE.location(),
            SINGLE_BLOCK.get().configured(new BlockStateFeatureConfig(Blocks.BLACK_WOOL.defaultBlockState()))
            .decorated(Placements.HEIGHTMAP)
            .decorated(LIMITED_SQUARE.get().configured(NoPlacementConfig.INSTANCE)));
    }

    void onGatherData(GatherDataEvent event)
    {
        this.registerToVanillaRegistries();

        DataGenerator generator = event.getGenerator();
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        // generate our configuredfeature json to retrieve from our biomeloadedevent
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
    }

    void onBiomeLoading(BiomeLoadingEvent event)
    {
        if (TESTING_BIOMES.contains(RegistryKey.create(Registry.BIOME_REGISTRY, event.getName())))
        {
            // replace sky, fog, water color, leave everything else as-is
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
        
        // we have a Set<RegistryKey<Biome>> defined here -- TODO implement biome tags
        TESTING_BIOMES.forEach(biomeKey -> {
            // set biome sky color to green, add wool feature 
            IBiomeParameters biomeParameters = biomeModifiers.get(biomeKey);
            if (biomeParameters == null)
                return;
            biomeParameters.getEffectsBuilder().skyColor(0x00FF00);
            BiomeGenerationSettingsBuilder builder = biomeParameters.getGenerationBuilder();
            builder.addFeature(Decoration.VEGETAL_DECORATION, featureRegistry.get(RED_WOOL_CONFIGUREDFEATURE));
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
}
