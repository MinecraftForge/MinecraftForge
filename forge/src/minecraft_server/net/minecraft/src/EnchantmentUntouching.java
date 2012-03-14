package net.minecraft.src;

public class EnchantmentUntouching extends Enchantment
{
    protected EnchantmentUntouching(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.digger);
        this.setName("untouching");
    }

    public int getMinEnchantability(int par1)
    {
        return 25;
    }

    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    public int getMaxLevel()
    {
        return 1;
    }

    public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != fortune.effectId;
    }
}
