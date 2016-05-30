package net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Wrapper to handle {@link IFluidBlock} as an IFluidHandler
 */
public class FluidBlockWrapper implements IFluidHandler
{
	protected final IFluidBlock fluidBlock;
	protected final World world;
	protected final BlockPos blockPos;

	public FluidBlockWrapper(IFluidBlock fluidBlock, World world, BlockPos blockPos)
	{
		this.fluidBlock = fluidBlock;
		this.world = world;
		this.blockPos = blockPos;
	}

	@Override
	public FluidTankInfo[] getTankInfo()
	{
		float percentFilled = fluidBlock.getFilledPercentage(world, blockPos);
		if (percentFilled < 0)
		{
			percentFilled *= -1;
		}
		int amountFilled = Math.round(Fluid.BUCKET_VOLUME * percentFilled);
		FluidStack fluid = amountFilled > 0 ? new FluidStack(fluidBlock.getFluid(), amountFilled) : null;
		return new FluidTankInfo[]{ new FluidTankInfo(fluid, Fluid.BUCKET_VOLUME)};
	}

	@Override
	public int fill(FluidStack resource, boolean doFill)
	{
		return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain)
	{
		if (resource == null || !fluidBlock.canDrain(world, blockPos))
		{
			return null;
		}

		FluidStack simulatedDrain = fluidBlock.drain(world, blockPos, false);
		if (resource.containsFluid(simulatedDrain))
		{
			if (doDrain)
			{
				return fluidBlock.drain(world, blockPos, true);
			}
			else
			{
				return simulatedDrain;
			}
		}

		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain)
	{
		if (maxDrain <= 0 || !fluidBlock.canDrain(world, blockPos))
		{
			return null;
		}

		FluidStack simulatedDrain = fluidBlock.drain(world, blockPos, false);
		if (simulatedDrain != null && simulatedDrain.amount <= maxDrain)
		{
			if (doDrain)
			{
				return fluidBlock.drain(world, blockPos, true);
			}
			else
			{
				return simulatedDrain;
			}
		}

		return null;
	}
}
