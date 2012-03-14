package net.minecraft.src;

public class EnchantmentLootBonus extends Enchantment
{
    protected EnchantmentLootBonus(int par1, int par2, EnumEnchantmentType par3EnumEnchantmentType)
    {
        super(par1, par2, par3EnumEnchantmentType);
        this.setName("lootBonus");

        if (par3EnumEnchantmentType == EnumEnchantmentType.digger)
        {
            this.setName("lootBonusDigger");
        }
    }

    public int getMinEnchantability(int par1)
    {
        return 20 + (par1 - 1) * 12;
    }

    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    public int getMaxLevel()
    {
        return 3;
    }

    public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != silkTouch.effectId;
    }
}
