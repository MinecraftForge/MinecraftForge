package net.minecraft.src;

public class EnchantmentArrowInfinite extends Enchantment
{
    public EnchantmentArrowInfinite(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowInfinite");
    }

    public int getMinEnchantability(int par1)
    {
        return 20;
    }

    public int getMaxEnchantability(int par1)
    {
        return 50;
    }

    public int getMaxLevel()
    {
        return 1;
    }
}
