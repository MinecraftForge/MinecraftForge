package net.minecraft.src;

public class EntityAIRestrictSun extends EntityAIBase
{
    private EntityCreature field_48235_a;

    public EntityAIRestrictSun(EntityCreature par1EntityCreature)
    {
        this.field_48235_a = par1EntityCreature;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.field_48235_a.worldObj.isDaytime();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48235_a.getNavigator().func_48680_d(true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48235_a.getNavigator().func_48680_d(false);
    }
}
