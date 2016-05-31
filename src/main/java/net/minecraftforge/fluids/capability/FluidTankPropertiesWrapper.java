package net.minecraftforge.fluids.capability;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Basic {@link IFluidTankProperties} wrapper for {@link FluidTank}.
 */
public class FluidTankPropertiesWrapper implements IFluidTankProperties
{
	private final FluidTank tank;
	private final TankInteractionType interactionType;

	public FluidTankPropertiesWrapper(FluidTank tank)
	{
		this(tank, TankInteractionType.OPEN);
	}

	public FluidTankPropertiesWrapper(FluidTank tank, TankInteractionType interactionType)
	{
		this.tank = tank;
		this.interactionType = interactionType;
	}

	@Nullable
	@Override
	public FluidStack getContents()
	{
		FluidStack contents = tank.getFluid();
		return contents == null ? null : contents.copy();
	}

	@Override
	public int getCapacity()
	{
		return tank.getCapacity();
	}

	public TankInteractionType getInteractionType()
	{
		return interactionType;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluidStack)
	{
		return tank.canFillFluidType(fluidStack);
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluidStack)
	{
		return tank.canDrainFluidType(fluidStack);
	}
}
