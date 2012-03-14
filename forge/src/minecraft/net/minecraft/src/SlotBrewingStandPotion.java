package net.minecraft.src;

class SlotBrewingStandPotion extends Slot
{
    /** The player that has this container open. */
    private EntityPlayer player;

    /** The brewing stand this slot belongs to. */
    final ContainerBrewingStand container;

    public SlotBrewingStandPotion(ContainerBrewingStand par1ContainerBrewingStand, EntityPlayer par2EntityPlayer, IInventory par3IInventory, int par4, int par5, int par6)
    {
        super(par3IInventory, par4, par5, par6);
        this.container = par1ContainerBrewingStand;
        this.player = par2EntityPlayer;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return par1ItemStack != null && (par1ItemStack.itemID == Item.potion.shiftedIndex || par1ItemStack.itemID == Item.glassBottle.shiftedIndex);
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 1;
    }

    /**
     * Called when the player picks up an item from an inventory slot
     */
    public void onPickupFromSlot(ItemStack par1ItemStack)
    {
        if (par1ItemStack.itemID == Item.potion.shiftedIndex && par1ItemStack.getItemDamage() > 0)
        {
            this.player.addStat(AchievementList.potion, 1);
        }

        super.onPickupFromSlot(par1ItemStack);
    }
}
