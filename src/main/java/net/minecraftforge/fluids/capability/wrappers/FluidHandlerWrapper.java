/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * FluidHandlerWrapper automatically converts the old {@link net.minecraftforge.fluids.IFluidHandler} to the new version.
 * @deprecated will be removed along with {@link net.minecraftforge.fluids.IFluidHandler}
 */
@Deprecated
public class FluidHandlerWrapper implements IFluidHandler
{
    protected final net.minecraftforge.fluids.IFluidHandler handler;
    protected final EnumFacing side;

    public FluidHandlerWrapper(net.minecraftforge.fluids.IFluidHandler handler, EnumFacing side)
    {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return FluidTankProperties.convert(handler.getTankInfo(side));
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource == null || !handler.canFill(side, resource.getFluid()))
            return 0;
        return handler.fill(side, resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (resource == null || !handler.canDrain(side, resource.getFluid()))
            return null;
        return handler.drain(side, resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return handler.drain(side, maxDrain, doDrain);
    }
}
