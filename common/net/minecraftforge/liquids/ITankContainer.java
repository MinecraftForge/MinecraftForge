package net.minecraftforge.liquids;

import net.minecraftforge.common.ForgeDirection;

public interface ITankContainer {
	
	/**
	 * Fills liquid into internal tanks, distribution is left to the ITankContainer.
	 * @param from Orientation the liquid is pumped in from.
	 * @param resource LiquidStack representing the maximum amount of liquid filled into the ITankContainer
	 * @param doFill If false filling will only be simulated.
	 * @return Amount of resource that was filled into internal tanks.
	 */
	int fill(ForgeDirection from, LiquidStack resource, boolean doFill);
	/**
	 * Fills liquid into the specified internal tank. 
	 * @param from Orientation the liquid is pumped in from.
	 * @param resource LiquidStack representing the maximum amount of liquid filled into the ITankContainer
	 * @param doFill If false filling will only be simulated.
	 * @return Amount of resource that was filled into internal tanks.
	 */
	int fill(int tankIndex, LiquidStack resource, boolean doFill);

	/**
	 * Drains liquid out of internal tanks, distribution is left to the ITankContainer.
	 * @param from Orientation the liquid is drained to.
	 * @param maxDrain Maximum amount of liquid to drain.
	 * @param doDrain If false draining will only be simulated.
	 * @return LiquidStack representing the liquid and amount actually drained from the ITankContainer
	 */
	LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain);
	/**
	 * Drains liquid out of the specified internal tank.
	 * @param from Orientation the liquid is drained to.
	 * @param maxDrain Maximum amount of liquid to drain.
	 * @param doDrain If false draining will only be simulated.
	 * @return LiquidStack representing the liquid and amount actually drained from the ITankContainer
	 */
	LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain);
	
	/**
	 * @return Array of {@link LiquidTank}s contained in this ITankContainer
	 */
	ILiquidTank[] getTanks();
	
}
