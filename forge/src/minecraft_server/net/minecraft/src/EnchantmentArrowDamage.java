package net.minecraft.src;

public class EnchantmentArrowDamage extends Enchantment
{
    public EnchantmentArrowDamage(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowDamage");
    }

    public int getMinEnchantability(int par1)
    {
        return 1 + (par1 - 1) * 10;
    }

    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + 15;
    }

    public int getMaxLevel()
    {
        return 5;
    }
}
