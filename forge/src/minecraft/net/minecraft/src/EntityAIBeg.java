package net.minecraft.src;

public class EntityAIBeg extends EntityAIBase
{
    private EntityWolf field_48350_a;
    private EntityPlayer field_48348_b;
    private World field_48349_c;
    private float field_48346_d;
    private int field_48347_e;

    public EntityAIBeg(EntityWolf par1EntityWolf, float par2)
    {
        this.field_48350_a = par1EntityWolf;
        this.field_48349_c = par1EntityWolf.worldObj;
        this.field_48346_d = par2;
        this.setMutexBits(2);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        this.field_48348_b = this.field_48349_c.getClosestPlayerToEntity(this.field_48350_a, (double)this.field_48346_d);
        return this.field_48348_b == null ? false : this.func_48345_a(this.field_48348_b);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48348_b.isEntityAlive() ? false : (this.field_48350_a.getDistanceSqToEntity(this.field_48348_b) > (double)(this.field_48346_d * this.field_48346_d) ? false : this.field_48347_e > 0 && this.func_48345_a(this.field_48348_b));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48350_a.func_48150_h(true);
        this.field_48347_e = 40 + this.field_48350_a.getRNG().nextInt(40);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48350_a.func_48150_h(false);
        this.field_48348_b = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.field_48350_a.getLookHelper().setLookPosition(this.field_48348_b.posX, this.field_48348_b.posY + (double)this.field_48348_b.getEyeHeight(), this.field_48348_b.posZ, 10.0F, (float)this.field_48350_a.getVerticalFaceSpeed());
        --this.field_48347_e;
    }

    private boolean func_48345_a(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();
        return var2 == null ? false : (!this.field_48350_a.isTamed() && var2.itemID == Item.bone.shiftedIndex ? true : this.field_48350_a.isWheat(var2));
    }
}
