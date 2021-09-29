/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.world;

import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public List<Supplier<ConfiguredFeature<?, ?>>> getFeatures(GenerationStep.Decoration stage) {
        addFeatureStepsUpTo(stage.ordinal());
        return features.get(stage.ordinal());
    }

    public Optional<Supplier<ConfiguredSurfaceBuilder<?>>> getSurfaceBuilder() {
        return surfaceBuilder;
    }

    public List<Supplier<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving stage) {
        return carvers.computeIfAbsent(stage, key -> new ArrayList<>());
    }

    public List<Supplier<ConfiguredStructureFeature<?, ?>>> getStructures() {
        return structureStarts;
    }
}