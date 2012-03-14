package net.minecraft.src;

public class EntityDragonBase extends EntityLiving
{
    /** The maximum health of the Entity. */
    protected int maxHealth = 100;

    public EntityDragonBase(World par1World)
    {
        super(par1World);
    }

    public int getMaxHealth()
    {
        return this.maxHealth;
    }

    public boolean attackEntityFromPart(EntityDragonPart par1EntityDragonPart, DamageSource par2DamageSource, int par3)
    {
        return this.attackEntityFrom(par2DamageSource, par3);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        return false;
    }

    /**
     * Returns a super of attackEntityFrom in EntityDragonBase, because the normal attackEntityFrom is overriden
     */
    protected boolean superAttackFrom(DamageSource par1DamageSource, int par2)
    {
        return super.attackEntityFrom(par1DamageSource, par2);
    }
}
