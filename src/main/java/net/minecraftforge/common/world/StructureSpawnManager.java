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

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Class to help manage entity spawns inside of structures
 */
public class StructureSpawnManager
{
    private static Map<StructureFeature<?>, StructureSpawnInfo> structuresWithSpawns = Collections.emptyMap();

    /**
     * Gathers potential entity spawns for all the different registered structures.
     * @apiNote Internal
     */
    public static void gatherEntitySpawns()
    {
        //We use a linked hash map to ensure that we check the structures in an order that if there are multiple structures a position satisfies
        // then we have the same behavior as vanilla as vanilla checks, swamp huts, pillager outposts, ocean monuments, and nether fortresses in
        // that order.
        Map<StructureFeature<?>, StructureSpawnInfo> structuresWithSpawns = new LinkedHashMap<>();
        gatherEntitySpawns(structuresWithSpawns, StructureFeature.SWAMP_HUT);
        gatherEntitySpawns(structuresWithSpawns, StructureFeature.PILLAGER_OUTPOST);
        gatherEntitySpawns(structuresWithSpawns, StructureFeature.OCEAN_MONUMENT);
        gatherEntitySpawns(structuresWithSpawns, StructureFeature.NETHER_BRIDGE);
        for (StructureFeature<?> structure : ForgeRegistries.STRUCTURE_FEATURES.getValues())
        {
            if (structure != StructureFeature.SWAMP_HUT && structure != StructureFeature.PILLAGER_OUTPOST && structure != StructureFeature.OCEAN_MONUMENT &&
                structure != StructureFeature.NETHER_BRIDGE)
            {
                //If we didn't already gather the spawns already to ensure we do vanilla ones already
                // gather the spawns for this structure
                gatherEntitySpawns(structuresWithSpawns, structure);
            }
        }
        StructureSpawnManager.structuresWithSpawns = structuresWithSpawns;
    }

    private static void gatherEntitySpawns(Map<StructureFeature<?>, StructureSpawnInfo> structuresWithSpawns, StructureFeature<?> structure)
    {
        StructureSpawnListGatherEvent event = new StructureSpawnListGatherEvent(structure);
        MinecraftForge.EVENT_BUS.post(event);
        ImmutableMap.Builder<net.minecraft.world.entity.MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> builder = ImmutableMap.builder();
        event.getEntitySpawns().forEach((classification, spawns) -> {
            if (!spawns.isEmpty())
                builder.put(classification, WeightedRandomList.create());
        });
        ImmutableMap<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> entitySpawns = builder.build();
        if (!entitySpawns.isEmpty())
            structuresWithSpawns.put(structure, new StructureSpawnInfo(entitySpawns, event.isInsideOnly()));
    }

    /**
     * Looks up if a given position is within a structure and returns any entity spawns that structure has for the given classification, or null if
     * none are found.
     * @param structureManager Structure Manager, used to check if a position is within a structure.
     * @param classification   Entity classification
     * @param pos              Position to get entity spawns of
     */
    @Nullable
    public static WeightedRandomList<MobSpawnSettings.SpawnerData> getStructureSpawns(StructureFeatureManager structureManager, MobCategory classification, BlockPos pos)
    {
        for (Entry<StructureFeature<?>, StructureSpawnInfo> entry : structuresWithSpawns.entrySet())
        {
            StructureFeature<?> structure = entry.getKey();
            StructureSpawnInfo spawnInfo = entry.getValue();
            //Note: We check if the structure has spawns for a type first before looking at the world as it should be a cheaper check
            if (spawnInfo.spawns.containsKey(classification) && structureManager.getStructureAt(pos, spawnInfo.insideOnly, structure).isValid())
                return spawnInfo.spawns.get(classification);
        }
        return null;
    }

    /**
     * Gets the entity spawn lists for entities of a given classification for a given structure.
     * @param structure      The Structure
     * @param classification The classification to lookup
     */
    public static WeightedRandomList<MobSpawnSettings.SpawnerData> getSpawnList(StructureFeature<?> structure, MobCategory classification)
    {
        if (structuresWithSpawns.containsKey(structure))
            return structuresWithSpawns.get(structure).spawns.getOrDefault(classification, WeightedRandomList.create());
        return WeightedRandomList.create();
    }

    /**
     * Helper class to keep track of spawns and if the spawns should be restricted to inside the structure pieces.
     */
    private static class StructureSpawnInfo
    {
        private final Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawns;
        private final boolean insideOnly;

        private StructureSpawnInfo(ImmutableMap<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawns, boolean insideOnly)
        {
            this.spawns = spawns;
            this.insideOnly = insideOnly;
        }
    }
}
