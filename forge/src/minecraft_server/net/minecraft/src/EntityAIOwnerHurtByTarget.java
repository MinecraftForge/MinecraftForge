package net.minecraft.src;

public class EntityAIOwnerHurtByTarget extends EntityAITarget
{
    EntityTameable field_48294_a;
    EntityLiving field_48293_b;

    public EntityAIOwnerHurtByTarget(EntityTameable par1EntityTameable)
    {
        super(par1EntityTameable, 32.0F, false);
        this.field_48294_a = par1EntityTameable;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48294_a.isTamed())
        {
            return false;
        }
        else
        {
            EntityLiving var1 = this.field_48294_a.getOwner();

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_48293_b = var1.getAITarget();
                return this.func_48284_a(this.field_48293_b, false);
            }
        }
    }

    public void startExecuting()
    {
        this.field_48291_c.func_48327_b(this.field_48293_b);
        super.startExecuting();
    }
}
