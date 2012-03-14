package net.minecraft.src;

public class EntityAILookIdle extends EntityAIBase
{
    private EntityLiving field_46114_a;
    private double field_46112_b;
    private double field_46113_c;
    private int field_46111_d = 0;

    public EntityAILookIdle(EntityLiving par1EntityLiving)
    {
        this.field_46114_a = par1EntityLiving;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.field_46114_a.getRNG().nextFloat() < 0.02F;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_46111_d >= 0;
    }

    public void startExecuting()
    {
        double var1 = (Math.PI * 2D) * this.field_46114_a.getRNG().nextDouble();
        this.field_46112_b = Math.cos(var1);
        this.field_46113_c = Math.sin(var1);
        this.field_46111_d = 20 + this.field_46114_a.getRNG().nextInt(20);
    }

    public void updateTask()
    {
        --this.field_46111_d;
        this.field_46114_a.getLookHelper().setLookPosition(this.field_46114_a.posX + this.field_46112_b, this.field_46114_a.posY + (double)this.field_46114_a.getEyeHeight(), this.field_46114_a.posZ + this.field_46113_c, 10.0F, (float)this.field_46114_a.getVerticalFaceSpeed());
    }
}
