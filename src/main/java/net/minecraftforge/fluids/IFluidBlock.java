/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

/**
 * Implement this interface on Block classes which represent world-placeable Fluids.
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
     * @param level      the level to place the block in
     * @param pos        the position to place the block at
     * @param fluidStack the fluid stack to get the required data from
     * @param action     If SIMULATE, the placement will only be simulated
     * @return the amount of fluid extracted from the provided stack to achieve some fluid level
     */
    int place(Level level, BlockPos pos, @Nonnull FluidStack fluidStack, IFluidHandler.FluidAction action);

    /**
     * Attempt to drain the block. This method should be called by devices such as pumps.
     *
     * NOTE: The block is intended to handle its own state changes.
     *
     * @param action
     *            If SIMULATE, the drain will only be simulated.
     * @return the fluid stack after draining the block
     */
    @Nonnull
    FluidStack drain(Level level, BlockPos pos, IFluidHandler.FluidAction action);

    /**
     * Check to see if a block can be drained. This method should be called by devices such as
     * pumps.
     */
    boolean canDrain(Level level, BlockPos pos);

    /**
     * Returns the amount of a single block is filled. Value between 0 and 1.
     * 1 meaning the entire 1x1x1 cube is full, 0 meaning completely empty.
     *
     * If the return value is negative. It will be treated as filling the block
     * from the top down instead of bottom up.
     */
    float getFilledPercentage(Level level, BlockPos pos);
}
