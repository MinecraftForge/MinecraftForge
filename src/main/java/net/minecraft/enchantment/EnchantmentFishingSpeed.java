package net.minecraft.enchantment;

public class EnchantmentFishingSpeed extends Enchantment
{
    private static final String __OBFID = "CL_00000117";

    protected EnchantmentFishingSpeed(int p_i45361_1_, int p_i45361_2_, EnumEnchantmentType p_i45361_3_)
    {
        super(p_i45361_1_, p_i45361_2_, p_i45361_3_);
        this.setName("fishingSpeed");
    }

    // JAVADOC METHOD $$ func_77321_a
    public int getMinEnchantability(int par1)
    {
        return 15 + (par1 - 1) * 9;
    }

    // JAVADOC METHOD $$ func_77317_b
    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    // JAVADOC METHOD $$ func_77325_b
    public int getMaxLevel()
    {
        return 3;
    }
}