package net.minecraftforge.fluids;

/**
 * A tank is the unit of interaction with Fluid inventories.
 * 
 * A reference implementation can be found at {@link FluidTank}.
 * 
 * @author King Lemming, cpw (ILiquidTank)
 * 
 */
public interface IFluidTank
{
    /**
     * @return FluidStack representing the fluid in the tank, null if the tank is empty.
     */
    FluidStack getFluid();

    /**
     * @return Current amount of fluid in the tank.
     */
    int getFluidAmount();

    /**
     * @return Capacity of this fluid tank.
     */
    int getCapacity();

    /**
     * Returns a wrapper object {@link FluidTankInfo } containing the capacity of the tank and the
     * FluidStack it holds.
     * 
     * Should prevent manipulation of the IFluidTank. See {@link FluidTank}.
     * 
     * @return State information for the IFluidTank.
     */
    FluidTankInfo getInfo();

    /**
     * 
     * @param resource
     *            FluidStack attempting to fill the tank.
     * @param doFill
     *            If false, the fill will only be simulated.
     * @return Amount of fluid that was accepted by the tank.
     */
    int fill(FluidStack resource, boolean doFill);

    /**
     * 
     * @param maxDrain
     *            Maximum amount of fluid to be removed from the container.
     * @param doFill
     *            If false, the fill will only be simulated.
     * @return Amount of fluid that was removed from the tank.
     */
    FluidStack drain(int maxDrain, boolean doDrain);
}
