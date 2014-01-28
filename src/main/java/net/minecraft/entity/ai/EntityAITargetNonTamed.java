package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityTameable;

public class EntityAITargetNonTamed extends EntityAINearestAttackableTarget
{
    private EntityTameable theTameable;
    private static final String __OBFID = "CL_00001623";

    public EntityAITargetNonTamed(EntityTameable par1EntityTameable, Class par2Class, int par3, boolean par4)
    {
        super(par1EntityTameable, par2Class, par3, par4);
        this.theTameable = par1EntityTameable;
    }

    // JAVADOC METHOD $$ func_75250_a
    public boolean shouldExecute()
    {
        return !this.theTameable.isTamed() && super.shouldExecute();
    }
}