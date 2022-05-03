/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BiomeGenerationSettingsBuilder extends BiomeGenerationSettings.Builder
{
    public BiomeGenerationSettingsBuilder(BiomeGenerationSettings orig)
    {
        surfaceBuilder = Optional.of(orig.getSurfaceBuilder());
        orig.getCarvingStages().forEach(k -> carvers.put(k, new ArrayList<>(orig.getCarvers(k))));
        orig.features().forEach(l -> features.add(new ArrayList<>(l)));
        structureStarts.addAll(orig.structures());
    }

    public List<Supplier<ConfiguredFeature<?, ?>>> getFeatures(GenerationStage.Decoration stage) {
        addFeatureStepsUpTo(stage.ordinal());
        return features.get(stage.ordinal());
    }

    public Optional<Supplier<ConfiguredSurfaceBuilder<?>>> getSurfaceBuilder() {
        return surfaceBuilder;
    }

    public List<Supplier<ConfiguredCarver<?>>> getCarvers(GenerationStage.Carving stage) {
        return carvers.computeIfAbsent(stage, key -> new ArrayList<>());
    }

    public List<Supplier<StructureFeature<?, ?>>> getStructures() {
        return structureStarts;
    }
}