/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.AddFeaturesBiomeModifier;
import net.minecraftforge.common.world.AddSpawnBiomeModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * <p>This tests the following features and requirements of biome modifier jsons::</p>
 * <ul>
 * <li>Biome modifier jsons are created via datagen.</li>
 * <li>Biome modifiers modify all four modifiable fields in biomes, to ensure patches and coremods apply correctly (generation, spawns, climate, and client effects).</li>
 * <li>Biome modifiers use biome tags to determine which biomes to modify.</li>
 * <li>Biome modifiers add a json feature to modified biomes, to ensure json features are usable in biome modifiers.</li>
 * </ul>
 * <p>If the biome modifiers are applied correctly, then badlands biomes should generate large basalt columns,
 * spawn magma cubes, have red-colored water, and be snowy.</p>
 */
@Mod(BiomeModifierTest.MODID)
public class BiomeModifierTest
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "biome_modifiers_test";
    public static final boolean ENABLED = true;
    public static final String BASALT_PILLARS = "large_basalt_columns";
    public static final ResourceLocation BASALT_PILLARS_RL = new ResourceLocation(MODID, BASALT_PILLARS);
    public static final String TEST = "test";
    public static final ResourceLocation ADD_FEATURES_TO_BIOMES_RL = new ResourceLocation(MODID, TEST);
    public static final String ADD_BASALT = "add_basalt";
    public static final String ADD_MAGMA_CUBES = "add_magma_cubes";
    public static final String MODIFY_BADLANDS = "modify_badlands";

    public BiomeModifierTest()
    {
        if (!ENABLED)
            return;

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Serializer types can be registered via deferred register.
        final DeferredRegister<Codec<? extends BiomeModifier>> serializers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);
        serializers.register(modBus);
        serializers.register(TEST, TestModifier::makeCodec);

        modBus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event)
    {
        // Example of how to datagen datapack registry objects.
        DataGenerator generator = event.getGenerator();
        final Path outputFolder = generator.getOutputFolder();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
        final String directory = PackType.SERVER_DATA.getDirectory();

        // Prepare to datagenerate our placed feature.
        final String featurePathString = String.join("/", directory, MODID, Registry.PLACED_FEATURE_REGISTRY.location().getPath(), BASALT_PILLARS + ".json");
        final Path featurePath = outputFolder.resolve(featurePathString);
        // Both casts here are needed for eclipse to compile.
        final ResourceKey<ConfiguredFeature<?,?>> configuredFeatureKey = (ResourceKey<ConfiguredFeature<?,?>>)(ResourceKey<? extends ConfiguredFeature<?,?>>)NetherFeatures.LARGE_BASALT_COLUMNS.unwrapKey().get();
        // Make sure we're using the holder from the registryaccess/registryops, the static ones won't work.
        final Holder<ConfiguredFeature<?,?>> configuredFeatureHolder = ops.registry(Registry.CONFIGURED_FEATURE_REGISTRY).get().getOrCreateHolderOrThrow(configuredFeatureKey); 
        final PlacedFeature feature = new PlacedFeature(
            configuredFeatureHolder,
            List.of(CountOnEveryLayerPlacement.of(1), BiomeFilter.biome()));
        
        final ResourceLocation biomeModifiersRegistryID = ForgeRegistries.Keys.BIOME_MODIFIERS.location();
        final String biomeModifiersNamespace = biomeModifiersRegistryID.getNamespace();
        final String biomeModifiersPath = biomeModifiersRegistryID.getPath();
        final HolderSet.Named<Biome> badlandsTag = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_BADLANDS);
        
        // Prepare to datagenerate our add-feature biome modifier.
        final String addFeaturePathString = String.join("/", directory, MODID, biomeModifiersNamespace, biomeModifiersPath, ADD_BASALT + ".json");
        final Path addFeaturePath = outputFolder.resolve(addFeaturePathString);
        final BiomeModifier addFeature = new AddFeaturesBiomeModifier(
            badlandsTag,
            HolderSet.direct(ops.registry(Registry.PLACED_FEATURE_REGISTRY).get().getOrCreateHolderOrThrow(ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, BASALT_PILLARS_RL))),
            Decoration.TOP_LAYER_MODIFICATION);
        
        // Prepare to datagenerate our add-spawn biome modifier.
        final String addSpawnPathString = String.join("/", directory, MODID, biomeModifiersNamespace, biomeModifiersPath, ADD_MAGMA_CUBES + ".json");
        final Path addSpawnPath = outputFolder.resolve(addSpawnPathString);
        final BiomeModifier addSpawn = new AddSpawnBiomeModifier(
            badlandsTag,
            new SpawnerData(EntityType.MAGMA_CUBE, 100, 1, 4));

        // Prepare to datagenerate our climate and render effects biome modifier.
        final String biomeModifierPathString = String.join("/", directory, MODID, biomeModifiersRegistryID.getNamespace(), biomeModifiersRegistryID.getPath(), MODIFY_BADLANDS + ".json");
        final Path biomeModifierPath = outputFolder.resolve(biomeModifierPathString);
        final BiomeModifier biomeModifier = new TestModifier(
            new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_BADLANDS),
            Precipitation.SNOW,
            0xFF0000
            );

        generator.addProvider(event.includeServer(), new DataProvider()
        {
            @Override
            public void run(final CachedOutput cache) throws IOException
            {
                PlacedFeature.DIRECT_CODEC.encodeStart(ops, feature)
                    .resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", featurePathString, msg)) // Log error on encode failure.
                    .ifPresent(json -> // Output to file on encode success.
                    {
                        try
                        {
                            DataProvider.saveStable(cache, json, featurePath);
                        }
                        catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
                        {
                            LOGGER.error("Failed to save " + featurePathString, e);
                        }
                    });

                BiomeModifier.DIRECT_CODEC.encodeStart(ops, addFeature)
                    .resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", addFeaturePathString, msg)) // Log error on encode failure.
                    .ifPresent(json -> // Output to file on encode success.
                    {
                        try
                        {
                            DataProvider.saveStable(cache, json, addFeaturePath);
                        }
                        catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
                        {
                            LOGGER.error("Failed to save " + addFeaturePathString, e);
                        }
                    });

                BiomeModifier.DIRECT_CODEC.encodeStart(ops, addSpawn)
                    .resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", addSpawnPathString, msg)) // Log error on encode failure.
                    .ifPresent(json -> // Output to file on encode success.
                    {
                        try
                        {
                            DataProvider.saveStable(cache, json, addSpawnPath);
                        }
                        catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
                        {
                            LOGGER.error("Failed to save " + addSpawnPathString, e);
                        }
                    });

                BiomeModifier.DIRECT_CODEC.encodeStart(ops, biomeModifier)
                    .resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", biomeModifierPathString, msg)) // Log error on encode failure.
                    .ifPresent(json -> // Output to file on encode success.
                    {
                        try
                        {
                            DataProvider.saveStable(cache, json, biomeModifierPath);
                        }
                        catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
                        {
                            LOGGER.error("Failed to save " + biomeModifierPathString, e);
                        }
                    });
            }

            @Override
            public String getName()
            {
                return MODID + " data provider";
            }
        });
    }

    public record TestModifier(HolderSet<Biome> biomes, Precipitation precipitation, int waterColor) implements BiomeModifier
    {
        private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(ADD_FEATURES_TO_BIOMES_RL, ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

        @Override
        public void modify(Holder<Biome> biome, Phase phase, Builder builder)
        {
            if (phase == Phase.MODIFY && this.biomes.contains(biome))
            {
                builder.getClimateSettings().setPrecipitation(this.precipitation);
                builder.getEffects().waterColor(this.waterColor);
                if (this.precipitation == Precipitation.SNOW)
                    builder.getClimateSettings().setTemperature(0F);
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec()
        {
            return SERIALIZER.get();
        }

        private static Codec<TestModifier> makeCodec()
        {
            return RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(TestModifier::biomes),
                    Precipitation.CODEC.fieldOf("precipitation").forGetter(TestModifier::precipitation),
                    Codec.INT.fieldOf("water_color").forGetter(TestModifier::waterColor)
            ).apply(builder, TestModifier::new));
        }
    }
}
