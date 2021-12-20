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

package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.block.spreader.SpreadBehaviors;
import net.minecraftforge.common.block.spreader.SpreaderType;

import java.util.Random;

public interface IForgeSpreadingBlock
{
    SpreaderType getSpreadingType(BlockState state);

    default void spread(BlockState state, ServerLevel level, BlockPos pos, Random random, int tries, int range)
    {
        if (!level.isAreaLoaded(pos, range + 1)) return;
        range = (range * 2) + 1;
        for (int i = 0; i < tries; ++i)
        {
            BlockPos blockpos = pos.offset(random.nextInt(range) - 1, random.nextInt(5) - 3, random.nextInt(range) - 1);
            BlockState targetState = level.getBlockState(blockpos);
            if (SpreadBehaviors.canSpread(targetState, getSpreadingType(state)))
            {
                level.setBlockAndUpdate(blockpos, SpreadBehaviors.getSpreadState(targetState, level, pos, getSpreadingType(state)));
            }
        }
    }
}
