package net.minecraftforge.fluids.capability.templates;

import com.google.common.collect.Lists;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
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
    public FluidTankInfo[] getTankInfo()
    {
        List<FluidTankInfo> tanks = Lists.newArrayList();
        for (IFluidHandler handler : subHandlers)
        {
            Collections.addAll(tanks, handler.getTankInfo());
        }
        return tanks.toArray(new FluidTankInfo[tanks.size()]);
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
