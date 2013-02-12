package net.minecraftforge.liquids;

import net.minecraft.item.ItemStack;

/**
 * An itemstack version of the ILiquidTank
 * Used for allowing Items to function as tanks
 *
 * @author cpw, xcompwiz
 */
public interface ILiquidContainer {

	/**
	 * @return LiquidStack representing the liquid contained in the container, null if empty.
	 */
	LiquidStack getLiquid(ItemStack itemstack);

	/**
	 * @return capacity of this container
	 */
	int getCapacity(ItemStack itemstack);

	/**
	 *
	 * @param resource
	 * @param doFill
	 * @return Amount of liquid used for filling.
	 */
	int fill(ItemStack itemstack, LiquidStack resource, boolean doFill);
	/**
	 *
	 * @param maxDrain
	 * @param doDrain
	 * @return Null if nothing was drained, otherwise a LiquidStack containing the drained.
	 */
	LiquidStack drain(ItemStack itemstack, int maxDrain, boolean doDrain);

	/**
	 * Positive values indicate a positive liquid pressure (liquid wants to leave this tank)
	 * Negative values indicate a negative liquid pressure (liquid wants to fill this tank)
	 * Zero indicates no pressure
	 *
	 * @return a number indicating container pressure
	 */
	public int getContainerPressure(ItemStack itemstack);

}
