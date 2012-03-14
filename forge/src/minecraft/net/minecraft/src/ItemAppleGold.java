package net.minecraft.src;

public class ItemAppleGold extends ItemFood
{
    public ItemAppleGold(int par1, int par2, float par3, boolean par4)
    {
        super(par1, par2, par3, par4);
    }

    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return true;
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return EnumRarity.epic;
    }
}
