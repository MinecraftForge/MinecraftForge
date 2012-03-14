package net.minecraft.src;

public class EntityAICreeperSwell extends EntityAIBase
{
    EntityCreeper field_48244_a;
    EntityLiving field_48243_b;

    public EntityAICreeperSwell(EntityCreeper par1EntityCreeper)
    {
        this.field_48244_a = par1EntityCreeper;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.field_48244_a.func_48331_as();
        return this.field_48244_a.getCreeperState() > 0 || var1 != null && this.field_48244_a.getDistanceSqToEntity(var1) < 9.0D;
    }

    public void startExecuting()
    {
        this.field_48244_a.getNavigator().func_48662_f();
        this.field_48243_b = this.field_48244_a.func_48331_as();
    }

    public void resetTask()
    {
        this.field_48243_b = null;
    }

    public void updateTask()
    {
        if (this.field_48243_b == null)
        {
            this.field_48244_a.setCreeperState(-1);
        }
        else if (this.field_48244_a.getDistanceSqToEntity(this.field_48243_b) > 49.0D)
        {
            this.field_48244_a.setCreeperState(-1);
        }
        else if (!this.field_48244_a.func_48318_al().func_48546_a(this.field_48243_b))
        {
            this.field_48244_a.setCreeperState(-1);
        }
        else
        {
            this.field_48244_a.setCreeperState(1);
        }
    }
}
