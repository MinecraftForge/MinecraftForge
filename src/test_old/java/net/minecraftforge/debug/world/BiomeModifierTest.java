/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
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

    /* Static registry objects */

    // Biome Modifier Serializers
    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);
    private static final RegistryObject<Codec<TestModifier>> MODIFY_BIOMES = BIOME_MODIFIER_SERIALIZERS.register("modify_biomes", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(TestModifier::biomes),
                    Codec.STRING.xmap(s -> Precipitation.valueOf(s.toUpperCase(Locale.ROOT)), e -> e.name().toLowerCase(Locale.ROOT)).fieldOf("precipitation").forGetter(TestModifier::precipitation),
                    Codec.INT.fieldOf("water_color").forGetter(TestModifier::waterColor)
            ).apply(builder, TestModifier::new))
    );

    /* Dynamic registry objects */

    private static final ResourceKey<PlacedFeature> LARGE_BASALT_COLUMNS = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MODID, "large_basalt_columns"));

    private static final ResourceKey<BiomeModifier> ADD_BASALT_MODIFIER = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(MODID, "add_basalt"));
    private static final ResourceKey<BiomeModifier> ADD_MAGMA_CUBES_MODIFIER = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(MODID, "add_magma_cubes"));
    private static final ResourceKey<BiomeModifier> MODIFY_BADLANDS_MODIFIER = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(MODID, "modify_badlands"));
    private static final ResourceKey<BiomeModifier> REMOVE_FOREST_TREES_MODIFIER = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(MODID, "remove_forest_trees"));
    private static final ResourceKey<BiomeModifier> REMOVE_FOREST_SKELETONS_MODIFIER = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(MODID, "remove_forest_skeletons"));

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.PLACED_FEATURE, context -> context.register(LARGE_BASALT_COLUMNS,
                    new PlacedFeature(
                            context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(NetherFeatures.LARGE_BASALT_COLUMNS),
                            List.of(CountOnEveryLayerPlacement.of(1), BiomeFilter.biome())
                    )
            ))
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                var badlandsTag = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_BADLANDS);
                var forestTag = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_FOREST);

                context.register(ADD_BASALT_MODIFIER, new AddFeaturesBiomeModifier(
                        badlandsTag,
                        HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(LARGE_BASALT_COLUMNS)),
                        Decoration.TOP_LAYER_MODIFICATION
                ));

                context.register(ADD_MAGMA_CUBES_MODIFIER, AddSpawnsBiomeModifier.singleSpawn(
                        badlandsTag,
                        new SpawnerData(EntityType.MAGMA_CUBE, 100, 1, 4)
                ));

                context.register(MODIFY_BADLANDS_MODIFIER, new TestModifier(
                        badlandsTag,
                        Precipitation.SNOW,
                        0xFF000
                ));

                context.register(REMOVE_FOREST_TREES_MODIFIER, RemoveFeaturesBiomeModifier.allSteps(
                        forestTag,
                        HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(VegetationPlacements.TREES_BIRCH_AND_OAK))
                ));

                context.register(REMOVE_FOREST_SKELETONS_MODIFIER, new RemoveSpawnsBiomeModifier(
                        forestTag,
                        context.lookup(Registries.ENTITY_TYPE).getOrThrow(EntityTypeTags.SKELETONS)
                ));
            });

    public BiomeModifierTest()
    {
        if (!ENABLED)
            return;

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Serializer types can be registered via deferred register.
        BIOME_MODIFIER_SERIALIZERS.register(modBus);

        modBus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event)
    {
        event.getGenerator().addProvider(event.includeServer(), (DataProvider.Factory<BiomeModifiers>) output -> new BiomeModifiers(output, event.getLookupProvider()));
    }

    private static class BiomeModifiers extends DatapackBuiltinEntriesProvider
    {

        public BiomeModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
        {
            super(output, registries, BUILDER, Set.of(MODID));
        }

        @Override
        public String getName()
        {
            return "Biome Modifier Registries: " + MODID;
        }
    }

    public record TestModifier(HolderSet<Biome> biomes, Precipitation precipitation, int waterColor) implements BiomeModifier
    {
        @Override
        public void modify(Holder<Biome> biome, Phase phase, Builder builder)
        {
            if (phase == Phase.MODIFY && this.biomes.contains(biome))
            {
                builder.getClimateSettings().setHasPrecipitation(true);
                builder.getSpecialEffects().waterColor(this.waterColor);
                if (this.precipitation == Precipitation.SNOW)
                    builder.getClimateSettings().setTemperature(0F);
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec()
        {
            return MODIFY_BIOMES.get();
        }
    }
}
