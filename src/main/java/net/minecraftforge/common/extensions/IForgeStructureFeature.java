/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Collections;
import java.util.List;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public interface IForgeStructureFeature
{
    private StructureFeature<?> self()
    {
        return (StructureFeature<?>) this;
    }

    /**
     * Gets the default list of spawns for this structure, of the specified category.
     *
     * @apiNote Implement this over any of the type specific getSpecial functions in StructureFeature.
     */
    @SuppressWarnings("incomplete-switch")
    default List<MobSpawnSettings.SpawnerData> getDefaultSpawnList(MobCategory category)
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
        return self() != StructureFeature.PILLAGER_OUTPOST && self() != StructureFeature.OCEAN_MONUMENT;
    }

    /**
     * Helper method to get the list of entity spawns for this structure for the given classification.
     * @param classification The classification of entities.
     * @apiNote This method is marked as final in {@link StructureFeature} so as to not be overridden by modders and breaking support for
     * {@link net.minecraftforge.event.world.StructureSpawnListGatherEvent}.
     */
    WeightedRandomList<MobSpawnSettings.SpawnerData> getSpawnList(MobCategory classification);
}
