/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

/**
 * <p>Stock biome modifier that adds features to biomes. Has the following json format:</p>
 * <pre>
 * {
 *   "type": "forge:add_features", // required
 *   "biomes": "#namespace:your_biome_tag" // accepts a biome id, [list of biome ids], or #namespace:biome_tag
 *   "features": "namespace:your_feature", // accepts a placed feature id, [list of placed feature ids], or #namespace:feature_tag
 *   "step": "underground_ores" // accepts a Decoration enum name
 * }
 * </pre>
 * <p>Be wary of using this to add vanilla PlacedFeatures to biomes, as doing so may cause a feature cycle violation.</p>
 * 
 * @param biomes Biomes to add features to.
 * @param features PlacedFeatures to add to biomes.
 * @param step Decoration step to run features in.
 */
public record AddFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Decoration step) implements BiomeModifier
{
    @Override
    public void modify(Holder<Biome> biome, Phase phase, Builder builder)
    {
        if (phase == Phase.ADD && this.biomes.contains(biome))
        {
            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            this.features().forEach(holder -> generationSettings.addFeature(this.step(), holder));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec()
    {
        return ForgeMod.ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
    }
}
