/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.data;

import net.minecraft.DetectedVersion;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.test.BaseTestMod;

import java.util.*;
import java.util.function.Supplier;

@Mod(DatapackBuiltinEntriesProviderTest.MOD_ID)
public class DatapackBuiltinEntriesProviderTest extends BaseTestMod {

    public static final String MOD_ID = "datapack_builtin_entries_provider_test";
    // Vanilla registry entries
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSSY_STONE_FEATURE = ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MOD_ID, "mossy_stone"));
    public static final ResourceKey<PlacedFeature> MOSSY_STONE_PLACEMENT = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(MOD_ID, "mossy_stone"));
    // Forge registry entries
    public static final ResourceKey<BiomeModifier> MOSSY_STONE_MODIFIER = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(MOD_ID, "mossy_stone_modifier"));
    // The ore targets
    public static final Supplier<List<OreConfiguration.TargetBlockState>> MOSSY_STONE_TARGETS = () -> {
        return List.of(
            OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.MOSSY_COBBLESTONE.defaultBlockState()),
            OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), Blocks.MOSSY_COBBLESTONE.defaultBlockState())
        );
    };

    @SubscribeEvent
    public void onDataGen(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        /* Adds the DataPackBuiltinEntriesProvider to the data generator
         * If the registry is not correctly patched (it does only include the vanilla registries), the provider will fail with an exception
         * Reason: The RegistrySetBuilder creates a full patched registry including a lookup for all registries
         *         For the lookup a cloner is needed, which is not available for forge registries
         */
        gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(), this.createProvider(), Set.of(MOD_ID)));
    }

    // Creates the registry builder for 2 vanilla and 1 forge registry
    private RegistrySetBuilder createProvider() {
        var builder = new RegistrySetBuilder();
        builder.add(Registries.CONFIGURED_FEATURE, c -> this.createFeature(c));
        builder.add(Registries.PLACED_FEATURE, this::createPlacement);
        builder.add(ForgeRegistries.Keys.BIOME_MODIFIERS, this::createModifier);
        return builder;
    }

    // Registers the mossy stone feature
    private void createFeature(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(MOSSY_STONE_FEATURE, new ConfiguredFeature<>(
            Feature.ORE,
            new OreConfiguration(MOSSY_STONE_TARGETS.get(), 5)
        ));
    }

    // Registers the mossy stone placement
    private void createPlacement(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> featureRegistry = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(MOSSY_STONE_PLACEMENT, new PlacedFeature(
            featureRegistry.getOrThrow(MOSSY_STONE_FEATURE),
            List.of(CountPlacement.of(8), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), BiomeFilter.biome())
        ));
    }

    // Registers the mossy stone biome modifier
    private void createModifier(BootstapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placementRegistry = context.lookup(Registries.PLACED_FEATURE);
        context.register(MOSSY_STONE_MODIFIER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomeRegistry.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(placementRegistry.getOrThrow(MOSSY_STONE_PLACEMENT)),
            GenerationStep.Decoration.UNDERGROUND_ORES
        ));
    }
}
