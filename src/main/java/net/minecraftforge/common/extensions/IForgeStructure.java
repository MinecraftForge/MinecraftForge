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

package net.minecraftforge.common.extensions;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.world.StructureSpawnManager;

public interface IForgeStructure
{
    default Structure<?> getStructure()
    {
        return (Structure<?>) this;
    }

    default List<MobSpawnInfo.Spawners> getDefaultSpawnList()
    {
        return Collections.emptyList();
    }

    default List<MobSpawnInfo.Spawners> getDefaultCreatureSpawnList()
    {
        return Collections.emptyList();
    }

    default boolean getDefaultRestrictsSpawnsToInside()
    {
        //The Pillager Outpost and Ocean Monument check the full structure by default instead of limiting themselves to being within the structure's bounds
        return getStructure() != Structure.field_236366_b_ && getStructure() != Structure.field_236376_l_;
    }

    default List<MobSpawnInfo.Spawners> getSpawnList(EntityClassification classification)
    {
        return StructureSpawnManager.getSpawnList(getStructure(), classification);
    }

    default boolean hasSpawnsFor(net.minecraft.entity.EntityClassification classification)
    {
        return StructureSpawnManager.hasSpawnsFor(getStructure(), classification);
    }
}
