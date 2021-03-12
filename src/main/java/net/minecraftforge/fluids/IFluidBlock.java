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

package net.minecraftforge.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
     * Attempts to place the block at a given position. The placed block's level will correspond
     * to the provided fluid amount.
     * This method should be called by fluid containers such as buckets, but it is recommended
     * to use {@link FluidUtil}.
     *
     * @param world      the world to place the block in
     * @param pos        the position to place the block at
     * @param fluidStack the fluid stack to get the required data from
     * @param action     If SIMULATE, the placement will only be simulated
     * @return the amount of fluid extracted from the provided stack to achieve some fluid level
     */
    int place(World world, BlockPos pos, @Nonnull FluidStack fluidStack, IFluidHandler.FluidAction action);

    /**
     * Attempt to drain the block. This method should be called by devices such as pumps.
     *
     * NOTE: The block is intended to handle its own state changes.
     *
     * @param action
     *            If SIMULATE, the drain will only be simulated.
     * @return
     */
    @Nonnull
    FluidStack drain(World world, BlockPos pos, IFluidHandler.FluidAction action);

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
