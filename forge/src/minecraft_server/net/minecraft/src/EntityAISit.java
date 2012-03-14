package net.minecraft.src;

public class EntityAISit extends EntityAIBase
{
    private EntityTameable field_48212_a;
    private boolean field_48211_b = false;

    public EntityAISit(EntityTameable par1EntityTameable)
    {
        this.field_48212_a = par1EntityTameable;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48212_a.isTamed())
        {
            return false;
        }
        else if (this.field_48212_a.isInWater())
        {
            return false;
        }
        else if (!this.field_48212_a.onGround)
        {
            return false;
        }
        else
        {
            EntityLiving var1 = this.field_48212_a.getOwner();
            return var1 == null ? true : (this.field_48212_a.getDistanceSqToEntity(var1) < 144.0D && var1.getAITarget() != null ? false : this.field_48211_b);
        }
    }

    public void startExecuting()
    {
        this.field_48212_a.getNavigator().func_48662_f();
        this.field_48212_a.func_48369_c(true);
    }

    public void resetTask()
    {
        this.field_48212_a.func_48369_c(false);
    }

    public void func_48210_a(boolean par1)
    {
        this.field_48211_b = par1;
    }
}
