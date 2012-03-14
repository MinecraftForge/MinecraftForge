package net.minecraft.src;

public class InventoryLargeChest implements IInventory
{
    /** Name of the chest. */
    private String name;

    /** Inventory object corresponding to double chest upper part */
    private IInventory upperChest;

    /** Inventory object corresponding to double chest lower part */
    private IInventory lowerChest;

    public InventoryLargeChest(String par1Str, IInventory par2IInventory, IInventory par3IInventory)
    {
        this.name = par1Str;

        if (par2IInventory == null)
        {
            par2IInventory = par3IInventory;
        }

        if (par3IInventory == null)
        {
            par3IInventory = par2IInventory;
        }

        this.upperChest = par2IInventory;
        this.lowerChest = par3IInventory;
    }

    public int getSizeInventory()
    {
        return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return this.name;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return par1 >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlot(par1 - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(par1);
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        return par1 >= this.upperChest.getSizeInventory() ? this.lowerChest.decrStackSize(par1 - this.upperChest.getSizeInventory(), par2) : this.upperChest.decrStackSize(par1, par2);
    }

    /**
     * works exactly like getStackInSlot, is only used upon closing GUIs
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        return par1 >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlotOnClosing(par1 - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlotOnClosing(par1);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        if (par1 >= this.upperChest.getSizeInventory())
        {
            this.lowerChest.setInventorySlotContents(par1 - this.upperChest.getSizeInventory(), par2ItemStack);
        }
        else
        {
            this.upperChest.setInventorySlotContents(par1, par2ItemStack);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return this.upperChest.getInventoryStackLimit();
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        this.upperChest.onInventoryChanged();
        this.lowerChest.onInventoryChanged();
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.upperChest.isUseableByPlayer(par1EntityPlayer) && this.lowerChest.isUseableByPlayer(par1EntityPlayer);
    }

    public void openChest()
    {
        this.upperChest.openChest();
        this.lowerChest.openChest();
    }

    public void closeChest()
    {
        this.upperChest.closeChest();
        this.lowerChest.closeChest();
    }
}
