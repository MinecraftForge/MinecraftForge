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

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class EmptyFluidHandler implements IFluidHandler, IFluidTank
{
    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();
    public static final FluidTankInfo EMPTY_TANK_INFO = new FluidTankInfo(null, 0);
    public static final IFluidTankProperties EMPTY_TANK_PROPERTIES = new FluidTankProperties(null, 0, false, false);
    public static final IFluidTankProperties[] EMPTY_TANK_PROPERTIES_ARRAY = new IFluidTankProperties[] { EMPTY_TANK_PROPERTIES };

    protected EmptyFluidHandler() {}

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
        return 0;
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return EMPTY_TANK_INFO;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return 0;
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
