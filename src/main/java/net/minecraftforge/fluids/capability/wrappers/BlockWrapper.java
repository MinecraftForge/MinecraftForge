/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.VoidFluidHandler;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * Wrapper around any block, only accounts for fluid placement, otherwise the block acts a void.
 * If the block in question inherits from the default Vanilla or Forge implementations,
 * consider using {@link BlockLiquidWrapper} or {@link FluidBlockWrapper} respectively.
 */
public class BlockWrapper extends VoidFluidHandler
{
    protected final BlockState state;
    protected final World world;
    protected final BlockPos blockPos;

    public BlockWrapper(BlockState state, World world, BlockPos blockPos)
    {
        this.state = state;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        // NOTE: "Filling" means placement in this context!
        if (resource.getAmount() < FluidAttributes.BUCKET_VOLUME)
        {
            return 0;
        }
        if (action.execute())
        {
            FluidUtil.destroyBlockOnFluidPlacement(world, blockPos);
            world.setBlock(blockPos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
        return FluidAttributes.BUCKET_VOLUME;
    }

    public static class LiquidContainerBlockWrapper extends VoidFluidHandler
    {
        protected final ILiquidContainer liquidContainer;
        protected final World world;
        protected final BlockPos blockPos;

        public LiquidContainerBlockWrapper(ILiquidContainer liquidContainer, World world, BlockPos blockPos)
        {
            this.liquidContainer = liquidContainer;
            this.world = world;
            this.blockPos = blockPos;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            // NOTE: "Filling" means placement in this context!
            if (resource.getAmount() >= FluidAttributes.BUCKET_VOLUME)
            {
                BlockState state = world.getBlockState(blockPos);
                if (liquidContainer.canPlaceLiquid(world, blockPos, state, resource.getFluid()))
                {
                    //If we are executing try to actually fill the container, if it failed return that we failed
                    if (action.simulate() || liquidContainer.placeLiquid(world, blockPos, state, resource.getFluid().getAttributes().getStateForPlacement(world, blockPos, resource)))
                    {
                        return FluidAttributes.BUCKET_VOLUME;
                    }
                }
            }
            return 0;
        }
    }
}
