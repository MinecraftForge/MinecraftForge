package net.minecraftforge.ingredients.capability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.ingredients.capability.templates.IngredientHandlerItemStack;

/**
 * A simple ingredient container to mimic the functionality of
 * {@link net.minecraftforge.fluids.capability.ItemFluidContainer}
 * This container may be set so it can only be completely filled or empty.
 * It also may be set to be consumed when emptied.
 * */
public class ItemIngredientContainer extends Item
{
    protected final int capacity;

    /***
     * @param capacity
     *      The maximum capacity of the container
     */
    public ItemIngredientContainer(int capacity)
    {
        this.capacity = capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new IngredientHandlerItemStack(stack, capacity);
    }
}
