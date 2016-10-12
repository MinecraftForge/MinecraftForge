package net.minecraftforge.fluids;

import javax.annotation.Nullable;

/**
 * Wrapper class used to encapsulate information about an IFluidTank.
 */
public final class FluidTankInfo
{
    @Nullable
    public final FluidStack fluid;
    public final int capacity;

    public FluidTankInfo(@Nullable FluidStack fluid, int capacity)
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
