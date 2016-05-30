package net.minecraftforge.fluids.capability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

/**
 * A simple fluid container, to replace the functionality of {@link FluidContainerRegistry) and {@link IFluidContainerItem}.
 * This fluid container may be set so that is can only completely filled or empty. (binary)
 * It may also be set so that it gets consumed when it is drained. (consumable)
 */
public class ItemFluidContainer extends Item
{
    protected final int capacity;

    /**
     * @param capacity   The maximum capacity of this fluid container.
     */
    public ItemFluidContainer(int capacity)
    {
        this.capacity = capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new FluidHandlerItemStack(stack, capacity);
    }
}
