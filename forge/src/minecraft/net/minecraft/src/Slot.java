package net.minecraft.src;

public class Slot
{
    /** The index of the slot in the inventory. */
    private final int slotIndex;

    /** The inventory we want to extract a slot from. */
    public final IInventory inventory;
    public int slotNumber;

    /** display position of the inventory slot on the screen x axis */
    public int xDisplayPosition;

    /** display position of the inventory slot on the screen y axis */
    public int yDisplayPosition;

    public Slot(IInventory par1IInventory, int par2, int par3, int par4)
    {
        this.inventory = par1IInventory;
        this.slotIndex = par2;
        this.xDisplayPosition = par3;
        this.yDisplayPosition = par4;
    }

    public void func_48433_a(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        if (par1ItemStack != null && par2ItemStack != null)
        {
            if (par1ItemStack.itemID == par2ItemStack.itemID)
            {
                int var3 = par2ItemStack.stackSize - par1ItemStack.stackSize;

                if (var3 > 0)
                {
                    this.func_48435_a(par1ItemStack, var3);
                }
            }
        }
    }

    protected void func_48435_a(ItemStack par1ItemStack, int par2) {}

    protected void func_48434_c(ItemStack par1ItemStack) {}

    /**
     * Called when the player picks up an item from an inventory slot
     */
    public void onPickupFromSlot(ItemStack par1ItemStack)
    {
        this.onSlotChanged();
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return true;
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    public ItemStack getStack()
    {
        return this.inventory.getStackInSlot(this.slotIndex);
    }

    /**
     * Returns if this slot contains a stack.
     */
    public boolean getHasStack()
    {
        return this.getStack() != null;
    }

    /**
     * Helper fnct to put a stack in the slot.
     */
    public void putStack(ItemStack par1ItemStack)
    {
        this.inventory.setInventorySlotContents(this.slotIndex, par1ItemStack);
        this.onSlotChanged();
    }

    /**
     * Called when the stack in a Slot changes
     */
    public void onSlotChanged()
    {
        this.inventory.onInventoryChanged();
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return this.inventory.getInventoryStackLimit();
    }

    /**
     * Returns the icon index on items.png that is used as background image of the slot.
     */
    public int getBackgroundIconIndex()
    {
        return -1;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int par1)
    {
        return this.inventory.decrStackSize(this.slotIndex, par1);
    }
}
