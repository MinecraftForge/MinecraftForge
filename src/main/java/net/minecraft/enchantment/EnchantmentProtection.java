package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class EnchantmentProtection extends Enchantment
{
    // JAVADOC FIELD $$ field_77354_A
    private static final String[] protectionName = new String[] {"all", "fire", "fall", "explosion", "projectile"};
    // JAVADOC FIELD $$ field_77355_B
    private static final int[] baseEnchantability = new int[] {1, 10, 5, 5, 3};
    // JAVADOC FIELD $$ field_77357_C
    private static final int[] levelEnchantability = new int[] {11, 8, 6, 8, 6};
    // JAVADOC FIELD $$ field_77353_D
    private static final int[] thresholdEnchantability = new int[] {20, 12, 10, 12, 15};
    // JAVADOC FIELD $$ field_77356_a
    public final int protectionType;
    private static final String __OBFID = "CL_00000121";

    public EnchantmentProtection(int par1, int par2, int par3)
    {
        super(par1, par2, EnumEnchantmentType.armor);
        this.protectionType = par3;

        if (par3 == 2)
        {
            this.type = EnumEnchantmentType.armor_feet;
        }
    }

    // JAVADOC METHOD $$ func_77321_a
    public int getMinEnchantability(int par1)
    {
        return baseEnchantability[this.protectionType] + (par1 - 1) * levelEnchantability[this.protectionType];
    }

    // JAVADOC METHOD $$ func_77317_b
    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + thresholdEnchantability[this.protectionType];
    }

    // JAVADOC METHOD $$ func_77325_b
    public int getMaxLevel()
    {
        return 4;
    }

    // JAVADOC METHOD $$ func_77318_a
    public int calcModifierDamage(int par1, DamageSource par2DamageSource)
    {
        if (par2DamageSource.canHarmInCreative())
        {
            return 0;
        }
        else
        {
            float f = (float)(6 + par1 * par1) / 3.0F;
            return this.protectionType == 0 ? MathHelper.floor_float(f * 0.75F) : (this.protectionType == 1 && par2DamageSource.isFireDamage() ? MathHelper.floor_float(f * 1.25F) : (this.protectionType == 2 && par2DamageSource == DamageSource.fall ? MathHelper.floor_float(f * 2.5F) : (this.protectionType == 3 && par2DamageSource.isExplosion() ? MathHelper.floor_float(f * 1.5F) : (this.protectionType == 4 && par2DamageSource.isProjectile() ? MathHelper.floor_float(f * 1.5F) : 0))));
        }
    }

    // JAVADOC METHOD $$ func_77320_a
    public String getName()
    {
        return "enchantment.protect." + protectionName[this.protectionType];
    }

    // JAVADOC METHOD $$ func_77326_a
    public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        if (par1Enchantment instanceof EnchantmentProtection)
        {
            EnchantmentProtection enchantmentprotection = (EnchantmentProtection)par1Enchantment;
            return enchantmentprotection.protectionType == this.protectionType ? false : this.protectionType == 2 || enchantmentprotection.protectionType == 2;
        }
        else
        {
            return super.canApplyTogether(par1Enchantment);
        }
    }

    // JAVADOC METHOD $$ func_92093_a
    public static int getFireTimeForEntity(Entity par0Entity, int par1)
    {
        int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.fireProtection.effectId, par0Entity.getLastActiveItems());

        if (j > 0)
        {
            par1 -= MathHelper.floor_float((float)par1 * (float)j * 0.15F);
        }

        return par1;
    }

    public static double func_92092_a(Entity par0Entity, double par1)
    {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.blastProtection.effectId, par0Entity.getLastActiveItems());

        if (i > 0)
        {
            par1 -= (double)MathHelper.floor_double(par1 * (double)((float)i * 0.15F));
        }

        return par1;
    }
}