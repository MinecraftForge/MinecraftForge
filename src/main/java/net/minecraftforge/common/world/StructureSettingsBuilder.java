/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import org.jetbrains.annotations.Nullable;

public class StructureSettingsBuilder
{
    private HolderSet<Biome> biomes;
    private final Map<MobCategory, StructureSpawnOverrideBuilder> spawnOverrides;
    private GenerationStep.Decoration step;
    private TerrainAdjustment terrainAdaptation;

    /**
     * @param settings Existing StructureSettings.
     * @return A new builder with a copy of that StructureSettings's values.
     */
    public static StructureSettingsBuilder copyOf(StructureSettings settings)
    {
        return new StructureSettingsBuilder(settings.biomes(), settings.spawnOverrides(), settings.step(), settings.terrainAdaptation());
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

    /**
     * Gets a mutable builder for the spawn overrides of a given mob category or {@code null} if no overrides are defined for that category.
     * @param category Mob category
     */
    @Nullable
    public StructureSpawnOverrideBuilder getSpawnOverrides(MobCategory category)
    {
        return spawnOverrides.get(category);
    }

    /**
     * Gets or creates a mutable builder for the spawn overrides of a given mob category. If the override needed to be created it will default to piece bounding.
     * @param category Mob category
     */
    public StructureSpawnOverrideBuilder getOrAddSpawnOverrides(MobCategory category)
    {
        return spawnOverrides.computeIfAbsent(category, c -> new StructureSpawnOverrideBuilder(StructureSpawnOverride.BoundingBoxType.PIECE, Collections.emptyList()));
    }

    /**
     * Removes the spawn overrides for the given mob category.
     * @param category Mob category
     */
    public void removeSpawnOverrides(MobCategory category)
    {
        this.spawnOverrides.remove(category);
    }

    /**
     * Gets the world generation decoration step the structure spawns during.
     */
    public GenerationStep.Decoration getDecorationStep()
    {
        return step;
    }

    /**
     * Sets the world generation decoration step the structure spawns during.
     */
    public void setDecorationStep(GenerationStep.Decoration step)
    {
        this.step = step;
    }

    /**
     * Gets the way the structure adapts to the terrain during generation.
     */
    public TerrainAdjustment getTerrainAdaptation()
    {
        return terrainAdaptation;
    }

    /**
     * Sets the way the structure adapts to the terrain during generation.
     * @param terrainAdaptation New terrain adjustment
     */
    public void setTerrainAdaptation(TerrainAdjustment terrainAdaptation)
    {
        this.terrainAdaptation = terrainAdaptation;
    }

    public static class StructureSpawnOverrideBuilder
    {
        private StructureSpawnOverride.BoundingBoxType boundingBox;
        private final List<MobSpawnSettings.SpawnerData> spawns;
        private final List<MobSpawnSettings.SpawnerData> spawnsView;

        /**
         * @param override Existing spawn override data.
         * @return A new builder with a copy of that StructureSpawnOverride's values.
         */
        public static StructureSpawnOverrideBuilder copyOf(StructureSpawnOverride override)
        {
            return new StructureSpawnOverrideBuilder(override.boundingBox(), override.spawns().unwrap());
        }

        private StructureSpawnOverrideBuilder(StructureSpawnOverride.BoundingBoxType boundingBox, List<MobSpawnSettings.SpawnerData> spawns)
        {
            this.boundingBox = boundingBox;
            this.spawns = new ArrayList<>(spawns);
            this.spawnsView = Collections.unmodifiableList(this.spawns);
        }

        /**
         * Gets the type of bounding box for this structures spawn overrides.
         */
        public StructureSpawnOverride.BoundingBoxType getBoundingBox()
        {
            return boundingBox;
        }

        /**
         * Sets the way the structure checks for spawn overrides. Whether it is on a piece by piece basis or within the bounds of the overall structure.
         */
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

        /**
         * Adds a spawn to the overrides.
         */
        public void addSpawn(MobSpawnSettings.SpawnerData spawn)
        {
            this.spawns.add(spawn);
        }

        /**
         * Removes a given spawn from the list of overrides. Use {@link #getSpawns()} to get instances of spawn data to remove.
         */
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