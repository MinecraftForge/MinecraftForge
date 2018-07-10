/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fluids.capability;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Basic {@link IFluidTankProperties} wrapper for {@link FluidTank}.
 */
public class FluidTankPropertiesWrapper implements IFluidTankProperties
{
    protected final FluidTank tank;

    public FluidTankPropertiesWrapper(FluidTank tank)
    {
        this.tank = tank;
    }

    @Nullable
    @Override
    public FluidStack getContents()
    {
        FluidStack contents = tank.getFluid();
        return contents == null ? null : contents.copy();
    }

    @Override
    public int getCapacity()
    {
        return tank.getCapacity();
    }

    @Override
    public boolean canFill()
    {
        return tank.canFill();
    }

    @Override
    public boolean canDrain()
    {
        return tank.canDrain();
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack)
    {
        return tank.canFillFluidType(fluidStack);
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack)
    {
        return tank.canDrainFluidType(fluidStack);
    }
}
