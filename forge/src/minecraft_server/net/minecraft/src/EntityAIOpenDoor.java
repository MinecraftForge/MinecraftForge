package net.minecraft.src;

public class EntityAIOpenDoor extends EntityAIDoorInteract
{
    boolean field_48196_i;
    int field_48195_j;

    public EntityAIOpenDoor(EntityLiving par1EntityLiving, boolean par2)
    {
        super(par1EntityLiving);
        this.field_48192_a = par1EntityLiving;
        this.field_48196_i = par2;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48196_i && this.field_48195_j > 0 && super.continueExecuting();
    }

    public void startExecuting()
    {
        this.field_48195_j = 20;
        this.field_48189_e.onPoweredBlockChange(this.field_48192_a.worldObj, this.field_48190_b, this.field_48191_c, this.field_48188_d, true);
    }

    public void resetTask()
    {
        if (this.field_48196_i)
        {
            this.field_48189_e.onPoweredBlockChange(this.field_48192_a.worldObj, this.field_48190_b, this.field_48191_c, this.field_48188_d, false);
        }
    }

    public void updateTask()
    {
        --this.field_48195_j;
        super.updateTask();
    }
}
