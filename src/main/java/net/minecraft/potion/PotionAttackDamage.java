package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class PotionAttackDamage extends Potion
{
    private static final String __OBFID = "CL_00001525";

    protected PotionAttackDamage(int par1, boolean par2, int par3)
    {
        super(par1, par2, par3);
    }

    public double func_111183_a(int par1, AttributeModifier par2AttributeModifier)
    {
        return this.id == Potion.weakness.id ? (double)(-0.5F * (float)(par1 + 1)) : 1.3D * (double)(par1 + 1);
    }
}