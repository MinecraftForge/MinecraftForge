package net.minecraft.src;

public class EntityAIOwnerHurtByTarget extends EntityAITarget
{
    EntityTameable field_48394_a;
    EntityLiving field_48393_b;

    public EntityAIOwnerHurtByTarget(EntityTameable par1EntityTameable)
    {
        super(par1EntityTameable, 32.0F, false);
        this.field_48394_a = par1EntityTameable;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48394_a.isTamed())
        {
            return false;
        }
        else
        {
            EntityLiving var1 = this.field_48394_a.getOwner();

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_48393_b = var1.getAITarget();
                return this.func_48376_a(this.field_48393_b, false);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48382_c.func_48092_c(this.field_48393_b);
        super.startExecuting();
    }
}
