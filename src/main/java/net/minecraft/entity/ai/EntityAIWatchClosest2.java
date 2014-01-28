package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIWatchClosest2 extends EntityAIWatchClosest
{
    private static final String __OBFID = "CL_00001590";

    public EntityAIWatchClosest2(EntityLiving par1EntityLiving, Class par2Class, float par3, float par4)
    {
        super(par1EntityLiving, par2Class, par3, par4);
        this.setMutexBits(3);
    }
}