/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;

public interface IForgeStructure
{
    default Structure<?> getStructure()
    {
        return (Structure<?>) this;
    }

    /**
     * Gets the default list of {@link EntityClassification#MONSTER} spawns for this structure.
     *
     * @apiNote Implement this over {@link Structure#getSpawnList()}
     */
    default List<MobSpawnInfo.Spawners> getDefaultSpawnList()
    {
        return Collections.emptyList();
    }

    /**
     * Gets the default list of {@link EntityClassification#CREATURE} spawns for this structure.
     *
     * @apiNote Implement this over {@link Structure#getCreatureSpawnList()}
     */
    default List<MobSpawnInfo.Spawners> getDefaultCreatureSpawnList()
    {
        return Collections.emptyList();
    }

    /**
     * Gets the default for if entity spawns are restricted to being inside of the pieces making up the structure or if being in the bounds of the overall structure
     * is good enough.
     * @return {@code true} if the location to check spawns for has to be inside of the pieces making up the structure, {@code false} otherwise.
     */
    default boolean getDefaultRestrictsSpawnsToInside()
    {
        //The Pillager Outpost and Ocean Monument check the full structure by default instead of limiting themselves to being within the structure's bounds
        return getStructure() != Structure.PILLAGER_OUTPOST && getStructure() != Structure.OCEAN_MONUMENT;
    }

    /**
     * Helper method to get the list of entity spawns for this structure for the given classification.
     * @param classification The classification of entities.
     * @apiNote This method is marked as final in {@link Structure} so as to not be overridden by modders and breaking support for
     * {@link net.minecraftforge.event.world.StructureSpawnListGatherEvent}.
     */
    List<MobSpawnInfo.Spawners> getSpawnList(EntityClassification classification);
}
