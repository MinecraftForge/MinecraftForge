/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddSpawnsBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.RemoveFeaturesBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.RemoveSpawnsBiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;
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
 * spawn magma cubes, have red-colored water, and be snowy. Additionally, biomes in the is_forest tag are missing
 * oak trees, pine trees, and skeletons.</p>
 */
@Mod(BiomeModifierTest.MODID)
public class BiomeModifierTest
{
    public static final String MODID = "biome_modifiers_test";
    private static final boolean ENABLED = true;

    private static final String LARGE_BASALT_COLUMNS = "large_basalt_columns";
    private static final ResourceLocation LARGE_BASALT_COLUMNS_RL = new ResourceLocation(MODID, LARGE_BASALT_COLUMNS);
    private static final ResourceKey<PlacedFeature> LARGE_BASALT_COLUMNS_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, LARGE_BASALT_COLUMNS_RL);

    private static final String MODIFY_BIOMES = "modify_biomes";
    private static final ResourceLocation MODIFY_BIOMES_RL = new ResourceLocation(MODID, MODIFY_BIOMES);

    private static final String ADD_BASALT = "add_basalt";
    private static final ResourceLocation ADD_BASALT_RL = new ResourceLocation(MODID, ADD_BASALT);

    private static final String ADD_MAGMA_CUBES = "add_magma_cubes";
    private static final ResourceLocation ADD_MAGMA_CUBES_RL = new ResourceLocation(MODID, ADD_MAGMA_CUBES);

    private static final String MODIFY_BADLANDS = "modify_badlands";
    private static final ResourceLocation MODIFY_BADLANDS_RL = new ResourceLocation(MODID, MODIFY_BADLANDS);

    private static final String REMOVE_FOREST_TREES = "remove_forest_trees";
    private static final ResourceLocation REMOVE_FOREST_TREES_RL = new ResourceLocation(MODID, REMOVE_FOREST_TREES);

    private static final String REMOVE_FOREST_SKELETONS = "remove_forest_skeletons";
    private static final ResourceLocation REMOVE_FOREST_SKELETONS_RL = new ResourceLocation(MODID, REMOVE_FOREST_SKELETONS);

    public BiomeModifierTest()
    {
        if (!ENABLED)
            return;

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Serializer types can be registered via deferred register.
        final DeferredRegister<Codec<? extends BiomeModifier>> serializers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);
        serializers.register(modBus);
        serializers.register(MODIFY_BIOMES, TestModifier::makeCodec);

        modBus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event)
    {
        // Example of how to datagen datapack registry objects.
        final DataGenerator generator = event.getGenerator();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());

        // Create our placed feature and biome modifiers.
        final ResourceKey<ConfiguredFeature<?,?>> configuredFeatureKey = NetherFeatures.LARGE_BASALT_COLUMNS.unwrapKey().get().cast(Registry.CONFIGURED_FEATURE_REGISTRY).get();
        // Make sure we're using the holder from the registryaccess/registryops, the static ones won't work.
        final Holder<ConfiguredFeature<?,?>> configuredFeatureHolder = ops.registry(Registry.CONFIGURED_FEATURE_REGISTRY).get().getOrCreateHolderOrThrow(configuredFeatureKey);
        final PlacedFeature basaltFeature = new PlacedFeature(
            configuredFeatureHolder,
            List.of(CountOnEveryLayerPlacement.of(1), BiomeFilter.biome()));

        final HolderSet.Named<Biome> badlandsTag = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_BADLANDS);
        final HolderSet.Named<Biome> forestsTag = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_FOREST);

        final BiomeModifier addBasaltFeature = new AddFeaturesBiomeModifier(
            badlandsTag,
            HolderSet.direct(ops.registry(Registry.PLACED_FEATURE_REGISTRY).get().getOrCreateHolderOrThrow(ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, LARGE_BASALT_COLUMNS_KEY.location()))),
            Decoration.TOP_LAYER_MODIFICATION);

        final BiomeModifier addSpawn = AddSpawnsBiomeModifier.singleSpawn(
            badlandsTag,
            new SpawnerData(EntityType.MAGMA_CUBE, 100, 1, 4));

        final BiomeModifier biomeModifier = new TestModifier(
            new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_BADLANDS),
            Precipitation.SNOW,
            0xFF0000
            );

        final BiomeModifier removeFeature = RemoveFeaturesBiomeModifier.allSteps(
            forestsTag,
            HolderSet.direct(ops.registry(Registry.PLACED_FEATURE_REGISTRY).get().getOrCreateHolderOrThrow(ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation("trees_birch_and_oak"))))
            );

        final BiomeModifier removeSpawn = new RemoveSpawnsBiomeModifier(
            forestsTag,
            new HolderSet.Named<>(ops.registry(Registry.ENTITY_TYPE_REGISTRY).get(), EntityTypeTags.SKELETONS)
            );

        // Create and add dataproviders.
        generator.addProvider(event.includeServer(), JsonCodecProvider.forDatapackRegistry(
            generator, existingFileHelper, MODID, ops, Registry.PLACED_FEATURE_REGISTRY, Map.of(
                LARGE_BASALT_COLUMNS_RL, basaltFeature)));

        generator.addProvider(event.includeServer(), JsonCodecProvider.forDatapackRegistry(
            generator, existingFileHelper, MODID, ops, ForgeRegistries.Keys.BIOME_MODIFIERS, Map.of(
                MODIFY_BADLANDS_RL, biomeModifier,
                ADD_BASALT_RL, addBasaltFeature,
                ADD_MAGMA_CUBES_RL, addSpawn,
                REMOVE_FOREST_TREES_RL, removeFeature,
                REMOVE_FOREST_SKELETONS_RL, removeSpawn)));
    }

    public record TestModifier(HolderSet<Biome> biomes, Precipitation precipitation, int waterColor) implements BiomeModifier
    {
        private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(MODIFY_BIOMES_RL, ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

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
