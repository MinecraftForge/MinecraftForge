package net.minecraft.src;

public class SlotFurnace extends Slot
{
    private EntityPlayer thePlayer;
    private int field_48419_f;

    public SlotFurnace(EntityPlayer par1EntityPlayer, IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.thePlayer = par1EntityPlayer;
    }

    /**
     * Check if the stack is a valid item for this slot.
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
            this.field_48419_f += Math.min(par1, this.getStack().stackSize);
        }

        return super.decrStackSize(par1);
    }

    /**
     * Called when the player picks up an item from an inventory slot
     */
    public void onPickupFromSlot(ItemStack par1ItemStack)
    {
        this.func_48416_b(par1ItemStack);
        super.onPickupFromSlot(par1ItemStack);
    }

    protected void func_48415_a(ItemStack par1ItemStack, int par2)
    {
        this.field_48419_f += par2;
        this.func_48416_b(par1ItemStack);
    }

    protected void func_48416_b(ItemStack par1ItemStack)
    {
        par1ItemStack.func_48584_a(this.thePlayer.worldObj, this.thePlayer, this.field_48419_f);
        this.field_48419_f = 0;

        if (par1ItemStack.itemID == Item.ingotIron.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.acquireIron, 1);
        }

        if (par1ItemStack.itemID == Item.fishCooked.shiftedIndex)
        {
            this.thePlayer.addStat(AchievementList.cookFish, 1);
        }

        ModLoader.takenFromFurnace(this.thePlayer, par1ItemStack);
    }
}
