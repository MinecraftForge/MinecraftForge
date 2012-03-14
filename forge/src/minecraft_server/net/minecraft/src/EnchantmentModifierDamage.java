package net.minecraft.src;

final class EnchantmentModifierDamage implements IEnchantmentModifier
{
    public int damageModifier;
    public DamageSource damageSource;

    private EnchantmentModifierDamage() {}

    public void calculateModifier(Enchantment par1Enchantment, int par2)
    {
        this.damageModifier += par1Enchantment.calcModifierDamage(par2, this.damageSource);
    }

    EnchantmentModifierDamage(Empty3 par1Empty3)
    {
        this();
    }
}
