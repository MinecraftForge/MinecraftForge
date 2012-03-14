package net.minecraft.src;

public class EntityEnderman extends EntityMob
{
    private static boolean[] canCarryBlocks = new boolean[256];
    public boolean isAttacking = false;
    private int teleportDelay = 0;
    private int field_35236_h = 0;

    public EntityEnderman(World par1World)
    {
        super(par1World);
        this.texture = "/mob/enderman.png";
        this.moveSpeed = 0.2F;
        this.attackStrength = 7;
        this.setSize(0.6F, 2.9F);
        this.stepHeight = 1.0F;
    }

    public int getMaxHealth()
    {
        return 40;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
        this.dataWatcher.addObject(17, new Byte((byte)0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("carried", (short)this.getCarried());
        par1NBTTagCompound.setShort("carriedData", (short)this.getCarryingData());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setCarried(par1NBTTagCompound.getShort("carried"));
        this.setCarryingData(par1NBTTagCompound.getShort("carriedData"));
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        EntityPlayer var1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, 64.0D);

        if (var1 != null)
        {
            if (this.shouldAttackPlayer(var1))
            {
                if (this.field_35236_h++ == 5)
                {
                    this.field_35236_h = 0;
                    return var1;
                }
            }
            else
            {
                this.field_35236_h = 0;
            }
        }

        return null;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getEntityBrightness(float par1)
    {
        return super.getEntityBrightness(par1);
    }

    /**
     * Checks to see if this enderman should be attacking this player
     */
    private boolean shouldAttackPlayer(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.armorInventory[3];

        if (var2 != null && var2.itemID == Block.pumpkin.blockID)
        {
            return false;
        }
        else
        {
            Vec3D var3 = par1EntityPlayer.getLook(1.0F).normalize();
            Vec3D var4 = Vec3D.createVector(this.posX - par1EntityPlayer.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - (par1EntityPlayer.posY + (double)par1EntityPlayer.getEyeHeight()), this.posZ - par1EntityPlayer.posZ);
            double var5 = var4.lengthVector();
            var4 = var4.normalize();
            double var7 = var3.dotProduct(var4);
            return var7 > 1.0D - 0.025D / var5 ? par1EntityPlayer.canEntityBeSeen(this) : false;
        }
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.isWet())
        {
            this.attackEntityFrom(DamageSource.drown, 1);
        }

        this.isAttacking = this.entityToAttack != null;
        this.moveSpeed = this.entityToAttack != null ? 6.5F : 0.3F;
        int var1;

        if (!this.worldObj.isRemote)
        {
            int var2;
            int var3;
            int var4;

            if (this.getCarried() == 0)
            {
                if (this.rand.nextInt(20) == 0)
                {
                    var1 = MathHelper.floor_double(this.posX - 2.0D + this.rand.nextDouble() * 4.0D);
                    var2 = MathHelper.floor_double(this.posY + this.rand.nextDouble() * 3.0D);
                    var3 = MathHelper.floor_double(this.posZ - 2.0D + this.rand.nextDouble() * 4.0D);
                    var4 = this.worldObj.getBlockId(var1, var2, var3);

                    if (canCarryBlocks[var4])
                    {
                        this.setCarried(this.worldObj.getBlockId(var1, var2, var3));
                        this.setCarryingData(this.worldObj.getBlockMetadata(var1, var2, var3));
                        this.worldObj.setBlockWithNotify(var1, var2, var3, 0);
                    }
                }
            }
            else if (this.rand.nextInt(2000) == 0)
            {
                var1 = MathHelper.floor_double(this.posX - 1.0D + this.rand.nextDouble() * 2.0D);
                var2 = MathHelper.floor_double(this.posY + this.rand.nextDouble() * 2.0D);
                var3 = MathHelper.floor_double(this.posZ - 1.0D + this.rand.nextDouble() * 2.0D);
                var4 = this.worldObj.getBlockId(var1, var2, var3);
                int var5 = this.worldObj.getBlockId(var1, var2 - 1, var3);

                if (var4 == 0 && var5 > 0 && Block.blocksList[var5].renderAsNormalBlock())
                {
                    this.worldObj.setBlockAndMetadataWithNotify(var1, var2, var3, this.getCarried(), this.getCarryingData());
                    this.setCarried(0);
                }
            }
        }

        for (var1 = 0; var1 < 2; ++var1)
        {
            this.worldObj.spawnParticle("portal", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
        }

        if (this.worldObj.isDaytime() && !this.worldObj.isRemote)
        {
            float var6 = this.getEntityBrightness(1.0F);

            if (var6 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (var6 - 0.4F) * 2.0F)
            {
                this.entityToAttack = null;
                this.teleportRandomly();
            }
        }

        if (this.isWet())
        {
            this.entityToAttack = null;
            this.teleportRandomly();
        }

        this.isJumping = false;

        if (this.entityToAttack != null)
        {
            this.faceEntity(this.entityToAttack, 100.0F, 100.0F);
        }

        if (!this.worldObj.isRemote && this.isEntityAlive())
        {
            if (this.entityToAttack != null)
            {
                if (this.entityToAttack instanceof EntityPlayer && this.shouldAttackPlayer((EntityPlayer)this.entityToAttack))
                {
                    this.moveStrafing = this.moveForward = 0.0F;
                    this.moveSpeed = 0.0F;

                    if (this.entityToAttack.getDistanceSqToEntity(this) < 16.0D)
                    {
                        this.teleportRandomly();
                    }

                    this.teleportDelay = 0;
                }
                else if (this.entityToAttack.getDistanceSqToEntity(this) > 256.0D && this.teleportDelay++ >= 30 && this.teleportToEntity(this.entityToAttack))
                {
                    this.teleportDelay = 0;
                }
            }
            else
            {
                this.teleportDelay = 0;
            }
        }

        super.onLivingUpdate();
    }

    /**
     * Teleport the enderman to a random nearby position
     */
    protected boolean teleportRandomly()
    {
        double var1 = this.posX + (this.rand.nextDouble() - 0.5D) * 64.0D;
        double var3 = this.posY + (double)(this.rand.nextInt(64) - 32);
        double var5 = this.posZ + (this.rand.nextDouble() - 0.5D) * 64.0D;
        return this.teleportTo(var1, var3, var5);
    }

    /**
     * Teleport the enderman to another entity
     */
    protected boolean teleportToEntity(Entity par1Entity)
    {
        Vec3D var2 = Vec3D.createVector(this.posX - par1Entity.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - par1Entity.posY + (double)par1Entity.getEyeHeight(), this.posZ - par1Entity.posZ);
        var2 = var2.normalize();
        double var3 = 16.0D;
        double var5 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - var2.xCoord * var3;
        double var7 = this.posY + (double)(this.rand.nextInt(16) - 8) - var2.yCoord * var3;
        double var9 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - var2.zCoord * var3;
        return this.teleportTo(var5, var7, var9);
    }

    /**
     * Teleport the enderman
     */
    protected boolean teleportTo(double par1, double par3, double par5)
    {
        double var7 = this.posX;
        double var9 = this.posY;
        double var11 = this.posZ;
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        boolean var13 = false;
        int var14 = MathHelper.floor_double(this.posX);
        int var15 = MathHelper.floor_double(this.posY);
        int var16 = MathHelper.floor_double(this.posZ);
        int var18;

        if (this.worldObj.blockExists(var14, var15, var16))
        {
            boolean var17 = false;

            while (!var17 && var15 > 0)
            {
                var18 = this.worldObj.getBlockId(var14, var15 - 1, var16);

                if (var18 != 0 && Block.blocksList[var18].blockMaterial.blocksMovement())
                {
                    var17 = true;
                }
                else
                {
                    --this.posY;
                    --var15;
                }
            }

            if (var17)
            {
                this.setPosition(this.posX, this.posY, this.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0 && !this.worldObj.isAnyLiquid(this.boundingBox))
                {
                    var13 = true;
                }
            }
        }

        if (!var13)
        {
            this.setPosition(var7, var9, var11);
            return false;
        }
        else
        {
            short var30 = 128;

            for (var18 = 0; var18 < var30; ++var18)
            {
                double var19 = (double)var18 / ((double)var30 - 1.0D);
                float var21 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                float var22 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                float var23 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                double var24 = var7 + (this.posX - var7) * var19 + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
                double var26 = var9 + (this.posY - var9) * var19 + this.rand.nextDouble() * (double)this.height;
                double var28 = var11 + (this.posZ - var11) * var19 + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
                this.worldObj.spawnParticle("portal", var24, var26, var28, (double)var21, (double)var22, (double)var23);
            }

            this.worldObj.playSoundEffect(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
            this.worldObj.playSoundAtEntity(this, "mob.endermen.portal", 1.0F, 1.0F);
            return true;
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.endermen.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.endermen.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.endermen.death";
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.enderPearl.shiftedIndex;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = this.getDropItemId();

        if (var3 > 0)
        {
            int var4 = this.rand.nextInt(2 + par2);

            for (int var5 = 0; var5 < var4; ++var5)
            {
                this.dropItem(var3, 1);
            }
        }
    }

    /**
     * Set the id of the block an enderman carries
     */
    public void setCarried(int par1)
    {
        this.dataWatcher.updateObject(16, Byte.valueOf((byte)(par1 & 255)));
    }

    /**
     * Get the id of the block an enderman carries
     */
    public int getCarried()
    {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    /**
     * Set the metadata of the block an enderman carries
     */
    public void setCarryingData(int par1)
    {
        this.dataWatcher.updateObject(17, Byte.valueOf((byte)(par1 & 255)));
    }

    /**
     * Get the metadata of the block an enderman carries
     */
    public int getCarryingData()
    {
        return this.dataWatcher.getWatchableObjectByte(17);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (par1DamageSource instanceof EntityDamageSourceIndirect)
        {
            for (int var3 = 0; var3 < 64; ++var3)
            {
                if (this.teleportRandomly())
                {
                    return true;
                }
            }

            return false;
        }
        else
        {
            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    static
    {
        canCarryBlocks[Block.grass.blockID] = true;
        canCarryBlocks[Block.dirt.blockID] = true;
        canCarryBlocks[Block.sand.blockID] = true;
        canCarryBlocks[Block.gravel.blockID] = true;
        canCarryBlocks[Block.plantYellow.blockID] = true;
        canCarryBlocks[Block.plantRed.blockID] = true;
        canCarryBlocks[Block.mushroomBrown.blockID] = true;
        canCarryBlocks[Block.mushroomRed.blockID] = true;
        canCarryBlocks[Block.tnt.blockID] = true;
        canCarryBlocks[Block.cactus.blockID] = true;
        canCarryBlocks[Block.blockClay.blockID] = true;
        canCarryBlocks[Block.pumpkin.blockID] = true;
        canCarryBlocks[Block.melon.blockID] = true;
        canCarryBlocks[Block.mycelium.blockID] = true;
    }
}
