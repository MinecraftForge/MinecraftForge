package net.minecraft.src;

public class EntityDragonPart extends Entity
{
    /** The dragon entity this dragon part belongs to */
    public final EntityDragonBase entityDragonObj;

    /** The name of the Dragon Part */
    public final String name;

    public EntityDragonPart(EntityDragonBase par1EntityDragonBase, String par2Str, float par3, float par4)
    {
        super(par1EntityDragonBase.worldObj);
        this.setSize(par3, par4);
        this.entityDragonObj = par1EntityDragonBase;
        this.name = par2Str;
    }

    protected void entityInit() {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        return this.entityDragonObj.attackEntityFromPart(this, par1DamageSource, par2);
    }

    public boolean isEntityEqual(Entity par1Entity)
    {
        return this == par1Entity || this.entityDragonObj == par1Entity;
    }
}
