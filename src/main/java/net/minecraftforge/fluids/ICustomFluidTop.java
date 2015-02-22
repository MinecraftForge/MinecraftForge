package net.minecraftforge.fluids;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ICustomFluidTop 
{
	/**
	 * The BlockPos is ABOVE the Fluid to check if the Top is completly covered.
	 * This is used in {@link BlockForgeFluidRenderer}
	 */
	public boolean canRenderTop(IBlockAccess world, BlockPos xyz);
}
