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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.MobSpawnInfo;

public class MobSpawnInfoBuilder extends MobSpawnInfo.Builder
{
    private final Set<EntityClassification> typesView = Collections.unmodifiableSet(this.spawners.keySet());
    private final Set<EntityType<?>> costView = Collections.unmodifiableSet(this.spawnCosts.keySet());

    public MobSpawnInfoBuilder(MobSpawnInfo orig)
    {
        orig.getSpawnerTypes().forEach(k -> {
            spawners.get(k).clear();
            spawners.get(k).addAll(new java.util.ArrayList<>(orig.getSpawners(k)));
        });
        orig.getEntityTypes().forEach(k -> spawnCosts.put(k, orig.getSpawnCost(k)));
        creatureSpawnProbability = orig.getCreatureSpawnProbability();
        validSpawnBiomeForPlayer = orig.isValidSpawnBiomeForPlayer();
    }

    public Set<EntityClassification> getSpawnerTypes()
    {
        return this.typesView;
    }

    public List<MobSpawnInfo.Spawners> getSpawner(EntityClassification type)
    {
        return this.spawners.get(type);
    }

    public Set<EntityType<?>> getEntityTypes()
    {
        return this.costView;
    }

    @Nullable
    public MobSpawnInfo.SpawnCosts getCost(EntityType<?> type)
    {
        return this.spawnCosts.get(type);
    }

    public float getProbability()
    {
        return this.creatureSpawnProbability;
    }

    public MobSpawnInfoBuilder disablePlayerSpawn()
    {
        this.validSpawnBiomeForPlayer = false;
        return this;
    }
}