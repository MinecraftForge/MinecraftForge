/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
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
 * <li>A biome modifier json is created via datagen.</li>
 * <li>The biome modifier modifies all four modifiable fields in biomes, to ensure patches and coremods apply correctly (generation, spawns, climate, and client effects).</li>
 * <li>The biome modifier uses a biome tag to determine which biomes to modify.</li>
 * <li>The biome modifier adds json feature to modified biomes, to ensure json features are usable in biome modifiers.</li>
 * </ul>
 * <p>If the biome modifier is applied correctly, then badlands biomes should generate large basalt columns,
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
    public static final ResourceKey<PlacedFeature> BASALT_PILLARS_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, BASALT_PILLARS_RL);
    public static final String TEST = "test";
    public static final ResourceLocation ADD_FEATURES_TO_BIOMES_RL = new ResourceLocation(MODID, TEST);
    public static final String MODIFY_BADLANDS = "modify_badlands";
    public static final ResourceLocation MODIFY_BADLANDS_RL = new ResourceLocation(MODID, MODIFY_BADLANDS);
    public static final ResourceKey<BiomeModifier> MODIFY_BADLANDS_KEY = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, MODIFY_BADLANDS_RL);

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
        final DataGenerator generator = event.getGenerator();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        
        // Any reference holders our objects have must come from this same RegistryAccess instance,
        // or encoding our objects will fail.
        final RegistryAccess registries = RegistryAccess.builtinCopy();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        final Registry<ConfiguredFeature<?,?>> configuredFeatures = ops.registry(Registry.CONFIGURED_FEATURE_REGISTRY).get();
        final Registry<PlacedFeature> placedFeatures = ops.registry(Registry.PLACED_FEATURE_REGISTRY).get();

        // Create our PlacedFeature so we can generate it.
        final ResourceKey<ConfiguredFeature<?,?>> configuredFeatureKey = NetherFeatures.LARGE_BASALT_COLUMNS.unwrapKey().get().cast(Registry.CONFIGURED_FEATURE_REGISTRY).get();
        final Holder<ConfiguredFeature<?,?>> configuredFeatureHolder = configuredFeatures.getOrCreateHolderOrThrow(configuredFeatureKey);
        final PlacedFeature placedFeature = new PlacedFeature(configuredFeatureHolder, List.of(CountOnEveryLayerPlacement.of(1), BiomeFilter.biome()));

        // Create our BiomeModifier so we can generate it.
        final HolderSet<PlacedFeature> placedFeatureHolderSet =
            HolderSet.direct(placedFeatures.getOrCreateHolderOrThrow(BASALT_PILLARS_KEY));
        final BiomeModifier biomeModifier = new TestModifier(
            new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_BADLANDS),
            Decoration.TOP_LAYER_MODIFICATION,
            placedFeatureHolderSet,
            new SpawnerData(EntityType.MAGMA_CUBE, 100, 1, 4),
            Precipitation.SNOW,
            0xFF0000
            );

        // Create and add our data providers.
        final DataProvider placedFeatureProvider =
            JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, MODID, ops, Registry.PLACED_FEATURE_REGISTRY,
                Map.of(BASALT_PILLARS_RL, placedFeature));
        generator.addProvider(event.includeServer(), placedFeatureProvider);
        
        final DataProvider biomeModifierProvider =
            JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, MODID, ops, ForgeRegistries.Keys.BIOME_MODIFIERS,
                Map.of(MODIFY_BADLANDS_RL, biomeModifier));
        generator.addProvider(event.includeServer(), biomeModifierProvider);
    }

    public record TestModifier(HolderSet<Biome> biomes, Decoration generationStage, HolderSet<PlacedFeature> features, SpawnerData spawn, Precipitation precipitation, int waterColor)
            implements BiomeModifier
    {
        private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(ADD_FEATURES_TO_BIOMES_RL, ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

        @Override
        public void modify(Holder<Biome> biome, Phase phase, Builder builder)
        {
            if (phase == Phase.ADD && this.biomes.contains(biome))
            {
                BiomeGenerationSettingsBuilder generation = builder.getGenerationSettings();
                this.features.forEach(holder -> generation.addFeature(this.generationStage, holder));
                builder.getMobSpawnSettings().addSpawn(this.spawn.type.getCategory(), this.spawn);
            }
            else if (phase == Phase.MODIFY && this.biomes.contains(biome))
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
                    Codec.STRING.comapFlatMap(TestModifier::generationStageFromString, Decoration::toString).fieldOf("generation_stage").forGetter(TestModifier::generationStage),
                    PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(TestModifier::features),
                    SpawnerData.CODEC.fieldOf("spawn").forGetter(TestModifier::spawn),
                    Precipitation.CODEC.fieldOf("precipitation").forGetter(TestModifier::precipitation),
                    Codec.INT.fieldOf("water_color").forGetter(TestModifier::waterColor)
            ).apply(builder, TestModifier::new));
        }

        private static DataResult<Decoration> generationStageFromString(String name)
        {
            try
            {
                return DataResult.success(Decoration.valueOf(name));
            }
            catch (Exception e)
            {
                return DataResult.error("Not a decoration stage: " + name);
            }
        }
    }
}
