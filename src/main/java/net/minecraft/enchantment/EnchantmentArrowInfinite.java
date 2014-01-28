package net.minecraft.enchantment;

public class EnchantmentArrowInfinite extends Enchantment
{
    private static final String __OBFID = "CL_00000100";

    public EnchantmentArrowInfinite(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.bow);
        this.setName("arrowInfinite");
    }

    // JAVADOC METHOD $$ func_77321_a
    public int getMinEnchantability(int par1)
    {
        return 20;
    }

    // JAVADOC METHOD $$ func_77317_b
    public int getMaxEnchantability(int par1)
    {
        return 50;
    }

    // JAVADOC METHOD $$ func_77325_b
    public int getMaxLevel()
    {
        return 1;
    }
}