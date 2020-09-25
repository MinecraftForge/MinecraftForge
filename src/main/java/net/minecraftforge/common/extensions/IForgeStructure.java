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

import java.util.List;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;

//TODO: Docs
public interface IForgeStructure
{

    default Structure<?> getStructure()
    {
        return (Structure<?>) this;
    }

    boolean hasSpawnsFor(EntityClassification classification);

    List<MobSpawnInfo.Spawners> getSpawnList(EntityClassification classification);

    default boolean isPositionInBoundsForSpawning(StructureManager structureManager, BlockPos pos)
    {
        Structure<?> structure = getStructure();
        if (structure == Structure.field_236374_j_ || structure == Structure.field_236378_n_) //Swamp Hut or Nether Fortress
            return structureManager.func_235010_a_(pos, true, structure).isValid();
        else if (structure == Structure.field_236366_b_ || structure == Structure.field_236376_l_) //Pillager Outpost or Ocean Monument
            return structureManager.func_235010_a_(pos, false, structure).isValid();
        return false;
    }
}