package net.minecraft.src;

public class EntityAICreeperSwell extends EntityAIBase
{
    EntityCreeper field_48237_a;
    EntityLiving field_48236_b;

    public EntityAICreeperSwell(EntityCreeper par1EntityCreeper)
    {
        this.field_48237_a = par1EntityCreeper;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.field_48237_a.func_48094_aS();
        return this.field_48237_a.getCreeperState() > 0 || var1 != null && this.field_48237_a.getDistanceSqToEntity(var1) < 9.0D;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48237_a.getNavigator().func_48672_f();
        this.field_48236_b = this.field_48237_a.func_48094_aS();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48236_b = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.field_48236_b == null)
        {
            this.field_48237_a.setCreeperState(-1);
        }
        else if (this.field_48237_a.getDistanceSqToEntity(this.field_48236_b) > 49.0D)
        {
            this.field_48237_a.setCreeperState(-1);
        }
        else if (!this.field_48237_a.func_48090_aM().canSee(this.field_48236_b))
        {
            this.field_48237_a.setCreeperState(-1);
        }
        else
        {
            this.field_48237_a.setCreeperState(1);
        }
    }
}
