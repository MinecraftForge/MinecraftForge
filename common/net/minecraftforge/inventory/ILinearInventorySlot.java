package net.minecraftforge.inventory;

import net.minecraft.item.ItemStack;

/**
 * A slot in an ILinearInventory. This is an interface as different slots may
 * have different properties.
 */
public interface ILinearInventorySlot {
    /**
     * Gets the item stack in the slot.
     * 
     * It is not acceptable to edit the returned item stack while it is still
     * contained in this slot. Some implementations might return a defensive
     * copy, so this will have unpredictable results.
     * 
     * @return The current contents of this slot.
     */
    ItemStack getStack();

    /**
     * Sets the item stack in the slot.
     * 
     * If the stack size is larger than the slot can hold, this method returns
     * false and doesn't update the slot's contents. The slot is not required to
     * check the item's maximum stack size, only the slot's maximum stack size.
     * 
     * Slots can also refuse to accept stacks for any other reason - for example
     * they may only be able to hold certain items.
     * 
     * If is == null, this method must return true.
     * 
     * If this method returns false, the contents of the slot are unchanged.
     * 
     * @param is
     *            The new stack.
     * @param simulate
     *            If true, then don't actually update the slot - just return
     *            true or false depending on whether the set would've succeeded.
     * @return True if the slot now contains the stack (or it would've if
     *         simulate was false).
     */
    boolean setStack(ItemStack is, boolean simulate);

    /**
     * Gets the maximum size of stacks that this slot can hold.
     * 
     * @return The maximum stack size
     */
    int getMaximumStackSize();

    /**
     * Returns true if items should be extracted from this slot. This may change
     * depending on the current contents of the slot.
     * 
     * This is only a recommendation - mods may alter inventory slots in any way
     * that setStack permits.
     * 
     * @return True if items can be extracted from this slot.
     */
    boolean shouldExtractItems();

    /**
     * Returns true if the given item type should be inserted into this slot.
     * The result is not affected by the current contents of the slot.
     * 
     * This is only a recommendation - mods may alter inventory slots in any way
     * that setStack permits. However, generally this method will agree with
     * setStack.
     * 
     * @param is
     *            The item to test - stack size is ignored.
     * @return True if the slot can hold this item.
     */
    boolean shouldInsertItem(ItemStack is);
}
