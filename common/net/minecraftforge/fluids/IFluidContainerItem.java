package net.minecraftforge.fluids;

import net.minecraft.item.ItemStack;

/**
 * Implement this interface on Item classes that support external manipulation of their internal
 * fluid storage.
 * 
 * A reference implementation is provided {@link ItemFluidContainer}.
 * 
 * NOTE: Use of NBT data on the containing ItemStack is encouraged.
 * 
 * @author King Lemming
 * 
 */
public interface IFluidContainerItem
{
    /**
     * 
     * @param container
     *            ItemStack which is the fluid container.
     * @return FluidStack representing the fluid in the container, null if the container is empty.
     */
    FluidStack getFluid(ItemStack container);

    /**
     * 
     * @param container
     *            ItemStack which is the fluid container.
     * @return Capacity of this fluid container.
     */
    int getCapacity(ItemStack container);

    /**
     * 
     * @param container
     *            ItemStack which is the fluid container.
     * @param resource
     *            FluidStack attempting to fill the container.
     * @param doFill
     *            If false, the fill will only be simulated.
     * @return Amount of fluid that was (or would have been, if simulated) filled into the
     *         container.
     */
    int fill(ItemStack container, FluidStack resource, boolean doFill);

    /**
     * 
     * @param container
     *            ItemStack which is the fluid container.
     * @param maxDrain
     *            Maximum amount of fluid to be removed from the container.
     * @param doFill
     *            If false, the drain will only be simulated.
     * @return Amount of fluid that was (or would have been, if simulated) drained from the
     *         container.
     */
    FluidStack drain(ItemStack container, int maxDrain, boolean doDrain);
}
