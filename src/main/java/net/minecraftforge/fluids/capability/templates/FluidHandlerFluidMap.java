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

package net.minecraftforge.fluids.capability.templates;

import com.google.common.collect.Lists;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.*;

/**
 * FluidHandlerFluidMap is a template class for concatenating multiple handlers into one,
 * where each handler is associated with a different fluid.
 */
public class FluidHandlerFluidMap implements IFluidHandler
{
    protected final Map<Fluid, IFluidHandler> handlers;

    public FluidHandlerFluidMap()
    {
        // LinkedHashMap to ensure iteration order is consistent.
        this(new LinkedHashMap<Fluid, IFluidHandler>());
    }

    public FluidHandlerFluidMap(Map<Fluid, IFluidHandler> handlers)
    {
        this.handlers = handlers;
    }

    public FluidHandlerFluidMap addHandler(Fluid fluid, IFluidHandler handler)
    {
        handlers.put(fluid, handler);
        return this;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        List<IFluidTankProperties> tanks = Lists.newArrayList();
        for (IFluidHandler iFluidHandler : handlers.values())
        {
            Collections.addAll(tanks, iFluidHandler.getTankProperties());
        }
        return tanks.toArray(new IFluidTankProperties[tanks.size()]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource == null)
            return 0;
        IFluidHandler handler = handlers.get(resource.getFluid());
        if (handler == null)
            return 0;
        return handler.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (resource == null)
            return null;
        IFluidHandler handler = handlers.get(resource.getFluid());
        if (handler == null)
            return null;
        return handler.drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        for (IFluidHandler handler : handlers.values())
        {
            FluidStack drain = handler.drain(maxDrain, doDrain);
            if (drain != null)
                return drain;
        }
        return null;
    }
}
