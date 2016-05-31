package net.minecraftforge.fluids.capability;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

/**
 * Basic implementation of {@link IFluidTankProperties}.
 */
public class FluidTankProperties implements IFluidTankProperties
{
	public static FluidTankProperties[] convert(FluidTankInfo[] fluidTankInfos)
	{
		FluidTankProperties[] properties = new FluidTankProperties[fluidTankInfos.length];
		for (int i = 0; i < fluidTankInfos.length; i++) {
			FluidTankInfo info = fluidTankInfos[i];
			properties[i] = new FluidTankProperties(info.fluid, info.capacity, TankInteractionType.OPEN);
		}
		return properties;
	}

	@Nullable
	private final FluidStack contents;
	private final int capacity;
	private final TankInteractionType interactionType;

	public FluidTankProperties(@Nullable FluidStack contents, int capacity)
	{
		this(contents, capacity, TankInteractionType.OPEN);
	}

	public FluidTankProperties(@Nullable FluidStack contents, int capacity, TankInteractionType interactionType)
	{
		this.contents = contents;
		this.capacity = capacity;
		this.interactionType = interactionType;
	}

	@Nullable
	@Override
	public FluidStack getContents() {
		return contents == null ? null : contents.copy();
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	public TankInteractionType getInteractionType() {
		return interactionType;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluidStack) {
		return true;
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluidStack) {
		return true;
	}
}
