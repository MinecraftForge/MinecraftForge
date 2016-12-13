/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * This allows for mods to make their own Blocks listen to
 * updates sent to Observers.
 *
 */
public interface IObserver
{
    /**
     * Called whenever an Observer update is received.
     *
     * @param state The listening block's state.
     * @param world The current world.
     * @param pos The listening block's position.
     * @param neighborBlock The updated block.
     * @param neighborPos The updated block's position.
     */
    void neighborChangedObserver(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos);
}
