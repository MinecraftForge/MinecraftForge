package net.minecraft.src;

public class EnchantmentWaterWorker extends Enchantment
{
    public EnchantmentWaterWorker(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.armor_head);
        this.setName("waterWorker");
    }

    public int getMinEnchantability(int par1)
    {
        return 1;
    }

    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + 40;
    }

    public int getMaxLevel()
    {
        return 1;
    }
}
