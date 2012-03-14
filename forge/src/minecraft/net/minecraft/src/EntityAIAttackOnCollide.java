package net.minecraft.src;

public class EntityAIAttackOnCollide extends EntityAIBase
{
    World worldObj;
    EntityLiving field_48267_b;
    EntityLiving entityTarget;
    int field_46091_d;
    float field_48266_e;
    boolean field_48264_f;
    PathEntity field_48265_g;
    Class field_48268_h;
    private int field_48269_i;

    public EntityAIAttackOnCollide(EntityLiving par1EntityLiving, Class par2Class, float par3, boolean par4)
    {
        this(par1EntityLiving, par3, par4);
        this.field_48268_h = par2Class;
    }

    public EntityAIAttackOnCollide(EntityLiving par1EntityLiving, float par2, boolean par3)
    {
        this.field_46091_d = 0;
        this.field_48267_b = par1EntityLiving;
        this.worldObj = par1EntityLiving.worldObj;
        this.field_48266_e = par2;
        this.field_48264_f = par3;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.field_48267_b.func_48094_aS();

        if (var1 == null)
        {
            return false;
        }
        else if (this.field_48268_h != null && !this.field_48268_h.isAssignableFrom(var1.getClass()))
        {
            return false;
        }
        else
        {
            this.entityTarget = var1;
            this.field_48265_g = this.field_48267_b.getNavigator().func_48679_a(this.entityTarget);
            return this.field_48265_g != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        EntityLiving var1 = this.field_48267_b.func_48094_aS();
        return var1 == null ? false : (!this.entityTarget.isEntityAlive() ? false : (!this.field_48264_f ? !this.field_48267_b.getNavigator().noPath() : this.field_48267_b.isWithinHomeDistance(MathHelper.floor_double(this.entityTarget.posX), MathHelper.floor_double(this.entityTarget.posY), MathHelper.floor_double(this.entityTarget.posZ))));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48267_b.getNavigator().func_48678_a(this.field_48265_g, this.field_48266_e);
        this.field_48269_i = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.entityTarget = null;
        this.field_48267_b.getNavigator().func_48672_f();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.field_48267_b.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);

        if ((this.field_48264_f || this.field_48267_b.func_48090_aM().canSee(this.entityTarget)) && --this.field_48269_i <= 0)
        {
            this.field_48269_i = 4 + this.field_48267_b.getRNG().nextInt(7);
            this.field_48267_b.getNavigator().func_48667_a(this.entityTarget, this.field_48266_e);
        }

        this.field_46091_d = Math.max(this.field_46091_d - 1, 0);
        double var1 = (double)(this.field_48267_b.width * 2.0F * this.field_48267_b.width * 2.0F);

        if (this.field_48267_b.getDistanceSq(this.entityTarget.posX, this.entityTarget.boundingBox.minY, this.entityTarget.posZ) <= var1)
        {
            if (this.field_46091_d <= 0)
            {
                this.field_46091_d = 20;
                this.field_48267_b.attackEntityAsMob(this.entityTarget);
            }
        }
    }
}
