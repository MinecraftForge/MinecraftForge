package net.minecraft.src;

public class EntityAILeapAtTarget extends EntityAIBase
{
    EntityLiving field_48252_a;
    EntityLiving field_48250_b;
    float field_48251_c;

    public EntityAILeapAtTarget(EntityLiving par1EntityLiving, float par2)
    {
        this.field_48252_a = par1EntityLiving;
        this.field_48251_c = par2;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        this.field_48250_b = this.field_48252_a.func_48094_aS();

        if (this.field_48250_b == null)
        {
            return false;
        }
        else
        {
            double var1 = this.field_48252_a.getDistanceSqToEntity(this.field_48250_b);
            return var1 >= 4.0D && var1 <= 16.0D ? (!this.field_48252_a.onGround ? false : this.field_48252_a.getRNG().nextInt(5) == 0) : false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48252_a.onGround;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        double var1 = this.field_48250_b.posX - this.field_48252_a.posX;
        double var3 = this.field_48250_b.posZ - this.field_48252_a.posZ;
        float var5 = MathHelper.sqrt_double(var1 * var1 + var3 * var3);
        this.field_48252_a.motionX += var1 / (double)var5 * 0.5D * 0.800000011920929D + this.field_48252_a.motionX * 0.20000000298023224D;
        this.field_48252_a.motionZ += var3 / (double)var5 * 0.5D * 0.800000011920929D + this.field_48252_a.motionZ * 0.20000000298023224D;
        this.field_48252_a.motionY = (double)this.field_48251_c;
    }
}
