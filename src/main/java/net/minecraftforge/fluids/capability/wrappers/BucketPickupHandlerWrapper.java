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

import javax.annotation.Nonnull;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class BucketPickupHandlerWrapper implements IFluidHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    protected final IBucketPickupHandler bucketPickupHandler;
    protected final World world;
    protected final BlockPos blockPos;

    public BucketPickupHandlerWrapper(IBucketPickupHandler bucketPickupHandler, World world, BlockPos blockPos)
    {
        this.bucketPickupHandler = bucketPickupHandler;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int getTanks()
    {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank)
    {
        if (tank == 0)
        {
            //Best guess at stored fluid
            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty())
            {
                return new FluidStack(fluidState.getType(), FluidAttributes.BUCKET_VOLUME);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return FluidAttributes.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
    {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (!resource.isEmpty() && FluidAttributes.BUCKET_VOLUME <= resource.getAmount())
        {
            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty() && resource.getFluid() == fluidState.getType())
            {
                if (action.execute())
                {
                    Fluid fluid = bucketPickupHandler.takeLiquid(world, blockPos, world.getBlockState(blockPos));
                    if (fluid != Fluids.EMPTY)
                    {
                        FluidStack extracted = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
                        if (!resource.isFluidEqual(extracted))
                        {
                            //Be loud if something went wrong
                            LOGGER.error("Fluid removed without successfully being picked up. Fluid {} at {} in {} matched requested type, but after performing pickup was {}.",
                                  fluidState.getType().getRegistryName(), blockPos, world.dimension().location(), fluid.getRegistryName());
                            return FluidStack.EMPTY;
                        }
                        return extracted;
                    }
                }
                else
                {
                    FluidStack extracted = new FluidStack(fluidState.getType(), FluidAttributes.BUCKET_VOLUME);
                    if (resource.isFluidEqual(extracted))
                    {
                        //Validate NBT matches
                        return extracted;
                    }
                }
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (FluidAttributes.BUCKET_VOLUME <= maxDrain)
        {
            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty())
            {
                if (action.simulate())
                {
                    return new FluidStack(fluidState.getType(), FluidAttributes.BUCKET_VOLUME);
                }
                Fluid fluid = bucketPickupHandler.takeLiquid(world, blockPos, world.getBlockState(blockPos));
                if (fluid != Fluids.EMPTY)
                {
                    return new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
                }
            }
        }
        return FluidStack.EMPTY;
    }
}
