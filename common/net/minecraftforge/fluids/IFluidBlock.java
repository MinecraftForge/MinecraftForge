package net.minecraftforge.fluids;

import net.minecraft.world.World;

/**
 * Implement this interface on Block classes which represent world-placeable Fluids.
 * 
 * NOTE: Using/extending the reference implementations {@link BlockFluidBase} is encouraged.
 * 
 * @author King Lemming
 * 
 */
public interface IFluidBlock
{
    /**
     * Returns the Fluid associated with this Block.
     */
    Fluid getFluid();

    /**
     * Attempt to drain the block. This method should be called by devices such as pumps.
     * 
     * NOTE: The block is intended to handle its own state changes.
     * 
     * @param doDrain
     *            If false, the drain will only be simulated.
     * @return
     */
    FluidStack drain(World world, int x, int y, int z, boolean doDrain);

    /**
     * Check to see if a block can be drained. This method should be called by devices such as
     * pumps.
     * 
     * @param doDrain
     *            If false, the drain will only be simulated.
     * @return
     */
    boolean canDrain(World world, int x, int y, int z);
}
