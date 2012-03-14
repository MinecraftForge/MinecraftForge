package net.minecraft.src;

public class InventoryCrafting implements IInventory
{
    /** List of the stacks in the crafting matrix. */
    private ItemStack[] stackList;

    /** the width of the crafting inventory */
    private int inventoryWidth;

    /**
     * Class containing the callbacks for the events on_GUIClosed and on_CraftMaxtrixChanged.
     */
    private Container eventHandler;

    public InventoryCrafting(Container par1Container, int par2, int par3)
    {
        int var4 = par2 * par3;
        this.stackList = new ItemStack[var4];
        this.eventHandler = par1Container;
        this.inventoryWidth = par2;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.stackList.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return par1 >= this.getSizeInventory() ? null : this.stackList[par1];
    }

    /**
     * Returns the itemstack in the slot specified (Top left is 0, 0). Args: row, column
     */
    public ItemStack getStackInRowAndColumn(int par1, int par2)
    {
        if (par1 >= 0 && par1 < this.inventoryWidth)
        {
            int var3 = par1 + par2 * this.inventoryWidth;
            return this.getStackInSlot(var3);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "container.crafting";
    }

    /**
     * works exactly like getStackInSlot, is only used upon closing GUIs
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.stackList[par1] != null)
        {
            ItemStack var2 = this.stackList[par1];
            this.stackList[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.stackList[par1] != null)
        {
            ItemStack var3;

            if (this.stackList[par1].stackSize <= par2)
            {
                var3 = this.stackList[par1];
                this.stackList[par1] = null;
                this.eventHandler.onCraftMatrixChanged(this);
                return var3;
            }
            else
            {
                var3 = this.stackList[par1].splitStack(par2);

                if (this.stackList[par1].stackSize == 0)
                {
                    this.stackList[par1] = null;
                }

                this.eventHandler.onCraftMatrixChanged(this);
                return var3;
            }
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
        this.stackList[par1] = par2ItemStack;
        this.eventHandler.onCraftMatrixChanged(this);
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
