package net.minecraftforge.liquids;

public interface ILiquidTank {

	/**
	 * @return LiquidStack representing the liquid contained in the tank, null if empty.
	 */
	LiquidStack getLiquid();
	void setLiquid(LiquidStack liquid);
	void setCapacity(int capacity);
	int getCapacity();

	/**
	 * 
	 * @param resource
	 * @param doFill
	 * @return Amount of liquid used for filling.
	 */
	int fill(LiquidStack resource, boolean doFill);
	/**
	 * 
	 * @param maxDrain
	 * @param doDrain
	 * @return Null if nothing was drained, otherwise a LiquidStack containing the drained.
	 */
	LiquidStack drain(int maxDrain, boolean doDrain);
}
