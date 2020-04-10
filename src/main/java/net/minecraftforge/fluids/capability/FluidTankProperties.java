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

package net.minecraftforge.fluids.capability;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

/**
 * Basic implementation of {@link IFluidTankProperties}.
 */
public class FluidTankProperties implements IFluidTankProperties
{
    public static FluidTankProperties[] convert(FluidTankInfo[] fluidTankInfos)
    {
        FluidTankProperties[] properties = new FluidTankProperties[fluidTankInfos.length];
        for (int i = 0; i < fluidTankInfos.length; i++)
        {
            FluidTankInfo info = fluidTankInfos[i];
            properties[i] = new FluidTankProperties(info.fluid, info.capacity);
        }
        return properties;
    }

    @Nullable
    private final FluidStack contents;
    private final int capacity;
    private final boolean canFill;
    private final boolean canDrain;

    public FluidTankProperties(@Nullable FluidStack contents, int capacity)
    {
        this(contents, capacity, true, true);
    }

    public FluidTankProperties(@Nullable FluidStack contents, int capacity, boolean canFill, boolean canDrain)
    {
        this.contents = contents;
        this.capacity = capacity;
        this.canFill = canFill;
        this.canDrain = canDrain;
    }

    @Nullable
    @Override
    public FluidStack getContents()
    {
        return contents == null ? null : contents.copy();
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public boolean canFill()
    {
        return canFill;
    }

    @Override
    public boolean canDrain()
    {
        return canDrain;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack)
    {
        return canFill;
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack)
    {
        return canDrain;
    }
}
