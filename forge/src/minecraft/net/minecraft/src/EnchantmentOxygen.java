package net.minecraft.src;

public class EnchantmentOxygen extends Enchantment
{
    public EnchantmentOxygen(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.armor_head);
        this.setName("oxygen");
    }

    /**
     * Returns the minimal value of enchantability nedded on the enchantment level passed.
     */
    public int getMinEnchantability(int par1)
    {
        return 10 * par1;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + 30;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 3;
    }
}
