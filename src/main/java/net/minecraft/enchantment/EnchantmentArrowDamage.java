package net.minecraft.enchantment;

public class EnchantmentArrowDamage extends Enchantment
{
    private static final String __OBFID = "CL_00000098";

    public EnchantmentArrowDamage(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowDamage");
    }

    // JAVADOC METHOD $$ func_77321_a
    public int getMinEnchantability(int par1)
    {
        return 1 + (par1 - 1) * 10;
    }

    // JAVADOC METHOD $$ func_77317_b
    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + 15;
    }

    // JAVADOC METHOD $$ func_77325_b
    public int getMaxLevel()
    {
        return 5;
    }
}