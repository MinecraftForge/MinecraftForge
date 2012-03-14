package net.minecraft.src;

public class EntityFallingSand extends Entity
{
    public int blockID;

    /** How long the block has been falling for. */
    public int fallTime = 0;

    public EntityFallingSand(World par1World)
    {
        super(par1World);
    }

    public EntityFallingSand(World par1World, double par2, double par4, double par6, int par8)
    {
        super(par1World);
        this.blockID = par8;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(par2, par4, par6);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit() {}

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.blockID == 0)
        {
            this.setEntityDead();
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            ++this.fallTime;
            this.motionY -= 0.03999999910593033D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.posY);
            int var3 = MathHelper.floor_double(this.posZ);

            if (this.fallTime == 1 && this.worldObj.getBlockId(var1, var2, var3) == this.blockID)
            {
                this.worldObj.setBlockWithNotify(var1, var2, var3, 0);
            }
            else if (!this.worldObj.isRemote && this.fallTime == 1)
            {
                this.setEntityDead();
            }

            if (this.onGround)
            {
                this.motionX *= 0.699999988079071D;
                this.motionZ *= 0.699999988079071D;
                this.motionY *= -0.5D;

                if (this.worldObj.getBlockId(var1, var2, var3) != Block.pistonMoving.blockID)
                {
                    this.setEntityDead();

                    if ((!this.worldObj.canBlockBePlacedAt(this.blockID, var1, var2, var3, true, 1) || BlockSand.canFallBelow(this.worldObj, var1, var2 - 1, var3) || !this.worldObj.setBlockWithNotify(var1, var2, var3, this.blockID)) && !this.worldObj.isRemote)
                    {
                        this.dropItem(this.blockID, 1);
                    }
                }
            }
            else if (this.fallTime > 100 && !this.worldObj.isRemote && (var2 < 1 || var2 > 256) || this.fallTime > 600)
            {
                this.dropItem(this.blockID, 1);
                this.setEntityDead();
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Tile", (byte)this.blockID);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.blockID = par1NBTTagCompound.getByte("Tile") & 255;
    }

    public float getShadowSize()
    {
        return 0.0F;
    }

    public World getWorld()
    {
        return this.worldObj;
    }
}
