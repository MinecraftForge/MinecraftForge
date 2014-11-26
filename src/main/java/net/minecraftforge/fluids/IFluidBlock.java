package net.minecraftforge.fluids;

import net.minecraft.util.BlockPos;
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
    FluidStack drain(World world, BlockPos pos, boolean doDrain);

    /**
     * Check to see if a block can be drained. This method should be called by devices such as
     * pumps.
     *
     * @param doDrain
     *            If false, the drain will only be simulated.
     * @return
     */
    boolean canDrain(World world, BlockPos pos);

    /**
     * Returns the amount of a single block is filled. Value between 0 and 1.
     * 1 meaning the entire 1x1x1 cube is full, 0 meaning completely empty.
     *
     * If the return value is negative. It will be treated as filling the block
     * from the top down instead of bottom up.
     *
     * @return
     */
    float getFilledPercentage(World world, BlockPos pos);
}