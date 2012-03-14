package net.minecraft.src;

public class SlotFurnace extends Slot
{
    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer thePlayer;
    private int field_48437_f;

    public SlotFurnace(EntityPlayer par1EntityPlayer, IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.thePlayer = par1EntityPlayer;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int par1)
    {
        if (this.getHasStack())
        {
            this.field_48437_f += Math.min(par1, this.getStack().stackSize);
        }

        return super.decrStackSize(par1);
    }

    /**
     * Called when the player picks up an item from an inventory slot
     */
    public void onPickupFromSlot(ItemStack par1ItemStack)
    {
        this.func_48434_c(par1ItemStack);
        super.onPickupFromSlot(par1ItemStack);
    }

    protected void func_48435_a(ItemStack par1ItemStack, int par2)
    {
        this.field_48437_f += par2;
        this.func_48434_c(par1ItemStack);
    }

    protected void func_48434_c(ItemStack par1ItemStack)
    {
        par1ItemStack.func_48507_a(this.thePlayer.worldObj, this.thePlayer, this.field_48437_f);
        this.field_48437_f = 0;
        ModLoader.takenFromFurnace(this.thePlayer, par1ItemStack);

        if (par1ItemStack.itemID == Item.ingotIron.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.acquireIron, 1);
        }

        if (par1ItemStack.itemID == Item.fishCooked.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.cookFish, 1);
        }
    }
}
