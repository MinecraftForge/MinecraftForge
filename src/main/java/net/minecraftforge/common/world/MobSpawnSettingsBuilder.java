/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.world;

import java.util.Collections;
import java.util.Set;

import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

public class MobSpawnSettingsBuilder extends MobSpawnSettings.Builder {
    private final Set<MobCategory> typesView = Collections.unmodifiableSet(this.spawners.keySet());
    private final Set<EntityType<?>> costView = Collections.unmodifiableSet(this.mobSpawnCosts.keySet());

    public MobSpawnSettingsBuilder(MobSpawnSettings orig) {
        orig.getSpawnerTypes().forEach(k ->
            spawners.put(k, WeightedList.<MobSpawnSettings.SpawnerData>builder().addAll(orig.getMobs(k).unwrap()))
        );
        orig.getEntityTypes().forEach(k -> mobSpawnCosts.put(k, orig.getMobSpawnCost(k)));
        creatureGenerationProbability = orig.getCreatureProbability();
    }

    public Set<MobCategory> getSpawnerTypes() {
        return this.typesView;
    }

    public WeightedList.Builder<MobSpawnSettings.SpawnerData> getSpawner(MobCategory type) {
        return this.spawners.get(type);
    }

    public Set<EntityType<?>> getEntityTypes() {
        return this.costView;
    }

    @Nullable
    public MobSpawnSettings.MobSpawnCost getCost(EntityType<?> type) {
        return this.mobSpawnCosts.get(type);
    }

    public float getProbability() {
        return this.creatureGenerationProbability;
    }

    public MobSpawnSettingsBuilder disablePlayerSpawn() {
        return this;
    }
}
