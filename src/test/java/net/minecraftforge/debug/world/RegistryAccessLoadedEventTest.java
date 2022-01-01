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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.RegistryHolder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeBuilder;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeSpecialEffectsBuilder;
import net.minecraftforge.common.world.IBiomeParameters;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.RegistryAccessLoadedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(RegistryAccessLoadedEventTest.MODID)
public class RegistryAccessLoadedEventTest
{
    public static final String MODID = "registry_access_loaded_event_test";
    
    public static final ResourceKey<Biome> WINDSWEPT_HILLS = Biomes.WINDSWEPT_HILLS;
    public static final ResourceKey<Biome> SNOWY_PLAINS = Biomes.SNOWY_PLAINS;
    public static final ResourceKey<Biome> WINDSWEPT_FOREST = Biomes.WINDSWEPT_FOREST;
    public static final Set<ResourceKey<Biome>> TESTING_BIOMES = ImmutableSet.of(WINDSWEPT_HILLS, SNOWY_PLAINS, WINDSWEPT_FOREST);
    
    public static final ResourceKey<PlacedFeature> RED_WOOL_PLACED_FEATURE = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(MODID, "red_wool"));
    public static final ResourceKey<PlacedFeature> BLACK_WOOL_PLACED_FEATURE = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(MODID, "block_wool"));

    public static final ResourceKey<ConfiguredFeature<?,?>> RED_WOOL_CONFIGURED_FEATURE = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, RED_WOOL_PLACED_FEATURE.location());
    
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final RegistryObject<SingleBlockFeature> SINGLE_BLOCK = FEATURES.register("single_block", () -> new SingleBlockFeature(BlockStateConfiguration.CODEC));

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
    static
    {
        BIOMES.register("test_biome", () -> OverworldBiomes.theVoid());
    } // register dummy biome, json will override it

    // BiomeLoadingEvent configuredfeatures must be created and registered in java -- we'll do one of those to make sure BiomeLoadingEvent still works
    public static ConfiguredFeature<?, ?> configuredBlackWoolFeature;
    public static PlacedFeature placedBlackWoolFeature;
    // RegistryAccessLoadedEvent can retrieve configuredfeatures from dynamic registries, we will register a separate feature by having a json for it
    
    // some other vanilla-registry objects
    public static PlacementModifierType<LimitedSquarePlacement> limitedSquarePlacement;

    public RegistryAccessLoadedEventTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // register deferred registers
        FEATURES.register(modBus);
        BIOMES.register(modBus);

        // subscribe events
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onGatherData);

        forgeBus.addListener(this::onBiomeLoading);
        forgeBus.addListener(this::onRegistryAccessLoaded);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(this::afterCommonSetup);
    }

    // main thread
    private void afterCommonSetup()
    {
        this.registerToVanillaRegistries();
    }

    // rundata doesn't fire common setup event -- do stuff here if it's needed in both
    private void registerToVanillaRegistries()
    {
        limitedSquarePlacement = Registry.register(Registry.PLACEMENT_MODIFIERS, LimitedSquarePlacement.ID, () ->LimitedSquarePlacement.CODEC);
        // register a feature the old way to make sure BiomeLoadingEvent still works
        configuredBlackWoolFeature = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, BLACK_WOOL_PLACED_FEATURE.location(),
            SINGLE_BLOCK.get().configured(new BlockStateConfiguration(Blocks.BLACK_WOOL.defaultBlockState())));
        placedBlackWoolFeature = BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, BLACK_WOOL_PLACED_FEATURE.location(),
            configuredBlackWoolFeature.placed(
                    LimitedSquarePlacement.INSTANCE,
                    PlacementUtils.HEIGHTMAP));
    }

    private void onGatherData(GatherDataEvent event)
    {
        if (event.includeServer())
        {
            this.onGatherServerData(event);
        }
    }
    
    private void onGatherServerData(GatherDataEvent event)
    {
        this.registerToVanillaRegistries(); // common setup doesn't run during datagen, ensure anything we registered there is registered

        DataGenerator generator = event.getGenerator();
        Path resourcesFolder = generator.getOutputFolder();
        
        // create biome and feature objects to datagen
        // we do want to test this on a json biome
        ConfiguredFeature<BlockStateConfiguration, ?> redWoolFeature = SINGLE_BLOCK.get().configured(new BlockStateConfiguration(Blocks.RED_WOOL.defaultBlockState()));
        
        // then we need to register that configuredfeature to the builtin registries before we make the write ops
        // (so the placed feature json can reference it)
        BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, RED_WOOL_PLACED_FEATURE.location(), redWoolFeature);
        
        // create the RegistryWriteOps, we can use this to create worldgen jsons
        RegistryHolder registries = RegistryAccess.builtin();
        RegistryWriteOps<JsonElement> writeOps = RegistryWriteOps.create(JsonOps.INSTANCE, registries);
        
        // adding new biomes to the overworld is tricky right now, so we'll just override an existing biome
        // setting the sky color to something weird will let us know we're in our json biome
        // copying features from the biome from the registryaccess will ensure we get a proper json instead of inline feature definitions
        BiomeBuilder windsweptForestOverrideBuilder = BiomeBuilder.copyFrom(registries.registryOrThrow(Registry.BIOME_REGISTRY).get(Biomes.WINDSWEPT_FOREST));
            windsweptForestOverrideBuilder.getEffectsBuilder()
                .skyColor(16711680)
                .foliageColorOverride(16711680)
                .grassColorOverride(16711680)
                .fogColor(16711680)
                .waterColor(16711680)
                .waterFogColor(16711680);
        Biome windwsweptForestOverride = windsweptForestOverrideBuilder.build();
        
        // also make sure we create our placed feature json using a json configuredfeature reference via the registryaccess
        PlacedFeature placedRedWoolFeature = registries.registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY)
            .get(RED_WOOL_CONFIGURED_FEATURE)
            .placed(Lists.newArrayList(LimitedSquarePlacement.INSTANCE, PlacementUtils.HEIGHTMAP));
        
        // then write the jsons
        DataProvider provider = new DataProvider()
        {
            @Override
            public void run(HashCache cache) throws IOException
            {
                Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
                makeWorldgenDataWriter(cache, writeOps, resourcesFolder, gson, Biome.CODEC, "worldgen/biome")
                    .accept(Biomes.WINDSWEPT_FOREST.location(), ()->windwsweptForestOverride);
                makeWorldgenDataWriter(cache, writeOps, resourcesFolder, gson, ConfiguredFeature.CODEC, "worldgen/configured_feature")
                    .accept(RED_WOOL_PLACED_FEATURE.location(), ()->redWoolFeature);
                makeWorldgenDataWriter(cache, writeOps, resourcesFolder, gson, PlacedFeature.CODEC, "worldgen/placed_feature")
                    .accept(RED_WOOL_PLACED_FEATURE.location(), ()->placedRedWoolFeature);
            }

            @Override
            public String getName()
            {
                return MODID + " worldgen data provider";
            }
        };
        generator.addProvider(provider);
    }

    private void onBiomeLoading(BiomeLoadingEvent event)
    {
        // test the BiomeLoadingEvent to ensure it still works when the RegistryAccessLoadedEvent is also being used
        // we'll modify the windswept hills, changing the sky color to black and adding a black wool feature
        // the RegistryAccessLoadedEvent should override the sky color to green and add a red wool feature (keeping the black wool feature)
        if (WINDSWEPT_HILLS.location().equals(event.getName()))
        {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placedBlackWoolFeature);
            BiomeSpecialEffects oldEffects = event.getEffects();
            BiomeSpecialEffectsBuilder newEffects = BiomeSpecialEffectsBuilder.copyFrom(oldEffects);
            newEffects.fogColor(0);
            newEffects.skyColor(0);
            newEffects.waterFogColor(0);
            newEffects.waterColor(0);
            event.setEffects(newEffects.build());
        }
    }
    
    private void onRegistryAccessLoaded(RegistryAccessLoadedEvent event)
    {
        RegistryAccess registries = event.getRegistryAccess();
        Map<ResourceKey<Biome>,IBiomeParameters> biomeModifiers = event.getBiomeModifiers();
        
        // registryOrThrow is reasonably safe to call for vanilla's registries as vanilla uses it everywhere too
        // (i.e. if somebody removes the biome registry for some reason then vanilla will crash first anyway)
        Registry<PlacedFeature> featureRegistry = registries.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        
        // for each biome we are testing, modify it and add a json feature to it
        // we have three biomes we should test
        // snowy plains -- unmodified by BiomeLoadingEvent or datapack override prior to this event
        // windswept hills -- modified by BLE to have black sky and add a black wool feature
        // windswept forest -- overidden by json, sky is red
        // for each of these biomes, we will use this event to change the sky to green and add a red wool feature
        // windswept hills should retain its black wool feature as well
        TESTING_BIOMES.forEach(biomeKey -> {
            // set biome sky color to green, add wool feature
            IBiomeParameters biomeParameters = biomeModifiers.get(biomeKey);
            if (biomeParameters == null)
                return;
            biomeParameters.getEffectsBuilder().skyColor(0x00FF00);
            BiomeGenerationSettingsBuilder builder = biomeParameters.getGenerationBuilder();
            builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, featureRegistry.get(RED_WOOL_PLACED_FEATURE));
        });
    }
    
    private static <T> BiConsumer<ResourceLocation,T> makeWorldgenDataWriter(HashCache cache, RegistryWriteOps<JsonElement> writeOps, Path resourcesFolder, Gson gson, Codec<T> codec, String dataTypeFolder)
    {
        return (id, obj) ->
        {
            JsonElement json = codec.encodeStart(writeOps, obj).get().orThrow();
            Path path = resourcesFolder.resolve(String.join("/", PackType.SERVER_DATA.getDirectory(), id.getNamespace(), dataTypeFolder, id.getPath()) + ".json");
            try
            {
                DataProvider.save(gson, cache, json, path);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        };
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
    public static class LimitedSquarePlacement extends InSquarePlacement
    {
        public static final ResourceLocation ID = new ResourceLocation(MODID, "limited_square");
        public static final LimitedSquarePlacement INSTANCE = new LimitedSquarePlacement();
        public static final Codec<LimitedSquarePlacement> CODEC = Codec.unit(INSTANCE);

        @Override
        public Stream<BlockPos> getPositions(PlacementContext context, Random rand, BlockPos pos)
        {
            int x = pos.getX() + 4 + rand.nextInt(8);
            int y = pos.getY();
            int z = pos.getZ() + 4 + rand.nextInt(8);
            return Stream.of(new BlockPos(x, y, z));
        }

        @Override
        public PlacementModifierType<?> type()
        {
            return limitedSquarePlacement;
        }        
    }
}
