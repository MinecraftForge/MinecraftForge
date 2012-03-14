package net.minecraft.src;

public class EntityAIWatchClosest extends EntityAIBase
{
    private EntityLiving field_46110_a;
    private Entity field_48242_b;
    private float field_46106_d;
    private int field_46107_e;
    private float field_48241_e;
    private Class field_48240_f;

    public EntityAIWatchClosest(EntityLiving par1EntityLiving, Class par2Class, float par3)
    {
        this.field_46110_a = par1EntityLiving;
        this.field_48240_f = par2Class;
        this.field_46106_d = par3;
        this.field_48241_e = 0.02F;
        this.setMutexBits(2);
    }

    public EntityAIWatchClosest(EntityLiving par1EntityLiving, Class par2Class, float par3, float par4)
    {
        this.field_46110_a = par1EntityLiving;
        this.field_48240_f = par2Class;
        this.field_46106_d = par3;
        this.field_48241_e = par4;
        this.setMutexBits(2);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_46110_a.getRNG().nextFloat() >= this.field_48241_e)
        {
            return false;
        }
        else
        {
            if (this.field_48240_f == EntityPlayer.class)
            {
                this.field_48242_b = this.field_46110_a.worldObj.getClosestPlayerToEntity(this.field_46110_a, (double)this.field_46106_d);
            }
            else
            {
                this.field_48242_b = this.field_46110_a.worldObj.findNearestEntityWithinAABB(this.field_48240_f, this.field_46110_a.boundingBox.expand((double)this.field_46106_d, 3.0D, (double)this.field_46106_d), this.field_46110_a);
            }

            return this.field_48242_b != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48242_b.isEntityAlive() ? false : (this.field_46110_a.getDistanceSqToEntity(this.field_48242_b) > (double)(this.field_46106_d * this.field_46106_d) ? false : this.field_46107_e > 0);
    }

    public void startExecuting()
    {
        this.field_46107_e = 40 + this.field_46110_a.getRNG().nextInt(40);
    }

    public void resetTask()
    {
        this.field_48242_b = null;
    }

    public void updateTask()
    {
        this.field_46110_a.getLookHelper().setLookPosition(this.field_48242_b.posX, this.field_48242_b.posY + (double)this.field_48242_b.getEyeHeight(), this.field_48242_b.posZ, 10.0F, (float)this.field_46110_a.getVerticalFaceSpeed());
        --this.field_46107_e;
    }
}
