package net.minecraftforge.items;

import net.minecraft.item.ItemStack;

public interface IItemHandlerModifiable extends IItemHandler
{
    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be null)
     * @throws RuntimeException if the handler is called in a way that the handler
     * was not expecting.
     **/
    void setStackInSlot(int slot, ItemStack stack);
}
