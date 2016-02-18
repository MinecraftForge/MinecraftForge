package net.minecraftforge.items;

import net.minecraft.item.ItemStack;

public interface IItemHandler
{
    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    int getSlots();

    /**
     * Returns the ItemStack in a given slot.
     *
     * The result's stack size may be greater than the itemstacks max size.
     *
     * If the result is null, then the slot is empty.
     * If the result is not null but the stack size is zero, then it represents
     * an empty slot that will only accept* a specific itemstack.
     *
     * <p/>
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. May be null.
     **/
    ItemStack getStackInSlot(int slot);

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return null).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    /**
     * Extracts an ItemStack from the given slot. The returned value must be null
     * if nothing is extracted, otherwise it's stack size must not be greater than amount or the
     * itemstacks getMaxStackSize().
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stacks max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be null, if nothing can be extracted
     **/
    ItemStack extractItem(int slot, int amount, boolean simulate);
}
