package net.minecraftforge.liquids;

/**
 * A tank is the unit of interaction with liquid inventories.
 *
 * @author cpw
 */
public interface ILiquidTank {

	/**
	 * @return LiquidStack representing the liquid contained in the tank, null if empty.
	 */
	LiquidStack getLiquid();

	/**
	 * These shouldn't be used to interact with a foreign tank. Use {@link #fill(LiquidStack, boolean)}
	 * and {@link #drain(int, boolean)}.
	 *
	 * @param liquid
	 */
	@Deprecated
	void setLiquid(LiquidStack liquid);
	/**
	 * This method should not be used to interact with a foreign tank. Use {@link #fill(LiquidStack, boolean)}
	 * and {@link #drain(int, boolean)}.
	 *
	 * @param capacity
	 */
	@Deprecated
	void setCapacity(int capacity);

	/**
	 * @return capacity of this tank
	 */
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

	/**
	 * Positive values indicate a positive liquid pressure (liquid wants to leave this tank)
	 * Negative values indicate a negative liquid pressure (liquid wants to fill this tank)
	 * Zero indicates no pressure
	 *
	 * @return a number indicating tank pressure
	 */
	public int getTankPressure();

}
