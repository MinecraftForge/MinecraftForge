package net.minecraft.src;

public class EnchantmentArrowKnockback extends Enchantment
{
    public EnchantmentArrowKnockback(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowKnockback");
    }

    /**
     * Returns the minimal value of enchantability nedded on the enchantment level passed.
     */
    public int getMinEnchantability(int par1)
    {
        return 12 + (par1 - 1) * 20;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + 25;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 2;
    }
}
