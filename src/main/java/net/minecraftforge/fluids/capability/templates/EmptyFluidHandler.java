package net.minecraftforge.fluids.capability.templates;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class EmptyFluidHandler implements IFluidHandler, IFluidTank
{
    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();
    private static final FluidTankInfo EMPTY_TANK_INFO = new FluidTankInfo(null, 0);
    private static final FluidTankInfo[] EMPTY_TANK_INFO_ARRAY = new FluidTankInfo[] { EMPTY_TANK_INFO };

    @Override
    public FluidTankInfo[] getTankInfo()
    {
        return EMPTY_TANK_INFO_ARRAY;
    }

    @Override
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
