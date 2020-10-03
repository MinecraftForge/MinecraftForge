/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

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

    private final Structure<?> structure;
    private final Map<EntityClassification, List<MobSpawnInfo.Spawners>> entitySpawns = new HashMap<>();
    private boolean restrictSpawnsToInside;

    public StructureSpawnListGatherEvent(Structure<?> structure)
    {
        this.structure = structure;
        this.restrictSpawnsToInside = this.structure.getDefaultRestrictsSpawnsToInside();
        this.entitySpawns.put(EntityClassification.MONSTER, new ArrayList<>(this.structure.getDefaultSpawnList()));
        this.entitySpawns.put(EntityClassification.CREATURE, new ArrayList<>(this.structure.getDefaultCreatureSpawnList()));
    }

    /**
     * @return Structure to add or remove spawns to/from.
     */
    public Structure<?> getStructure()
    {
        return structure;
    }

    /**
     * Restrict entity spawn location checks to being inside the pieces of the structure.
     */
    public void restrictSpawnsToInside()
    {
        this.restrictSpawnsToInside = true;
    }

    /**
     * Allow entity spawn location checks to match being inside the overall structure but outside the individual pieces (basically equates to allowing spawns outside and
     * nearby).
     */
    public void allowSpawnsOutside()
    {
        this.restrictSpawnsToInside = false;
    }

    /**
     * Checks if spawns for the structure are restricted to being inside the individual pieces of the structure.
     */
    public boolean restrictsSpawnsToInside()
    {
        return restrictSpawnsToInside;
    }

    /**
     * Gets the list representing the entity spawns for the given classification, creating and adding an empty list if needed. This list is mutable and spawns should be
     * added/removed from this list.
     * @param classification Entity Classification
     * @return The list of spawns for the given classification.
     */
    public List<MobSpawnInfo.Spawners> getEntitySpawns(EntityClassification classification)
    {
        return this.entitySpawns.computeIfAbsent(classification, c -> new ArrayList<>());
    }

    /**
     * Gets the map of spawns based on entity classification that is used to fill in the various spawn lists for the structure.
     */
    public Map<EntityClassification, List<MobSpawnInfo.Spawners>> getEntitySpawns()
    {
        return entitySpawns;
    }
}