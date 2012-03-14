package net.minecraft.src;

class SlotBrewingStandIngredient extends Slot
{
    /** The brewing stand this slot belongs to. */
    final ContainerBrewingStand container;

    public SlotBrewingStandIngredient(ContainerBrewingStand par1ContainerBrewingStand, IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.container = par1ContainerBrewingStand;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return par1ItemStack != null ? Item.itemsList[par1ItemStack.itemID].isPotionIngredient() : false;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 64;
    }
}
