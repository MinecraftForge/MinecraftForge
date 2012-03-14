package net.minecraft.src;

public interface IInventory
{
    /**
     * Returns the number of slots in the inventory.
     */
    int getSizeInventory();

    /**
     * Returns the stack in slot i
     */
    ItemStack getStackInSlot(int var1);

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    ItemStack decrStackSize(int var1, int var2);

    /**
     * works exactly like getStackInSlot, is only used upon closing GUIs
     */
    ItemStack getStackInSlotOnClosing(int var1);

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    void setInventorySlotContents(int var1, ItemStack var2);

    /**
     * Returns the name of the inventory.
     */
    String getInvName();

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    int getInventoryStackLimit();

    /**
     * Called when an the contents of an Inventory change, usually
     */
    void onInventoryChanged();

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    boolean isUseableByPlayer(EntityPlayer var1);

    void openChest();

    void closeChest();
}
