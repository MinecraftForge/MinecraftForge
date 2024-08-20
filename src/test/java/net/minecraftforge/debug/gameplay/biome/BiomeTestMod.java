/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.biome;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.test.BaseTestMod;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


@Mod(BiomeTestMod.MOD_ID)
public class BiomeTestMod extends BaseTestMod {
    public static final String MOD_ID = "biome_test_mod";

    private static final ResourceKey<PlacedFeature> OVERWORLD_NETHERITE_BLOCK_ORE_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(BiomeTestMod.MOD_ID, "overworld_netherite_block_ore_placed"));
    private static final ResourceKey<BiomeModifier> ADD_OVERWORLD_NETHERITE_BLOCK_ORE = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(BiomeTestMod.MOD_ID, "add_overworld_netherite_block_ore"));
    private static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_NETHERITE_BLOCK_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(BiomeTestMod.MOD_ID, "overworld_netherite_block_ore"));

    public BiomeTestMod(FMLConstructModEvent context) {
        super(context);
        modBus.addListener(BiomeTestMod::onDataGen);
    }

    private static void onDataGen(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), new ModWorldGenProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }

    private static class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
        public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, context -> {
                    context.register(OVERWORLD_NETHERITE_BLOCK_ORE, new ConfiguredFeature<>(
                            Feature.REPLACE_SINGLE_BLOCK,
                            new ReplaceBlockConfiguration(Blocks.AIR.defaultBlockState(), Blocks.NETHERITE_BLOCK.defaultBlockState())
                    ));
                })
                .add(Registries.PLACED_FEATURE, context -> {
                    context.register(OVERWORLD_NETHERITE_BLOCK_ORE_PLACED_KEY, new PlacedFeature(
                            context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(OVERWORLD_NETHERITE_BLOCK_ORE),
                            List.of(CountPlacement.of(1), HeightRangePlacement.of(ConstantHeight.of(VerticalAnchor.absolute(100))))
                    ));
                })
                .add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                    context.register(ADD_OVERWORLD_NETHERITE_BLOCK_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                            context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                            HolderSet.direct(context.lookup(Registries.PLACED_FEATURE).getOrThrow(OVERWORLD_NETHERITE_BLOCK_ORE_PLACED_KEY)),
                            GenerationStep.Decoration.UNDERGROUND_ORES
                    ));
                });

        public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, BUILDER, Set.of(BiomeTestMod.MOD_ID));
        }
    }
}
