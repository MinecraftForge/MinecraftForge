package net.minecraft.src;

public class EntityAIOpenDoor extends EntityAIDoorInteract
{
    boolean field_48328_i;
    int field_48327_j;

    public EntityAIOpenDoor(EntityLiving par1EntityLiving, boolean par2)
    {
        super(par1EntityLiving);
        this.field_48325_a = par1EntityLiving;
        this.field_48328_i = par2;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48328_i && this.field_48327_j > 0 && super.continueExecuting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48327_j = 20;
        this.field_48322_e.onPoweredBlockChange(this.field_48325_a.worldObj, this.field_48323_b, this.field_48324_c, this.field_48321_d, true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        if (this.field_48328_i)
        {
            this.field_48322_e.onPoweredBlockChange(this.field_48325_a.worldObj, this.field_48323_b, this.field_48324_c, this.field_48321_d, false);
        }
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        --this.field_48327_j;
        super.updateTask();
    }
}
