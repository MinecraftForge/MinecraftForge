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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * FluidHandlerConcatenate is a template class for concatenating multiple handlers into one.
 * If each tank is restricted to exactly one type of fluid, then use {@link FluidHandlerFluidMap} as it is more efficient.
 */
public class FluidHandlerConcatenate implements IFluidHandler
{
    protected final IFluidHandler[] subHandlers;

    public FluidHandlerConcatenate(IFluidHandler... subHandlers)
    {
        this.subHandlers = subHandlers;
    }

    public FluidHandlerConcatenate(Collection<IFluidHandler> subHandlers)
    {
        this.subHandlers = subHandlers.toArray(new IFluidHandler[subHandlers.size()]);
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        List<IFluidTankProperties> tanks = Lists.newArrayList();
        for (IFluidHandler handler : subHandlers)
        {
            Collections.addAll(tanks, handler.getTankProperties());
        }
        return tanks.toArray(new IFluidTankProperties[tanks.size()]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource == null || resource.amount <= 0)
            return 0;

        resource = resource.copy();

        int totalFillAmount = 0;
        for (IFluidHandler handler : subHandlers)
        {
            int fillAmount = handler.fill(resource, doFill);
            totalFillAmount += fillAmount;
            resource.amount -= fillAmount;
            if (resource.amount <= 0)
                break;
        }
        return totalFillAmount;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (resource == null || resource.amount <= 0)
            return null;

        resource = resource.copy();

        FluidStack totalDrained = null;
        for (IFluidHandler handler : subHandlers)
        {
            FluidStack drain = handler.drain(resource, doDrain);
            if (drain != null)
            {
                if (totalDrained == null)
                    totalDrained = drain;
                else
                    totalDrained.amount += drain.amount;

                resource.amount -= drain.amount;
                if (resource.amount <= 0)
                    break;
            }
        }
        return totalDrained;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (maxDrain == 0)
            return null;
        FluidStack totalDrained = null;
        for (IFluidHandler handler : subHandlers)
        {
            if (totalDrained == null)
            {
                totalDrained = handler.drain(maxDrain, doDrain);
                if (totalDrained != null)
                {
                    maxDrain -= totalDrained.amount;
                }
            }
            else
            {
                FluidStack copy = totalDrained.copy();
                copy.amount = maxDrain;
                FluidStack drain = handler.drain(copy, doDrain);
                if (drain != null)
                {
                    totalDrained.amount += drain.amount;
                    maxDrain -= drain.amount;
                }
            }

            if (maxDrain <= 0)
                break;
        }
        return totalDrained;
    }
}
