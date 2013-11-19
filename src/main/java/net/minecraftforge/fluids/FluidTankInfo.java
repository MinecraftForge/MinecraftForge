package net.minecraftforge.fluids;

/**
 * Wrapper class used to encapsulate information about an IFluidTank.
 * 
 * @author King Lemming
 * 
 */
public final class FluidTankInfo
{
    public final FluidStack fluid;
    public final int capacity;

    public FluidTankInfo(FluidStack fluid, int capacity)
    {
        this.fluid = fluid;
        this.capacity = capacity;
    }

    public FluidTankInfo(IFluidTank tank)
    {
        this.fluid = tank.getFluid();
        this.capacity = tank.getCapacity();
    }
}
