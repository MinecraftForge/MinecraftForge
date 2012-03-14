package net.minecraft.src;

public class EntityAILookAtVillager extends EntityAIBase
{
    private EntityIronGolem field_48226_a;
    private EntityVillager field_48224_b;
    private int field_48225_c;

    public EntityAILookAtVillager(EntityIronGolem par1EntityIronGolem)
    {
        this.field_48226_a = par1EntityIronGolem;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48226_a.worldObj.isDaytime())
        {
            return false;
        }
        else if (this.field_48226_a.getRNG().nextInt(8000) != 0)
        {
            return false;
        }
        else
        {
            this.field_48224_b = (EntityVillager)this.field_48226_a.worldObj.findNearestEntityWithinAABB(EntityVillager.class, this.field_48226_a.boundingBox.expand(6.0D, 2.0D, 6.0D), this.field_48226_a);
            return this.field_48224_b != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48225_c > 0;
    }

    public void startExecuting()
    {
        this.field_48225_c = 400;
        this.field_48226_a.func_48383_a(true);
    }

    public void resetTask()
    {
        this.field_48226_a.func_48383_a(false);
        this.field_48224_b = null;
    }

    public void updateTask()
    {
        this.field_48226_a.getLookHelper().setLookPositionWithEntity(this.field_48224_b, 30.0F, 30.0F);
        --this.field_48225_c;
    }
}
