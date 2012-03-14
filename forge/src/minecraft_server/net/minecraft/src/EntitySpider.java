package net.minecraft.src;

public class EntitySpider extends EntityMob
{
    public EntitySpider(World par1World)
    {
        super(par1World);
        this.texture = "/mob/spider.png";
        this.setSize(1.4F, 0.9F);
        this.moveSpeed = 0.8F;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote)
        {
            this.func_40125_a(this.isCollidedHorizontally);
        }
    }

    public int getMaxHealth()
    {
        return 16;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.75D - 0.5D;
    }

    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        float var1 = this.getEntityBrightness(1.0F);

        if (var1 < 0.5F)
        {
            double var2 = 16.0D;
            return this.worldObj.getClosestVulnerablePlayerToEntity(this, var2);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.spider";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.spider";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.spiderdeath";
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    {
        float var3 = this.getEntityBrightness(1.0F);

        if (var3 > 0.5F && this.rand.nextInt(100) == 0)
        {
            this.entityToAttack = null;
        }
        else
        {
            if (par2 > 2.0F && par2 < 6.0F && this.rand.nextInt(10) == 0)
            {
                if (this.onGround)
                {
                    double var4 = par1Entity.posX - this.posX;
                    double var6 = par1Entity.posZ - this.posZ;
                    float var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
                    this.motionX = var4 / (double)var8 * 0.5D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                    this.motionZ = var6 / (double)var8 * 0.5D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                    this.motionY = 0.4000000059604645D;
                }
            }
            else
            {
                super.attackEntity(par1Entity, par2);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.silk.shiftedIndex;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        super.dropFewItems(par1, par2);

        if (par1 && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + par2) > 0))
        {
            this.dropItem(Item.spiderEye.shiftedIndex, 1);
        }
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        return this.func_40124_p_();
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb() {}

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    public boolean isPotionAplicable(PotionEffect par1PotionEffect)
    {
        return par1PotionEffect.getPotionID() == Potion.poison.id ? false : super.isPotionAplicable(par1PotionEffect);
    }

    public boolean func_40124_p_()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void func_40125_a(boolean par1)
    {
        byte var2 = this.dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            var2 = (byte)(var2 | 1);
        }
        else
        {
            var2 &= -2;
        }

        this.dataWatcher.updateObject(16, Byte.valueOf(var2));
    }
}
