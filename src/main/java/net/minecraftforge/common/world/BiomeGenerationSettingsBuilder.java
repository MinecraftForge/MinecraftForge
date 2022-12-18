/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BiomeGenerationSettingsBuilder extends BiomeGenerationSettings.PlainBuilder
{
    public BiomeGenerationSettingsBuilder(BiomeGenerationSettings orig)
    {
        orig.getCarvingStages().forEach(k -> {
            carvers.put(k, new ArrayList<>());
            orig.getCarvers(k).forEach(v -> carvers.get(k).add(v));
        });
        orig.features().forEach(l -> {
            final ArrayList<Holder<PlacedFeature>> featureList = new ArrayList<>();
            l.forEach(featureList::add);
            features.add(featureList);
        });
    }

    public List<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration stage) {
        addFeatureStepsUpTo(stage.ordinal());
        return features.get(stage.ordinal());
    }

    public List<Holder<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving stage) {
        return carvers.computeIfAbsent(stage, key -> new ArrayList<>());
    }
}