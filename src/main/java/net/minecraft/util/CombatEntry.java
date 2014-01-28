package net.minecraft.util;

import net.minecraft.entity.EntityLivingBase;

public class CombatEntry
{
    private final DamageSource damageSrc;
    private final int field_94567_b;
    private final float field_94568_c;
    private final float field_94565_d;
    private final String field_94566_e;
    private final float field_94564_f;
    private static final String __OBFID = "CL_00001519";

    public CombatEntry(DamageSource par1DamageSource, int par2, float par3, float par4, String par5Str, float par6)
    {
        this.damageSrc = par1DamageSource;
        this.field_94567_b = par2;
        this.field_94568_c = par4;
        this.field_94565_d = par3;
        this.field_94566_e = par5Str;
        this.field_94564_f = par6;
    }

    // JAVADOC METHOD $$ func_94560_a
    public DamageSource getDamageSrc()
    {
        return this.damageSrc;
    }

    public float func_94563_c()
    {
        return this.field_94568_c;
    }

    public boolean func_94559_f()
    {
        return this.damageSrc.getEntity() instanceof EntityLivingBase;
    }

    public String func_94562_g()
    {
        return this.field_94566_e;
    }

    public IChatComponent func_151522_h()
    {
        return this.getDamageSrc().getEntity() == null ? null : this.getDamageSrc().getEntity().func_145748_c_();
    }

    public float func_94561_i()
    {
        return this.damageSrc == DamageSource.outOfWorld ? Float.MAX_VALUE : this.field_94564_f;
    }
}