package net.minecraft.src;

public class EnchantmentDigging extends Enchantment
{
    protected EnchantmentDigging(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.digger);
        this.setName("digging");
    }

    public int getMinEnchantability(int par1)
    {
        return 1 + 15 * (par1 - 1);
    }

    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    public int getMaxLevel()
    {
        return 5;
    }
}
