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

package net.minecraftforge.fluids.capability.templates;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

import static net.minecraftforge.fluids.capability.templates.EmptyFluidHandler.EMPTY_TANK_INFO;
import static net.minecraftforge.fluids.capability.templates.EmptyFluidHandler.EMPTY_TANK_PROPERTIES_ARRAY;

/**
 * VoidFluidHandler is a template fluid handler that can be filled indefinitely without ever getting full.
 * It does not store fluid that gets filled into it, but "destroys" it upon receiving it.
 */
public class VoidFluidHandler implements IFluidHandler, IFluidTank
{
    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

    public VoidFluidHandler() {}

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return EMPTY_TANK_PROPERTIES_ARRAY;
    }

    @Override
    @Nullable
    public FluidStack getFluid()
    {
        return null;
    }

    @Override
    public int getFluidAmount()
    {
        return 0;
    }

    @Override
    public int getCapacity()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return EMPTY_TANK_INFO;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return resource.amount;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return null;
    }
}
