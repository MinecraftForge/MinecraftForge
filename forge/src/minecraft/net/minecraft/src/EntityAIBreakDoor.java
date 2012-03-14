package net.minecraft.src;

public class EntityAIBreakDoor extends EntityAIDoorInteract
{
    private int field_48329_i;

    public EntityAIBreakDoor(EntityLiving par1EntityLiving)
    {
        super(par1EntityLiving);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return !super.shouldExecute() ? false : !this.field_48322_e.func_48213_h(this.field_48325_a.worldObj, this.field_48323_b, this.field_48324_c, this.field_48321_d);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
        this.field_48329_i = 240;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        double var1 = this.field_48325_a.getDistanceSq((double)this.field_48323_b, (double)this.field_48324_c, (double)this.field_48321_d);
        return this.field_48329_i >= 0 && !this.field_48322_e.func_48213_h(this.field_48325_a.worldObj, this.field_48323_b, this.field_48324_c, this.field_48321_d) && var1 < 4.0D;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        super.updateTask();

        if (this.field_48325_a.getRNG().nextInt(20) == 0)
        {
            this.field_48325_a.worldObj.playAuxSFX(1010, this.field_48323_b, this.field_48324_c, this.field_48321_d, 0);
        }

        if (--this.field_48329_i == 0 && this.field_48325_a.worldObj.difficultySetting == 3)
        {
            this.field_48325_a.worldObj.setBlockWithNotify(this.field_48323_b, this.field_48324_c, this.field_48321_d, 0);
            this.field_48325_a.worldObj.playAuxSFX(1012, this.field_48323_b, this.field_48324_c, this.field_48321_d, 0);
            this.field_48325_a.worldObj.playAuxSFX(2001, this.field_48323_b, this.field_48324_c, this.field_48321_d, this.field_48322_e.blockID);
        }
    }
}
