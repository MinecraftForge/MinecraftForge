/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import org.jetbrains.annotations.Nullable;

public class StructureSettingsBuilder
{
    private HolderSet<Biome> biomes;
    private Map<MobCategory, StructureSpawnOverrideBuilder> spawnOverrides;
    private GenerationStep.Decoration step;
    private TerrainAdjustment terrainAdaptation;

    /**
     * @param settings Existing StructureSettings.
     * @return A new builder with a copy of that StructureSettings's values.
     */
    public static StructureSettingsBuilder copyOf(StructureSettings settings)
    {
        return create(settings.biomes(), settings.spawnOverrides(), settings.step(), settings.terrainAdaptation());
    }

    public static StructureSettingsBuilder create(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation)
    {
        return new StructureSettingsBuilder(biomes, spawnOverrides, step, terrainAdaptation);
    }

    private StructureSettingsBuilder(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation)
    {
        this.biomes = biomes;
        this.spawnOverrides = spawnOverrides.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> StructureSpawnOverrideBuilder.copyOf(entry.getValue())));
        this.step = step;
        this.terrainAdaptation = terrainAdaptation;
    }

    /**
     * @return A new StructureSettings with the finalized values.
     */
    public StructureSettings build()
    {
        Map<MobCategory, StructureSpawnOverride> overrides = Collections.unmodifiableMap(
              this.spawnOverrides.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().build()))
        );
        return new StructureSettings(this.biomes, overrides, this.step, this.terrainAdaptation);
    }

    public HolderSet<Biome> getBiomes()
    {
        return biomes;
    }

    public void setBiomes(HolderSet<Biome> biomes)
    {
        this.biomes = biomes;
    }

    @Nullable
    public StructureSpawnOverrideBuilder getSpawnOverrides(MobCategory category)
    {
        return spawnOverrides.get(category);
    }

    public StructureSpawnOverrideBuilder getOrAddSpawnOverrides(MobCategory category)
    {
        return spawnOverrides.computeIfAbsent(category, c -> new StructureSpawnOverrideBuilder(StructureSpawnOverride.BoundingBoxType.PIECE, Collections.emptyList()));
    }

    public void removeSpawnOverrides(MobCategory category)
    {
        this.spawnOverrides.remove(category);
    }

    public GenerationStep.Decoration getDecorationStep()
    {
        return step;
    }

    public void setDecorationStep(GenerationStep.Decoration step)
    {
        this.step = step;
    }

    public TerrainAdjustment getTerrainAdaptation()
    {
        return terrainAdaptation;
    }

    public void setTerrainAdaptation(TerrainAdjustment terrainAdaptation)
    {
        this.terrainAdaptation = terrainAdaptation;
    }

    public static class StructureSpawnOverrideBuilder
    {
        private StructureSpawnOverride.BoundingBoxType boundingBox;
        private List<MobSpawnSettings.SpawnerData> spawns;
        private final List<MobSpawnSettings.SpawnerData> spawnsView;

        public static StructureSpawnOverrideBuilder copyOf(StructureSpawnOverride override)
        {
            return create(override.boundingBox(), override.spawns());
        }

        public static StructureSpawnOverrideBuilder create(BoundingBoxType boundingBox, WeightedRandomList<MobSpawnSettings.SpawnerData> spawns)
        {
            return new StructureSpawnOverrideBuilder(boundingBox, spawns.unwrap());
        }

        private StructureSpawnOverrideBuilder(StructureSpawnOverride.BoundingBoxType boundingBox, List<MobSpawnSettings.SpawnerData> spawns)
        {
            this.boundingBox = boundingBox;
            this.spawns = new ArrayList<>(spawns);
            this.spawnsView = Collections.unmodifiableList(this.spawns);
        }

        public StructureSpawnOverride.BoundingBoxType getBoundingBox()
        {
            return boundingBox;
        }

        public void setBoundingBox(StructureSpawnOverride.BoundingBoxType boundingBox)
        {
            this.boundingBox = boundingBox;
        }

        /**
         * Unmodifiable view of the possible spawns.
         */
        public List<MobSpawnSettings.SpawnerData> getSpawns()
        {
            return spawnsView;
        }

        public void addSpawn(MobSpawnSettings.SpawnerData spawn)
        {
            this.spawns.add(spawn);
        }

        public void removeSpawn(MobSpawnSettings.SpawnerData spawn)
        {
            this.spawns.remove(spawn);
        }

        /**
         * @return A new StructureSpawnOverride with the finalized values.
         */
        public StructureSpawnOverride build()
        {
            return new StructureSpawnOverride(boundingBox, WeightedRandomList.create(spawns));
        }
    }
}