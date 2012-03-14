package net.minecraft.src;

public class EntityAILookAtVillager extends EntityAIBase
{
    private EntityIronGolem field_48406_a;
    private EntityVillager field_48404_b;
    private int field_48405_c;

    public EntityAILookAtVillager(EntityIronGolem par1EntityIronGolem)
    {
        this.field_48406_a = par1EntityIronGolem;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48406_a.worldObj.isDaytime())
        {
            return false;
        }
        else if (this.field_48406_a.getRNG().nextInt(8000) != 0)
        {
            return false;
        }
        else
        {
            this.field_48404_b = (EntityVillager)this.field_48406_a.worldObj.findNearestEntityWithinAABB(EntityVillager.class, this.field_48406_a.boundingBox.expand(6.0D, 2.0D, 6.0D), this.field_48406_a);
            return this.field_48404_b != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48405_c > 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48405_c = 400;
        this.field_48406_a.func_48116_a(true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48406_a.func_48116_a(false);
        this.field_48404_b = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.field_48406_a.getLookHelper().setLookPositionWithEntity(this.field_48404_b, 30.0F, 30.0F);
        --this.field_48405_c;
    }
}
