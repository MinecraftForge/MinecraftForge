package net.minecraft.src;

public class InventoryCraftResult implements IInventory
{
    /** A list of one item containing the result of the crafting formula */
    private ItemStack[] stackResult = new ItemStack[1];

    public int getSizeInventory()
    {
        return 1;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.stackResult[par1];
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "Result";
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.stackResult[par1] != null)
        {
            ItemStack var3 = this.stackResult[par1];
            this.stackResult[par1] = null;
            return var3;
        }
        else
        {
            return null;
        }
    }

    /**
     * works exactly like getStackInSlot, is only used upon closing GUIs
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.stackResult[par1] != null)
        {
            ItemStack var2 = this.stackResult[par1];
            this.stackResult[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.stackResult[par1] = par2ItemStack;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged() {}

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    public void openChest() {}

    public void closeChest() {}
}
