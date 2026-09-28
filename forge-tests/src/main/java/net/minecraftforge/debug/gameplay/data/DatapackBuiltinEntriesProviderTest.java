/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.data;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.AnyOfRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.data.RegistryDataBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.test.BaseTestMod;

import java.util.*;

@Mod(DatapackBuiltinEntriesProviderTest.MOD_ID)
public class DatapackBuiltinEntriesProviderTest extends BaseTestMod {

    public static final String MOD_ID = "datapack_builtin_entries_provider_test";
    // Vanilla registry entries
    public static final ResourceKey<Feature> MOSSY_STONE_FEATURE = ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(MOD_ID, "mossy_stone"));
    public static final ResourceKey<PlacedFeature> MOSSY_STONE_PLACEMENT = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(MOD_ID, "mossy_stone"));
    // Forge registry entries
    public static final ResourceKey<BiomeModifier> MOSSY_STONE_MODIFIER = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(MOD_ID, "mossy_stone_modifier"));

    public DatapackBuiltinEntriesProviderTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        GatherDataEvent.getBus(modBus).addListener(this::gatherData);
    }

    private void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        /* Adds the DataPackBuiltinEntriesProvider to the data generator
         * If the registry is not correctly patched (it does only include the vanilla registries), the provider will fail with an exception
         * Reason: The RegistrySetBuilder creates a full patched registry including a lookup for all registries
         *         For the lookup a cloner is needed, which is not available for forge registries
         */
        var registries = RegistryDataBuilder.of()
            .name(modid())
            .world(set -> set
                .add(Registries.FEATURE, this::createFeature)
                .add(Registries.PLACED_FEATURE, this::createPlacement)
                .add(ForgeRegistries.Keys.BIOME_MODIFIERS, this::createModifier)
            );
        gen.addProvider(event.includeServer(), registries.worldGenerator(gen.getPackOutput()));
    }

    // Registers the mossy stone feature
    private void createFeature(BootstrapContext<Feature> context) {
        context.register(MOSSY_STONE_FEATURE, new OreFeature(
            new AnyOfRuleTest(List.of(
                new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
            )), Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 5
        ));
    }

    // Registers the mossy stone placement
    private void createPlacement(BootstrapContext<PlacedFeature> context) {
        var featureRegistry = context.lookup(Registries.FEATURE);
        context.register(MOSSY_STONE_PLACEMENT, new PlacedFeature(
            featureRegistry.getOrThrow(MOSSY_STONE_FEATURE),
            List.of(CountPlacement.of(8), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), BiomeFilter.biome())
        ));
    }

    // Registers the mossy stone biome modifier
    private void createModifier(BootstrapContext<BiomeModifier> context) {
        var biomeRegistry = context.lookup(Registries.BIOME);
        var placementRegistry = context.lookup(Registries.PLACED_FEATURE);
        context.register(MOSSY_STONE_MODIFIER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomeRegistry.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(placementRegistry.getOrThrow(MOSSY_STONE_PLACEMENT)),
            GenerationStep.Decoration.UNDERGROUND_ORES
        ));
    }
}
