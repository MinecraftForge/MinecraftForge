package net.minecraft.src;

public class EntityAIBeg extends EntityAIBase
{
    private EntityWolf field_48147_a;
    private EntityPlayer field_48145_b;
    private World field_48146_c;
    private float field_48143_d;
    private int field_48144_e;

    public EntityAIBeg(EntityWolf par1EntityWolf, float par2)
    {
        this.field_48147_a = par1EntityWolf;
        this.field_48146_c = par1EntityWolf.worldObj;
        this.field_48143_d = par2;
        this.setMutexBits(2);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        this.field_48145_b = this.field_48146_c.getClosestPlayerToEntity(this.field_48147_a, (double)this.field_48143_d);
        return this.field_48145_b == null ? false : this.func_48142_a(this.field_48145_b);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48145_b.isEntityAlive() ? false : (this.field_48147_a.getDistanceSqToEntity(this.field_48145_b) > (double)(this.field_48143_d * this.field_48143_d) ? false : this.field_48144_e > 0 && this.func_48142_a(this.field_48145_b));
    }

    public void startExecuting()
    {
        this.field_48147_a.func_48378_e(true);
        this.field_48144_e = 40 + this.field_48147_a.getRNG().nextInt(40);
    }

    public void resetTask()
    {
        this.field_48147_a.func_48378_e(false);
        this.field_48145_b = null;
    }

    public void updateTask()
    {
        this.field_48147_a.getLookHelper().setLookPosition(this.field_48145_b.posX, this.field_48145_b.posY + (double)this.field_48145_b.getEyeHeight(), this.field_48145_b.posZ, 10.0F, (float)this.field_48147_a.getVerticalFaceSpeed());
        --this.field_48144_e;
    }

    private boolean func_48142_a(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();
        return var2 == null ? false : (!this.field_48147_a.isTamed() && var2.itemID == Item.bone.shiftedIndex ? true : this.field_48147_a.isWheat(var2));
    }
}
