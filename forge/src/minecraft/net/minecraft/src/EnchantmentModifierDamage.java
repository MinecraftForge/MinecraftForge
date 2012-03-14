package net.minecraft.src;

final class EnchantmentModifierDamage implements IEnchantmentModifier
{
    /**
     * Used to calculate the damage modifier (extra armor) on enchantments that the player have on equipped armors.
     */
    public int damageModifier;

    /**
     * Used as parameter to calculate the damage modifier (extra armor) on enchantments that the player have on equipped
     * armors.
     */
    public DamageSource damageSource;

    private EnchantmentModifierDamage() {}

    /**
     * Generic method use to calculate modifiers of offensive or defensive enchantment values.
     */
    public void calculateModifier(Enchantment par1Enchantment, int par2)
    {
        this.damageModifier += par1Enchantment.calcModifierDamage(par2, this.damageSource);
    }

    EnchantmentModifierDamage(Empty3 par1Empty3)
    {
        this();
    }
}
