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
     * Attempts to drain the block. This method should be called by devices that 
     * desire to drain the fluid block.
     * 
     * NOTE: The block is intended to handle its own state changes.
     * 
     * @param doDrain
     *             If false, the draining of the block will only be simulated.
     * @return
     */
    FluidStack drain(World world, int x, int y, int z, boolean doDrain);

    /**
     * Check to see if a block can be drained. This method should be called by 
     * devices that try to drain the fluid block
     * 
     * @return
     *             True if the block can be drained
     */
    boolean canDrain(World world, int x, int y, int z);
    
    /**
    * Attempts to fill the block. This method should be called by devices that 
    * desire to fill the fluid block.
    *
    * NOTE: The block is intended to handle its own state changes.
    *
    * @param doFill
    *            If false, the filling of the block will only be simulated.
    * @return
    *            Amount of fluid that was filled into the block
    */
    int fill(World world, int x, int y, int z, FluidStack receivingStack, boolean doFill);

   /**
    * Check to see if a block can be filled. This method should be called by devices that desire to 
    * fill the fluid block
    *
    * @return
    *          True if the block can be filled
    */
   boolean canFill(World world, int x, int y, int z);
}
