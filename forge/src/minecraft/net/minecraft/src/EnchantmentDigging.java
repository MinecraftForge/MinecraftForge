package net.minecraft.src;

public class EnchantmentDigging extends Enchantment
{
    protected EnchantmentDigging(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.digger);
        this.setName("digging");
    }

    /**
     * Returns the minimal value of enchantability nedded on the enchantment level passed.
     */
    public int getMinEnchantability(int par1)
    {
        return 1 + 15 * (par1 - 1);
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 5;
    }
}
