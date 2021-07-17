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

package net.minecraftforge.event.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event fires when a Structure is gathering what mobs/creatures can spawn in it.
 *
 * In order to maintain the most compatibility possible with other mods' modifications to a structure,
 * the event should be assigned a {@link net.minecraftforge.eventbus.api.EventPriority} as follows:
 *
 * - Additions : {@link EventPriority#HIGH}
 * - Removals : {@link EventPriority#NORMAL}
 * - Any other modification : {@link EventPriority#LOW}
 *
 * Be aware that another mod could have done an operation beforehand, so an expected value out of a vanilla structure might not
 * always be the same, depending on other mods.
 */
public class StructureSpawnListGatherEvent extends Event
{

    private final StructureFeature<?> structure;
    private final Map<MobCategory, List<MobSpawnSettings.SpawnerData>> entitySpawns = new HashMap<>();
    private final Map<MobCategory, List<MobSpawnSettings.SpawnerData>> entitySpawnsUnmodifiableLists = new HashMap<>();
    private final Map<MobCategory, List<MobSpawnSettings.SpawnerData>> entitySpawnsUnmodifiable = Collections.unmodifiableMap(entitySpawnsUnmodifiableLists);
    private boolean insideOnly;

    public StructureSpawnListGatherEvent(StructureFeature<?> structure)
    {
        this.structure = structure;
        this.insideOnly = this.structure.getDefaultRestrictsSpawnsToInside();
        addEntitySpawns(MobCategory.MONSTER, this.structure.getDefaultSpawnList());
        addEntitySpawns(MobCategory.CREATURE, this.structure.getDefaultCreatureSpawnList());
    }

    /**
     * @return Structure to add or remove spawns to/from.
     */
    public StructureFeature<?> getStructure()
    {
        return structure;
    }

    /**
     * Change if entity spawn location checks are done against the entire bounds of the structure or only the inside the pieces of the structure.
     * @param insideOnly {@code true} to restrict the spawn checks to inside the pieces of the structure, {@code false} to allow spawns outside
     */
    public void setInsideOnly(boolean insideOnly)
    {
        this.insideOnly = insideOnly;
    }

    /**
     * Checks if spawns for the structure are restricted to being inside the individual pieces of the structure.
     */
    public boolean isInsideOnly()
    {
        return insideOnly;
    }

    /**
     * Gets an unmodifiable view of the the list representing the entity spawns for the given classification.
     * @param classification Entity Classification
     * @return The list of spawns for the given classification.
     */
    public List<MobSpawnSettings.SpawnerData> getEntitySpawns(MobCategory classification)
    {
        return this.entitySpawnsUnmodifiableLists.getOrDefault(classification, Collections.emptyList());
    }

    /**
     * Gets the internal spawn list for a given entity classification, or adds one if needed. (This includes adding it to the unmodifiable view)
     */
    private List<MobSpawnSettings.SpawnerData> getOrCreateEntitySpawns(MobCategory classification)
    {
        return this.entitySpawns.computeIfAbsent(classification, c -> {
            List<MobSpawnSettings.SpawnerData> spawners = new ArrayList<>();
            //If the classification isn't in entitySpawns yet, also add an unmodifiable view of the list to
            // the unmodifiable list spawn map
            this.entitySpawnsUnmodifiableLists.put(c, Collections.unmodifiableList(spawners));
            return spawners;
        });
    }

    /**
     * Adds a spawn to the list of spawns for the given classification.
     * @param classification Entity Classification
     * @param spawner        Spawner
     */
    public void addEntitySpawn(MobCategory classification, MobSpawnSettings.SpawnerData spawner)
    {
        getOrCreateEntitySpawns(classification).add(spawner);
    }

    /**
     * Adds spawns to the list of spawns for the given classification.
     * @param classification Entity Classification
     * @param spawners       Spawners to add
     */
    public void addEntitySpawns(MobCategory classification, List<MobSpawnSettings.SpawnerData> spawners)
    {
        getOrCreateEntitySpawns(classification).addAll(spawners);
    }

    /**
     * Removes a spawn from the list of spawns for the given classification.
     * @param classification Entity Classification
     * @param spawner        Spawner
     */
    public void removeEntitySpawn(MobCategory classification, MobSpawnSettings.SpawnerData spawner)
    {
        if (this.entitySpawns.containsKey(classification))
            this.entitySpawns.get(classification).remove(spawner);
    }

    /**
     * Gets an unmodifiable view of the map of spawns based on entity classification that is used to fill in the various spawn lists for the structure.
     */
    public Map<MobCategory, List<MobSpawnSettings.SpawnerData>> getEntitySpawns()
    {
        return entitySpawnsUnmodifiable;
    }
}