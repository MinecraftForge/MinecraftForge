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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidBlockWrapper implements IFluidHandler
{
    protected final IFluidBlock fluidBlock;
    protected final World world;
    protected final BlockPos blockPos;

    public FluidBlockWrapper(IFluidBlock fluidBlock, World world, BlockPos blockPos)
    {
        this.fluidBlock = fluidBlock;
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
        return tank == 0 ? fluidBlock.drain(world, blockPos, FluidAction.SIMULATE) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        FluidStack stored = getFluidInTank(tank);
        if (!stored.isEmpty())
        {
            float filledPercentage = fluidBlock.getFilledPercentage(world, blockPos);
            if (filledPercentage > 0)
            {
                return (int) (stored.getAmount() / filledPercentage);
            }
        }
        return FluidAttributes.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
    {
        return stack.getFluid() == fluidBlock.getFluid();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return fluidBlock.place(world, blockPos, resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (!resource.isEmpty() && fluidBlock.canDrain(world, blockPos) && resource.getFluid() == fluidBlock.getFluid())
        {
            FluidStack simulatedDrained = fluidBlock.drain(world, blockPos, FluidAction.SIMULATE);
            if (simulatedDrained.getAmount() <= resource.getAmount() && resource.isFluidEqual(simulatedDrained))
            {
                if (action.execute())
                {
                    return fluidBlock.drain(world, blockPos, FluidAction.EXECUTE).copy();
                }
                return simulatedDrained.copy();
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (maxDrain <= 0 && fluidBlock.canDrain(world, blockPos))
        {
            FluidStack simulatedDrained = fluidBlock.drain(world, blockPos, FluidAction.SIMULATE);
            if (simulatedDrained.getAmount() <= maxDrain)
            {
                if (action.execute())
                {
                    return fluidBlock.drain(world, blockPos, FluidAction.EXECUTE).copy();
                }
                return simulatedDrained.copy();
            }
        }
        return FluidStack.EMPTY;
    }
}
