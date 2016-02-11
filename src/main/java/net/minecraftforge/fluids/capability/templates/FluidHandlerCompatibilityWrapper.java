package net.minecraftforge.fluids.capability.templates;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * FluidHandlerCompatibilityWrapper is a very simple template to automatically convert the old IFluidHandler to the new version.
 */
public class FluidHandlerCompatibilityWrapper implements IFluidHandler
{
    protected final net.minecraftforge.fluids.IFluidHandler handler;
    protected final EnumFacing side;

    public FluidHandlerCompatibilityWrapper(net.minecraftforge.fluids.IFluidHandler handler, EnumFacing side)
    {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public FluidTankInfo[] getTankInfo()
    {
        return handler.getTankInfo(side);
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
