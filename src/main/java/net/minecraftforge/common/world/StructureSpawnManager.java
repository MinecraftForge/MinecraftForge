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

package net.minecraftforge.common.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class StructureSpawnManager
{
    private static Map<Structure<?>, StructureSpawnInfo> structuresWithSpawns = Collections.emptyMap();

    public static void gatherEntitySpawns()
    {
        //Ensure that we check the structures in an order that if there are multiple structures a position satisfies then we have the
        // same behavior as vanilla as vanilla checks, swamp huts, pillager outposts, ocean monuments, and nether fortresses in that order.
        Map<Structure<?>, StructureSpawnInfo> structuresWithSpawns = new LinkedHashMap<>();
        gatherEntitySpawns(structuresWithSpawns, Structure.field_236374_j_);
        gatherEntitySpawns(structuresWithSpawns, Structure.field_236366_b_);
        gatherEntitySpawns(structuresWithSpawns, Structure.field_236376_l_);
        gatherEntitySpawns(structuresWithSpawns, Structure.field_236378_n_);
        for (Structure<?> structure : ForgeRegistries.STRUCTURE_FEATURES.getValues())
        {
            if (structure != Structure.field_236374_j_ && structure != Structure.field_236366_b_ && structure != Structure.field_236376_l_ &&
                structure != Structure.field_236378_n_)
            {
                //If we didn't already gather the spawns already to ensure we do vanilla ones already
                // gather the spawns for this structure
                gatherEntitySpawns(structuresWithSpawns, structure);
            }
        }
        StructureSpawnManager.structuresWithSpawns = structuresWithSpawns;
    }

    private static void gatherEntitySpawns(Map<Structure<?>, StructureSpawnInfo> structuresWithSpawns, Structure<?> structure)
    {
        StructureSpawnListGatherEvent event = new StructureSpawnListGatherEvent(structure);
        MinecraftForge.EVENT_BUS.post(event);
        ImmutableMap.Builder<net.minecraft.entity.EntityClassification, List<MobSpawnInfo.Spawners>> builder = ImmutableMap.builder();
        event.getEntitySpawns().forEach((classification, spawns) -> {
            if (!spawns.isEmpty())
                builder.put(classification, ImmutableList.copyOf(spawns));
        });
        Map<EntityClassification, List<MobSpawnInfo.Spawners>> entitySpawns = builder.build();
        if (!entitySpawns.isEmpty())
            structuresWithSpawns.put(structure, new StructureSpawnInfo(entitySpawns, event.restrictsSpawnsToInside()));
    }

    @Nullable
    public static List<MobSpawnInfo.Spawners> getStructureSpawns(StructureManager structureManager, EntityClassification classification, BlockPos pos)
    {
        for (Entry<Structure<?>, StructureSpawnInfo> entry : structuresWithSpawns.entrySet())
        {
            Structure<?> structure = entry.getKey();
            //Note: We check if the structure has spawns for a type first before looking at the world as it should be a cheaper check
            if (structure.hasSpawnsFor(classification) && structureManager.func_235010_a_(pos, entry.getValue().restrictsSpawnsToInside, structure).isValid())
                return structure.getSpawnList(classification);
        }
        return null;
    }

    public static List<MobSpawnInfo.Spawners> getSpawnList(Structure<?> structure, EntityClassification classification)
    {
        if (structuresWithSpawns.containsKey(structure))
            return structuresWithSpawns.get(structure).spawns.getOrDefault(classification, Collections.emptyList());
        return Collections.emptyList();
    }

    public static boolean hasSpawnsFor(Structure<?> structure, EntityClassification classification)
    {
        if (structuresWithSpawns.containsKey(structure))
            return structuresWithSpawns.get(structure).spawns.containsKey(classification);
        return false;
    }

    private static class StructureSpawnInfo
    {
        private final Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawns;
        private final boolean restrictsSpawnsToInside;

        private StructureSpawnInfo(Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawns, boolean restrictsSpawnsToInside)
        {
            this.spawns = spawns;
            this.restrictsSpawnsToInside = restrictsSpawnsToInside;
        }
    }
}
