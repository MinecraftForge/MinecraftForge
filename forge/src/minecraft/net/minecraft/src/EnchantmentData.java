package net.minecraft.src;

public class EnchantmentData extends WeightedRandomChoice
{
    /** Enchantment object associated with this EnchantmentData */
    public final Enchantment enchantmentobj;

    /** Enchantment level associated with this EnchantmentData */
    public final int enchantmentLevel;

    public EnchantmentData(Enchantment par1Enchantment, int par2)
    {
        super(par1Enchantment.getWeight());
        this.enchantmentobj = par1Enchantment;
        this.enchantmentLevel = par2;
    }
}
