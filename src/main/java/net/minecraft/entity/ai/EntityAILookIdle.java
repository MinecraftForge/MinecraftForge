package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAILookIdle extends EntityAIBase
{
    // JAVADOC FIELD $$ field_75258_a
    private EntityLiving idleEntity;
    // JAVADOC FIELD $$ field_75256_b
    private double lookX;
    // JAVADOC FIELD $$ field_75257_c
    private double lookZ;
    // JAVADOC FIELD $$ field_75255_d
    private int idleTime;
    private static final String __OBFID = "CL_00001607";

    public EntityAILookIdle(EntityLiving par1EntityLiving)
    {
        this.idleEntity = par1EntityLiving;
        this.setMutexBits(3);
    }

    // JAVADOC METHOD $$ func_75250_a
    public boolean shouldExecute()
    {
        return this.idleEntity.getRNG().nextFloat() < 0.02F;
    }

    // JAVADOC METHOD $$ func_75253_b
    public boolean continueExecuting()
    {
        return this.idleTime >= 0;
    }

    // JAVADOC METHOD $$ func_75249_e
    public void startExecuting()
    {
        double d0 = (Math.PI * 2D) * this.idleEntity.getRNG().nextDouble();
        this.lookX = Math.cos(d0);
        this.lookZ = Math.sin(d0);
        this.idleTime = 20 + this.idleEntity.getRNG().nextInt(20);
    }

    // JAVADOC METHOD $$ func_75246_d
    public void updateTask()
    {
        --this.idleTime;
        this.idleEntity.getLookHelper().setLookPosition(this.idleEntity.posX + this.lookX, this.idleEntity.posY + (double)this.idleEntity.getEyeHeight(), this.idleEntity.posZ + this.lookZ, 10.0F, (float)this.idleEntity.getVerticalFaceSpeed());
    }
}