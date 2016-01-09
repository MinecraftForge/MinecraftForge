package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IItemHandler {
    /**
     * Returns the number of slots available on a given side.
     * Slots on different sides are not necessarily the same slots.
     *
     * @param side Orientation being queried.
     * @return The number of slots available on the side.
     **/
    int getSlots(EnumFacing side);

    /**
     * Returns the ItemStack in a given slot on the given side.
     * The resultant value's stacksize may be greater than the stacks max size.
     *
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY
     *
     * @param slot Slot to query
     * @param side Orientation being queried.
     * @return ItemStack in slot on the side. May be null.
     **/
    ItemStack getStackInSlot(int slot, EnumFacing side);

    /**
     * Inserts an ItemStack into the given slot on the given side and return the remainder.
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param side     Orientation the ItemStack is inserted from.
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return null)
     **/
    ItemStack insertItem(int slot, ItemStack stack, EnumFacing side, boolean simulate);

    /**
     * Extracts an ItemStack from the given slot on the given side. This must respect max stack-size.
     *
     * @param side     Orientation the ItemStack is extracted from.
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stacks max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot (should be null, if nothing can be extracted)
     **/
    ItemStack extractItem(int slot, int amount, EnumFacing side, boolean simulate);
}
