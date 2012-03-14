package net.minecraft.src;

public class EntityBlaze extends EntityMob
{
    /** Random offset used in floating behaviour */
    private float heightOffset = 0.5F;

    /** ticks until heightOffset is randomized */
    private int heightOffsetUpdateTime;
    private int field_40152_d;

    public EntityBlaze(World par1World)
    {
        super(par1World);
        this.texture = "/mob/fire.png";
        this.isImmuneToFire = true;
        this.attackStrength = 6;
        this.experienceValue = 10;
    }

    public int getMaxHealth()
    {
        return 20;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.blaze.breathe";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.blaze.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.blaze.death";
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);
    }

    public int getEntityBrightnessForRender(float par1)
    {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getEntityBrightness(float par1)
    {
        return 1.0F;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.isWet())
            {
                this.attackEntityFrom(DamageSource.drown, 1);
            }

            --this.heightOffsetUpdateTime;

            if (this.heightOffsetUpdateTime <= 0)
            {
                this.heightOffsetUpdateTime = 100;
                this.heightOffset = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
            }

            if (this.getEntityToAttack() != null && this.getEntityToAttack().posY + (double)this.getEntityToAttack().getEyeHeight() > this.posY + (double)this.getEyeHeight() + (double)this.heightOffset)
            {
                this.motionY += (0.30000001192092896D - this.motionY) * 0.30000001192092896D;
            }
        }

        if (this.rand.nextInt(24) == 0)
        {
            this.worldObj.playSoundEffect(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F);
        }

        if (!this.onGround && this.motionY < 0.0D)
        {
            this.motionY *= 0.6D;
        }

        for (int var1 = 0; var1 < 2; ++var1)
        {
            this.worldObj.spawnParticle("largesmoke", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
        }

        super.onLivingUpdate();
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    {
        if (this.attackTime <= 0 && par2 < 2.0F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY)
        {
            this.attackTime = 20;
            this.attackEntityAsMob(par1Entity);
        }
        else if (par2 < 30.0F)
        {
            double var3 = par1Entity.posX - this.posX;
            double var5 = par1Entity.boundingBox.minY + (double)(par1Entity.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
            double var7 = par1Entity.posZ - this.posZ;

            if (this.attackTime == 0)
            {
                ++this.field_40152_d;

                if (this.field_40152_d == 1)
                {
                    this.attackTime = 60;
                    this.func_40150_a(true);
                }
                else if (this.field_40152_d <= 4)
                {
                    this.attackTime = 6;
                }
                else
                {
                    this.attackTime = 100;
                    this.field_40152_d = 0;
                    this.func_40150_a(false);
                }

                if (this.field_40152_d > 1)
                {
                    float var9 = MathHelper.sqrt_float(par2) * 0.5F;
                    this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);

                    for (int var10 = 0; var10 < 1; ++var10)
                    {
                        EntitySmallFireball var11 = new EntitySmallFireball(this.worldObj, this, var3 + this.rand.nextGaussian() * (double)var9, var5, var7 + this.rand.nextGaussian() * (double)var9);
                        var11.posY = this.posY + (double)(this.height / 2.0F) + 0.5D;
                        this.worldObj.spawnEntityInWorld(var11);
                    }
                }
            }

            this.rotationYaw = (float)(Math.atan2(var7, var3) * 180.0D / Math.PI) - 90.0F;
            this.hasAttacked = true;
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1) {}

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
        return Item.blazeRod.shiftedIndex;
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return this.func_40151_ac();
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        if (par1)
        {
            int var3 = this.rand.nextInt(2 + par2);

            for (int var4 = 0; var4 < var3; ++var4)
            {
                this.dropItem(Item.blazeRod.shiftedIndex, 1);
            }
        }
    }

    public boolean func_40151_ac()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void func_40150_a(boolean par1)
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

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }
}
