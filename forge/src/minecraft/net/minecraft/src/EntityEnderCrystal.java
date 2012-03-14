package net.minecraft.src;

public class EntityEnderCrystal extends Entity
{
    /** Used to create the rotation animation when rendering the crystal. */
    public int innerRotation;
    public int health;

    public EntityEnderCrystal(World par1World)
    {
        super(par1World);
        this.innerRotation = 0;
        this.preventEntitySpawning = true;
        this.setSize(2.0F, 2.0F);
        this.yOffset = this.height / 2.0F;
        this.health = 5;
        this.innerRotation = this.rand.nextInt(100000);
    }

    public EntityEnderCrystal(World par1World, double par2, double par4, double par6)
    {
        this(par1World);
        this.setPosition(par2, par4, par6);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(8, Integer.valueOf(this.health));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.innerRotation;
        this.dataWatcher.updateObject(8, Integer.valueOf(this.health));
        int var1 = MathHelper.floor_double(this.posX);
        int var2 = MathHelper.floor_double(this.posY);
        int var3 = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getBlockId(var1, var2, var3) != Block.fire.blockID)
        {
            this.worldObj.setBlockWithNotify(var1, var2, var3, Block.fire.blockID);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (!this.isDead && !this.worldObj.isRemote)
        {
            this.health = 0;

            if (this.health <= 0)
            {
                if (!this.worldObj.isRemote)
                {
                    this.setEntityDead();
                    this.worldObj.createExplosion((Entity)null, this.posX, this.posY, this.posZ, 6.0F);
                }
                else
                {
                    this.setEntityDead();
                }
            }
        }

        return true;
    }
}
