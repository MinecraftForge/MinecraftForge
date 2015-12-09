package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IItemHandler {
    /**
     * Returns the number of slots available on a given side.
     * Slots on different side are not necessarily the same slots.
     *
     * @param side Orientation being queried.
     * @return The number of slots available on the side.
     **/
    int getSlots(EnumFacing side);

    /**
     * Returns the ItemStack in a given slot on the given side. This should not be used to manipulate contents.
     *
     * @param slot Slot to query
     * @param side Orientation being queried.
     * @return ItemStack in slot on the side. May be null.
     **/
    ItemStack getStackInSlot(int slot, EnumFacing side);

    /**
     * Inserts an ItemStack into the given slot on the given side.
     *
     * @param side     Orientation the ItemStack is inserted from.
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that could not be inserted (if the whole stack is accepted then return null)
     **/
    ItemStack insertItem(int slot, ItemStack stack, EnumFacing side, boolean simulate);

    /**
     * Inserts an ItemStack into the given slot on the given side.
     *
     * @param side     Orientation the ItemStack is extracted from.
     * @param slot     Slot to extract into.
     * @param amount   ItemStack to extract
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot
     **/
    ItemStack extractItem(int slot, int amount, EnumFacing side, boolean simulate);
}
