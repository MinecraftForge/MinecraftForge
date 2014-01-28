package net.minecraft.enchantment;

public class EnchantmentKnockback extends Enchantment
{
    private static final String __OBFID = "CL_00000118";

    protected EnchantmentKnockback(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.weapon);
        this.setName("knockback");
    }

    // JAVADOC METHOD $$ func_77321_a
    public int getMinEnchantability(int par1)
    {
        return 5 + 20 * (par1 - 1);
    }

    // JAVADOC METHOD $$ func_77317_b
    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    // JAVADOC METHOD $$ func_77325_b
    public int getMaxLevel()
    {
        return 2;
    }
}