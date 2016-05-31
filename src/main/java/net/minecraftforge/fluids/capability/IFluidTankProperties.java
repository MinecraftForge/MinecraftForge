package net.minecraftforge.fluids.capability;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;

/**
 * Simplified Read-only Information about an IFluidTank.
 * This is useful for displaying information about a tank, and as hints for interacting with it.
 * Except for fluid contents, these properties are constant and do not depend on the actual state of the tank.
 *
 * The information here may not tell the full story of how the tank actually works,
 * for real fluid transactions you must use {@link IFluidHandler} to simulate, check, and then interact.
 *
 * Nothing contained in these properties is required for successfully interacting with a {@link IFluidHandler}.
 */
public interface IFluidTankProperties
{
	/**
	 * @return A copy of the fluid contents of this tank. May be null.
	 * To modify the contents, use {@link IFluidHandler}.
	 */
	@Nullable
	FluidStack getContents();

	/**
	 * @return The maximum amount of fluid this tank can hold, in millibuckets.
	 */
	int getCapacity();

	/**
	 * Returns true if the tank can be filled with this type of fluid.
	 * Used as a filter for fluid types.
	 *
	 * Does not consider the current contents or capacity of the tank,
	 * only whether it could ever fill with this type of fluid.
	 */
	boolean canFillFluidType(FluidStack fluidStack);

	/**
	 * Returns true if the tank can drain out this type of fluid.
	 * Used as a filter for fluid types.
	 *
	 * Does not consider the current contents or capacity of the tank,
	 * only whether it could ever drain out this type of fluid.
	 */
	boolean canDrainFluidType(FluidStack fluidStack);

	/**
	 * @see TankInteractionType
	 */
	TankInteractionType getInteractionType();

}
