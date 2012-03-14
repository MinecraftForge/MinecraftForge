package net.minecraft.src;

public class EntityAIOcelotAttack extends EntityAIBase
{
    World field_48171_a;
    EntityLiving field_48169_b;
    EntityLiving field_48170_c;
    int field_48168_d = 0;

    public EntityAIOcelotAttack(EntityLiving par1EntityLiving)
    {
        this.field_48169_b = par1EntityLiving;
        this.field_48171_a = par1EntityLiving.worldObj;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.field_48169_b.func_48331_as();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            this.field_48170_c = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48170_c.isEntityAlive() ? false : (this.field_48169_b.getDistanceSqToEntity(this.field_48170_c) > 225.0D ? false : !this.field_48169_b.getNavigator().noPath() || this.shouldExecute());
    }

    public void resetTask()
    {
        this.field_48170_c = null;
        this.field_48169_b.getNavigator().func_48662_f();
    }

    public void updateTask()
    {
        this.field_48169_b.getLookHelper().setLookPositionWithEntity(this.field_48170_c, 30.0F, 30.0F);
        double var1 = (double)(this.field_48169_b.width * 2.0F * this.field_48169_b.width * 2.0F);
        double var3 = this.field_48169_b.getDistanceSq(this.field_48170_c.posX, this.field_48170_c.boundingBox.minY, this.field_48170_c.posZ);
        float var5 = 0.23F;

        if (var3 > var1 && var3 < 16.0D)
        {
            var5 = 0.4F;
        }
        else if (var3 < 225.0D)
        {
            var5 = 0.18F;
        }

        this.field_48169_b.getNavigator().func_48652_a(this.field_48170_c, var5);
        this.field_48168_d = Math.max(this.field_48168_d - 1, 0);

        if (var3 <= var1)
        {
            if (this.field_48168_d <= 0)
            {
                this.field_48168_d = 20;
                this.field_48169_b.attackEntityAsMob(this.field_48170_c);
            }
        }
    }
}
