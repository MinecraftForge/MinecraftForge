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

package net.minecraftforge.fluids;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Implement this interface on Block classes which represent world-placeable Fluids.
 *
 * NOTE: Using/extending the reference implementations {@link BlockFluidBase} is encouraged.
 *
 */
public interface IFluidBlock
{
    /**
     * Returns the Fluid associated with this Block.
     */
    Fluid getFluid();

    /**
     * Attempt to drain the block. This method should be called by devices such as pumps.
     *
     * NOTE: The block is intended to handle its own state changes.
     *
     * @param doDrain
     *            If false, the drain will only be simulated.
     * @return
     */
    FluidStack drain(World world, BlockPos pos, boolean doDrain);

    /**
     * Check to see if a block can be drained. This method should be called by devices such as
     * pumps.
     *
     * @return
     */
    boolean canDrain(World world, BlockPos pos);

    /**
     * Returns the amount of a single block is filled. Value between 0 and 1.
     * 1 meaning the entire 1x1x1 cube is full, 0 meaning completely empty.
     *
     * If the return value is negative. It will be treated as filling the block
     * from the top down instead of bottom up.
     *
     * @return
     */
    float getFilledPercentage(World world, BlockPos pos);
}
