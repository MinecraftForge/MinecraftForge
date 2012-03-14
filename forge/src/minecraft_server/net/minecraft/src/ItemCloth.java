package net.minecraft.src;

public class ItemCloth extends ItemBlock
{
    public ItemCloth(int par1)
    {
        super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * returns the argument if the item has metadata, 0 otherwise
     */
    public int getMetadata(int par1)
    {
        return par1;
    }

    public String getItemNameIS(ItemStack par1ItemStack)
    {
        return super.getItemName() + "." + ItemDye.dyeColorNames[BlockCloth.getBlockFromDye(par1ItemStack.getItemDamage())];
    }
}
