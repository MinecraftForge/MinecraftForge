package net.minecraft.src;

public class EntityAISit extends EntityAIBase
{
    private EntityTameable field_48409_a;
    private boolean field_48408_b = false;

    public EntityAISit(EntityTameable par1EntityTameable)
    {
        this.field_48409_a = par1EntityTameable;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48409_a.isTamed())
        {
            return false;
        }
        else if (this.field_48409_a.isInWater())
        {
            return false;
        }
        else if (!this.field_48409_a.onGround)
        {
            return false;
        }
        else
        {
            EntityLiving var1 = this.field_48409_a.getOwner();
            return var1 == null ? true : (this.field_48409_a.getDistanceSqToEntity(var1) < 144.0D && var1.getAITarget() != null ? false : this.field_48408_b);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48409_a.getNavigator().func_48672_f();
        this.field_48409_a.func_48140_f(true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48409_a.func_48140_f(false);
    }

    public void func_48407_a(boolean par1)
    {
        this.field_48408_b = par1;
    }
}
