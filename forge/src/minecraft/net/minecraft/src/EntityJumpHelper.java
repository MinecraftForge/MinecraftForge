package net.minecraft.src;

public class EntityJumpHelper
{
    private EntityLiving entity;
    private boolean isJumping = false;

    public EntityJumpHelper(EntityLiving par1EntityLiving)
    {
        this.entity = par1EntityLiving;
    }

    public void setJumping()
    {
        this.isJumping = true;
    }

    /**
     * Called to actually make the entity jump if isJumping is true.
     */
    public void doJump()
    {
        this.entity.setJumping(this.isJumping);
        this.isJumping = false;
    }
}
