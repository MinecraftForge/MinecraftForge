package net.minecraft.src;

public class EntityAIRestrictSun extends EntityAIBase
{
    private EntityCreature field_48239_a;

    public EntityAIRestrictSun(EntityCreature par1EntityCreature)
    {
        this.field_48239_a = par1EntityCreature;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.field_48239_a.worldObj.isDaytime();
    }

    public void startExecuting()
    {
        this.field_48239_a.getNavigator().func_48669_d(true);
    }

    public void resetTask()
    {
        this.field_48239_a.getNavigator().func_48669_d(false);
    }
}
