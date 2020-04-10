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

package net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Wrapper to handle {@link IFluidBlock} as an IFluidHandler
 */
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
    public IFluidTankProperties[] getTankProperties()
    {
        float percentFilled = fluidBlock.getFilledPercentage(world, blockPos);
        if (percentFilled < 0)
        {
            percentFilled *= -1;
        }
        int amountFilled = Math.round(Fluid.BUCKET_VOLUME * percentFilled);
        FluidStack fluid = amountFilled > 0 ? new FluidStack(fluidBlock.getFluid(), amountFilled) : null;
        return new FluidTankProperties[]{ new FluidTankProperties(fluid, Fluid.BUCKET_VOLUME, false, true)};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        // NOTE: "Filling" means placement in this context!
        if (resource == null)
        {
            return 0;
        }
        return fluidBlock.place(world, blockPos, resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (resource == null || !fluidBlock.canDrain(world, blockPos))
        {
            return null;
        }

        FluidStack simulatedDrain = fluidBlock.drain(world, blockPos, false);
        if (resource.containsFluid(simulatedDrain))
        {
            if (doDrain)
            {
                return fluidBlock.drain(world, blockPos, true);
            }
            else
            {
                return simulatedDrain;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (maxDrain <= 0 || !fluidBlock.canDrain(world, blockPos))
        {
            return null;
        }

        FluidStack simulatedDrain = fluidBlock.drain(world, blockPos, false);
        if (simulatedDrain != null && simulatedDrain.amount <= maxDrain)
        {
            if (doDrain)
            {
                return fluidBlock.drain(world, blockPos, true);
            }
            else
            {
                return simulatedDrain;
            }
        }

        return null;
    }
}
