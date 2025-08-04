/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.biome;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegisterData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;
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

    public static final DeferredRegisterData<BiomeModifier> BIOME_MODIFIERS = DeferredRegisterData.create(ForgeRegistries.Keys.BIOME_MODIFIERS, MOD_ID);
    @SuppressWarnings("unused")
    private static final RegistryObject<BiomeModifier> MODIFIER = BIOME_MODIFIERS.register("modifier", ctx -> {
        return new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            ctx.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(PLACED.getHolder().orElseThrow()),
            GenerationStep.Decoration.UNDERGROUND_ORES
        );
    });

    public BiomeTestMod(FMLJavaModLoadingContext context) {
        super(context, false, true);
    }
}
