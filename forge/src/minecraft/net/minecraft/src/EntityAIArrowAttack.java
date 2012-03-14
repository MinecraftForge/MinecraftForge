package net.minecraft.src;

public class EntityAIArrowAttack extends EntityAIBase
{
    World field_48373_a;

    /** The entity the AI instance has been applied to */
    EntityLiving entityHost;
    EntityLiving attackTarget;
    int field_48369_d = 0;
    float field_48370_e;
    int field_48367_f = 0;
    int field_48368_g;
    int field_48374_h;

    public EntityAIArrowAttack(EntityLiving par1EntityLiving, float par2, int par3, int par4)
    {
        this.entityHost = par1EntityLiving;
        this.field_48373_a = par1EntityLiving.worldObj;
        this.field_48370_e = par2;
        this.field_48368_g = par3;
        this.field_48374_h = par4;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.entityHost.func_48094_aS();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.attackTarget = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        double var1 = 100.0D;
        double var3 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        boolean var5 = this.entityHost.func_48090_aM().canSee(this.attackTarget);

        if (var5)
        {
            ++this.field_48367_f;
        }
        else
        {
            this.field_48367_f = 0;
        }

        if (var3 <= var1 && this.field_48367_f >= 20)
        {
            this.entityHost.getNavigator().func_48672_f();
        }
        else
        {
            this.entityHost.getNavigator().func_48667_a(this.attackTarget, this.field_48370_e);
        }

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        this.field_48369_d = Math.max(this.field_48369_d - 1, 0);

        if (this.field_48369_d <= 0)
        {
            if (var3 <= var1 && var5)
            {
                this.func_48366_h();
                this.field_48369_d = this.field_48374_h;
            }
        }
    }

    private void func_48366_h()
    {
        if (this.field_48368_g == 1)
        {
            EntityArrow var1 = new EntityArrow(this.field_48373_a, this.entityHost, this.attackTarget, 1.6F, 12.0F);
            this.field_48373_a.playSoundAtEntity(this.entityHost, "random.bow", 1.0F, 1.0F / (this.entityHost.getRNG().nextFloat() * 0.4F + 0.8F));
            this.field_48373_a.spawnEntityInWorld(var1);
        }
        else if (this.field_48368_g == 2)
        {
            EntitySnowball var9 = new EntitySnowball(this.field_48373_a, this.entityHost);
            double var2 = this.attackTarget.posX - this.entityHost.posX;
            double var4 = this.attackTarget.posY + (double)this.attackTarget.getEyeHeight() - 1.100000023841858D - var9.posY;
            double var6 = this.attackTarget.posZ - this.entityHost.posZ;
            float var8 = MathHelper.sqrt_double(var2 * var2 + var6 * var6) * 0.2F;
            var9.setThrowableHeading(var2, var4 + (double)var8, var6, 1.6F, 12.0F);
            this.field_48373_a.playSoundAtEntity(this.entityHost, "random.bow", 1.0F, 1.0F / (this.entityHost.getRNG().nextFloat() * 0.4F + 0.8F));
            this.field_48373_a.spawnEntityInWorld(var9);
        }
    }
}
