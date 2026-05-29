/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.gameplay.biome;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftsingularity.common.world.BiomeModifier;
import net.minecraftsingularity.common.world.singularityBiomeModifiers;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegisterData;
import net.minecraftsingularity.registries.singularityRegistries;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.test.BaseTestMod;
import java.util.List;

@Mod(BiomeTestMod.MOD_ID)
public class BiomeTestMod extends BaseTestMod {
    public static final String MOD_ID = "biome_test";

    public static final DeferredRegisterData<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegisterData.create(Registries.CONFIGURED_FEATURE, MOD_ID);
    private static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED = CONFIGURED_FEATURES.register("configured", () ->
        new ConfiguredFeature<>(Feature.NO_OP, NoneFeatureConfiguration.INSTANCE)
    );

    public static final DeferredRegisterData<PlacedFeature> PLACED_FEATURES = DeferredRegisterData.create(Registries.PLACED_FEATURE, MOD_ID);
    private static final RegistryObject<PlacedFeature> PLACED = PLACED_FEATURES.register("placed", () ->
        new PlacedFeature(CONFIGURED.getHolder().orElseThrow(), List.of())
    );

    public static final DeferredRegisterData<BiomeModifier> BIOME_MODIFIERS = DeferredRegisterData.create(singularityRegistries.Keys.BIOME_MODIFIERS, MOD_ID);
    @SuppressWarnings("unused")
    private static final RegistryObject<BiomeModifier> MODIFIER = BIOME_MODIFIERS.register("modifier", ctx -> {
        return new singularityBiomeModifiers.AddFeaturesBiomeModifier(
            ctx.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(PLACED.getHolder().orElseThrow()),
            GenerationStep.Decoration.UNDERGROUND_ORES
        );
    });

    public BiomeTestMod(FMLJavaModLoadingContext context) {
        super(context, false, true);
    }
}
