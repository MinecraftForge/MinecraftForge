package net.minecraft.src;

public class EnchantmentArrowFire extends Enchantment
{
    public EnchantmentArrowFire(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowFire");
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
