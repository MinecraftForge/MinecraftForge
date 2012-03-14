package net.minecraft.src;

public class EntityAILookIdle extends EntityAIBase
{
    private EntityLiving field_46089_a;
    private double field_46087_b;
    private double field_46088_c;
    private int field_46086_d = 0;

    public EntityAILookIdle(EntityLiving par1EntityLiving)
    {
        this.field_46089_a = par1EntityLiving;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.field_46089_a.getRNG().nextFloat() < 0.02F;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_46086_d >= 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        double var1 = (Math.PI * 2D) * this.field_46089_a.getRNG().nextDouble();
        this.field_46087_b = Math.cos(var1);
        this.field_46088_c = Math.sin(var1);
        this.field_46086_d = 20 + this.field_46089_a.getRNG().nextInt(20);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        --this.field_46086_d;
        this.field_46089_a.getLookHelper().setLookPosition(this.field_46089_a.posX + this.field_46087_b, this.field_46089_a.posY + (double)this.field_46089_a.getEyeHeight(), this.field_46089_a.posZ + this.field_46088_c, 10.0F, (float)this.field_46089_a.getVerticalFaceSpeed());
    }
}
