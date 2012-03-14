package net.minecraft.src;

public class EnchantmentKnockback extends Enchantment
{
    protected EnchantmentKnockback(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.weapon);
        this.setName("knockback");
    }

    public int getMinEnchantability(int par1)
    {
        return 5 + 20 * (par1 - 1);
    }

    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    public int getMaxLevel()
    {
        return 2;
    }
}
