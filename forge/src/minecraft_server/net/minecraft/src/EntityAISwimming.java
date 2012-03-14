package net.minecraft.src;

public class EntityAISwimming extends EntityAIBase
{
    private EntityLiving field_46105_a;

    public EntityAISwimming(EntityLiving par1EntityLiving)
    {
        this.field_46105_a = par1EntityLiving;
        this.setMutexBits(4);
        par1EntityLiving.getNavigator().func_48660_e(true);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.field_46105_a.isInWater() || this.field_46105_a.handleLavaMovement();
    }

    public void updateTask()
    {
        if (this.field_46105_a.getRNG().nextFloat() < 0.8F)
        {
            this.field_46105_a.getJumpHelper().setJumping();
        }
    }
}
