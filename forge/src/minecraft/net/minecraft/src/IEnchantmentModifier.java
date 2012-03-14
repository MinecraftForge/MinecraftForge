package net.minecraft.src;

interface IEnchantmentModifier
{
    /**
     * Generic method use to calculate modifiers of offensive or defensive enchantment values.
     */
    void calculateModifier(Enchantment var1, int var2);
}
