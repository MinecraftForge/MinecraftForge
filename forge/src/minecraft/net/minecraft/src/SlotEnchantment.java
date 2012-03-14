package net.minecraft.src;

class SlotEnchantment extends Slot
{
    /** The brewing stand this slot belongs to. */
    final ContainerEnchantment container;

    SlotEnchantment(ContainerEnchantment par1ContainerEnchantment, IInventory par2IInventory, int par3, int par4, int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.container = par1ContainerEnchantment;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return true;
    }
}
