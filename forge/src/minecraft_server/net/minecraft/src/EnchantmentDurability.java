package net.minecraft.src;

public class EnchantmentDurability extends Enchantment
{
    protected EnchantmentDurability(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.digger);
        this.setName("durability");
    }

    public int getMinEnchantability(int par1)
    {
        return 5 + (par1 - 1) * 10;
    }

    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    public int getMaxLevel()
    {
        return 3;
    }
}
