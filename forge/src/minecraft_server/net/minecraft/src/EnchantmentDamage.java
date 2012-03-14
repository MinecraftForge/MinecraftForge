package net.minecraft.src;

public class EnchantmentDamage extends Enchantment
{
    private static final String[] protectionName = new String[] {"all", "undead", "arthropods"};
    private static final int[] baseEnchantability = new int[] {1, 5, 5};
    private static final int[] levelEnchantability = new int[] {16, 8, 8};
    private static final int[] thresholdEnchantability = new int[] {20, 20, 20};
    public final int damageType;

    public EnchantmentDamage(int par1, int par2, int par3)
    {
        super(par1, par2, EnumEnchantmentType.weapon);
        this.damageType = par3;
    }

    public int getMinEnchantability(int par1)
    {
        return baseEnchantability[this.damageType] + (par1 - 1) * levelEnchantability[this.damageType];
    }

    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + thresholdEnchantability[this.damageType];
    }

    public int getMaxLevel()
    {
        return 5;
    }

    public int calcModifierLiving(int par1, EntityLiving par2EntityLiving)
    {
        return this.damageType == 0 ? par1 * 3 : (this.damageType == 1 && par2EntityLiving.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD ? par1 * 4 : (this.damageType == 2 && par2EntityLiving.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD ? par1 * 4 : 0));
    }

    public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        return !(par1Enchantment instanceof EnchantmentDamage);
    }
}
