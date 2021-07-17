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

package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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
    protected final Level world;
    protected final BlockPos blockPos;

    public BlockWrapper(BlockState state, Level world, BlockPos blockPos)
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
        protected final LiquidBlockContainer liquidContainer;
        protected final Level world;
        protected final BlockPos blockPos;

        public LiquidContainerBlockWrapper(LiquidBlockContainer liquidContainer, Level world, BlockPos blockPos)
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
