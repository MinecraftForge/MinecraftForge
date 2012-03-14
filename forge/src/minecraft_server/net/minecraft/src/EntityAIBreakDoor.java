package net.minecraft.src;

public class EntityAIBreakDoor extends EntityAIDoorInteract
{
    private int field_48194_i;

    public EntityAIBreakDoor(EntityLiving par1EntityLiving)
    {
        super(par1EntityLiving);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return !super.shouldExecute() ? false : !this.field_48189_e.func_48135_d(this.field_48192_a.worldObj, this.field_48190_b, this.field_48191_c, this.field_48188_d);
    }

    public void startExecuting()
    {
        super.startExecuting();
        this.field_48194_i = 240;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        double var1 = this.field_48192_a.getDistanceSq((double)this.field_48190_b, (double)this.field_48191_c, (double)this.field_48188_d);
        return this.field_48194_i >= 0 && !this.field_48189_e.func_48135_d(this.field_48192_a.worldObj, this.field_48190_b, this.field_48191_c, this.field_48188_d) && var1 < 4.0D;
    }

    public void updateTask()
    {
        super.updateTask();

        if (this.field_48192_a.getRNG().nextInt(20) == 0)
        {
            this.field_48192_a.worldObj.playAuxSFX(1010, this.field_48190_b, this.field_48191_c, this.field_48188_d, 0);
        }

        if (--this.field_48194_i == 0 && this.field_48192_a.worldObj.difficultySetting == 3)
        {
            this.field_48192_a.worldObj.setBlockWithNotify(this.field_48190_b, this.field_48191_c, this.field_48188_d, 0);
            this.field_48192_a.worldObj.playAuxSFX(1012, this.field_48190_b, this.field_48191_c, this.field_48188_d, 0);
            this.field_48192_a.worldObj.playAuxSFX(2001, this.field_48190_b, this.field_48191_c, this.field_48188_d, this.field_48189_e.blockID);
        }
    }
}
