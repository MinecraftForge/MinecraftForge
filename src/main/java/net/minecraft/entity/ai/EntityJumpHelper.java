package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityJumpHelper
{
    private EntityLiving entity;
    private boolean isJumping;
    private static final String __OBFID = "CL_00001571";

    public EntityJumpHelper(EntityLiving par1EntityLiving)
    {
        this.entity = par1EntityLiving;
    }

    public void setJumping()
    {
        this.isJumping = true;
    }

    // JAVADOC METHOD $$ func_75661_b
    public void doJump()
    {
        this.entity.setJumping(this.isJumping);
        this.isJumping = false;
    }
}