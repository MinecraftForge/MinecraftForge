package net.minecraft.src;

import java.util.List;

public class EntityArrow extends Entity
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    private int inData = 0;
    private boolean inGround = false;
    public boolean doesArrowBelongToPlayer = false;

    /** Seems to be some sort of timer for animating an arrow. */
    public int arrowShake = 0;

    /** the entity that shot this arrow */
    public Entity shootingEntity;

    /** after so long in ground entity dies */
    private int ticksInGround;

    /** cant hit stuff first 5 ticks after you shoot it */
    private int ticksInAir = 0;
    private double damage = 2.0D;
    private int field_46010_n;

    /** Is this arrow a critical hit? (Controls particles and damage) */
    public boolean arrowCritical = false;

    public EntityArrow(World par1World)
    {
        super(par1World);
        this.setSize(0.5F, 0.5F);
    }

    public EntityArrow(World par1World, double par2, double par4, double par6)
    {
        super(par1World);
        this.setSize(0.5F, 0.5F);
        this.setPosition(par2, par4, par6);
        this.yOffset = 0.0F;
    }

    public EntityArrow(World par1World, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving, float par4, float par5)
    {
        super(par1World);
        this.shootingEntity = par2EntityLiving;
        this.doesArrowBelongToPlayer = par2EntityLiving instanceof EntityPlayer;
        this.posY = par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight() - 0.10000000149011612D;
        double var6 = par3EntityLiving.posX - par2EntityLiving.posX;
        double var8 = par3EntityLiving.posY + (double)par3EntityLiving.getEyeHeight() - 0.699999988079071D - this.posY;
        double var10 = par3EntityLiving.posZ - par2EntityLiving.posZ;
        double var12 = (double)MathHelper.sqrt_double(var6 * var6 + var10 * var10);

        if (var12 >= 1.0E-7D)
        {
            float var14 = (float)(Math.atan2(var10, var6) * 180.0D / Math.PI) - 90.0F;
            float var15 = (float)(-(Math.atan2(var8, var12) * 180.0D / Math.PI));
            double var16 = var6 / var12;
            double var18 = var10 / var12;
            this.setLocationAndAngles(par2EntityLiving.posX + var16, this.posY, par2EntityLiving.posZ + var18, var14, var15);
            this.yOffset = 0.0F;
            float var20 = (float)var12 * 0.2F;
            this.setArrowHeading(var6, var8 + (double)var20, var10, par4, par5);
        }
    }

    public EntityArrow(World par1World, EntityLiving par2EntityLiving, float par3)
    {
        super(par1World);
        this.shootingEntity = par2EntityLiving;
        this.doesArrowBelongToPlayer = par2EntityLiving instanceof EntityPlayer;
        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(par2EntityLiving.posX, par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight(), par2EntityLiving.posZ, par2EntityLiving.rotationYaw, par2EntityLiving.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        this.setArrowHeading(this.motionX, this.motionY, this.motionZ, par3 * 1.5F, 1.0F);
    }

    protected void entityInit() {}

    /**
     * Uses the provided coordinates as a heading and determines the velocity from it with the set force and random
     * variance. Args: x, y, z, force, forceVariation
     */
    public void setArrowHeading(double par1, double par3, double par5, float par7, float par8)
    {
        float var9 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= (double)var9;
        par3 /= (double)var9;
        par5 /= (double)var9;
        par1 += this.rand.nextGaussian() * 0.007499999832361937D * (double)par8;
        par3 += this.rand.nextGaussian() * 0.007499999832361937D * (double)par8;
        par5 += this.rand.nextGaussian() * 0.007499999832361937D * (double)par8;
        par1 *= (double)par7;
        par3 *= (double)par7;
        par5 *= (double)par7;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        float var10 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)var10) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var1) * 180.0D / Math.PI);
        }

        int var15 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);

        if (var15 > 0)
        {
            Block.blocksList[var15].setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            AxisAlignedBB var2 = Block.blocksList[var15].getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);

            if (var2 != null && var2.isVecInside(Vec3D.createVector(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            var15 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
            int var18 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

            if (var15 == this.inTile && var18 == this.inData)
            {
                ++this.ticksInGround;

                if (this.ticksInGround == 1200)
                {
                    this.setEntityDead();
                }
            }
            else
            {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        }
        else
        {
            ++this.ticksInAir;
            Vec3D var16 = Vec3D.createVector(this.posX, this.posY, this.posZ);
            Vec3D var17 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition var3 = this.worldObj.rayTraceBlocks_do_do(var16, var17, false, true);
            var16 = Vec3D.createVector(this.posX, this.posY, this.posZ);
            var17 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (var3 != null)
            {
                var17 = Vec3D.createVector(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
            }

            Entity var4 = null;
            List var5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double var6 = 0.0D;
            int var8;
            float var10;

            for (var8 = 0; var8 < var5.size(); ++var8)
            {
                Entity var9 = (Entity)var5.get(var8);

                if (var9.canBeCollidedWith() && (var9 != this.shootingEntity || this.ticksInAir >= 5))
                {
                    var10 = 0.3F;
                    AxisAlignedBB var11 = var9.boundingBox.expand((double)var10, (double)var10, (double)var10);
                    MovingObjectPosition var12 = var11.calculateIntercept(var16, var17);

                    if (var12 != null)
                    {
                        double var13 = var16.distanceTo(var12.hitVec);

                        if (var13 < var6 || var6 == 0.0D)
                        {
                            var4 = var9;
                            var6 = var13;
                        }
                    }
                }
            }

            if (var4 != null)
            {
                var3 = new MovingObjectPosition(var4);
            }

            float var19;

            if (var3 != null)
            {
                if (var3.entityHit != null)
                {
                    var19 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    int var20 = (int)Math.ceil((double)var19 * this.damage);

                    if (this.arrowCritical)
                    {
                        var20 += this.rand.nextInt(var20 / 2 + 2);
                    }

                    DamageSource var22 = null;

                    if (this.shootingEntity == null)
                    {
                        var22 = DamageSource.causeArrowDamage(this, this);
                    }
                    else
                    {
                        var22 = DamageSource.causeArrowDamage(this, this.shootingEntity);
                    }

                    if (this.isBurning())
                    {
                        var3.entityHit.setFire(5);
                    }

                    if (var3.entityHit.attackEntityFrom(var22, var20))
                    {
                        if (var3.entityHit instanceof EntityLiving)
                        {
                            ++((EntityLiving)var3.entityHit).arrowHitTempCounter;

                            if (this.field_46010_n > 0)
                            {
                                float var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                                if (var21 > 0.0F)
                                {
                                    var3.entityHit.addVelocity(this.motionX * (double)this.field_46010_n * 0.6000000238418579D / (double)var21, 0.1D, this.motionZ * (double)this.field_46010_n * 0.6000000238418579D / (double)var21);
                                }
                            }
                        }

                        this.worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                        this.setEntityDead();
                    }
                    else
                    {
                        this.motionX *= -0.10000000149011612D;
                        this.motionY *= -0.10000000149011612D;
                        this.motionZ *= -0.10000000149011612D;
                        this.rotationYaw += 180.0F;
                        this.prevRotationYaw += 180.0F;
                        this.ticksInAir = 0;
                    }
                }
                else
                {
                    this.xTile = var3.blockX;
                    this.yTile = var3.blockY;
                    this.zTile = var3.blockZ;
                    this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
                    this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.motionX = (double)((float)(var3.hitVec.xCoord - this.posX));
                    this.motionY = (double)((float)(var3.hitVec.yCoord - this.posY));
                    this.motionZ = (double)((float)(var3.hitVec.zCoord - this.posZ));
                    var19 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / (double)var19 * 0.05000000074505806D;
                    this.posY -= this.motionY / (double)var19 * 0.05000000074505806D;
                    this.posZ -= this.motionZ / (double)var19 * 0.05000000074505806D;
                    this.worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.arrowCritical = false;
                }
            }

            if (this.arrowCritical)
            {
                for (var8 = 0; var8 < 4; ++var8)
                {
                    this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)var8 / 4.0D, this.posY + this.motionY * (double)var8 / 4.0D, this.posZ + this.motionZ * (double)var8 / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            var19 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var19) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float var23 = 0.99F;
            var10 = 0.05F;

            if (this.isInWater())
            {
                for (int var25 = 0; var25 < 4; ++var25)
                {
                    float var24 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)var24, this.posY - this.motionY * (double)var24, this.posZ - this.motionZ * (double)var24, this.motionX, this.motionY, this.motionZ);
                }

                var23 = 0.8F;
            }

            this.motionX *= (double)var23;
            this.motionY *= (double)var23;
            this.motionZ *= (double)var23;
            this.motionY -= (double)var10;
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short)this.xTile);
        par1NBTTagCompound.setShort("yTile", (short)this.yTile);
        par1NBTTagCompound.setShort("zTile", (short)this.zTile);
        par1NBTTagCompound.setByte("inTile", (byte)this.inTile);
        par1NBTTagCompound.setByte("inData", (byte)this.inData);
        par1NBTTagCompound.setByte("shake", (byte)this.arrowShake);
        par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        par1NBTTagCompound.setBoolean("player", this.doesArrowBelongToPlayer);
        par1NBTTagCompound.setDouble("damage", this.damage);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.xTile = par1NBTTagCompound.getShort("xTile");
        this.yTile = par1NBTTagCompound.getShort("yTile");
        this.zTile = par1NBTTagCompound.getShort("zTile");
        this.inTile = par1NBTTagCompound.getByte("inTile") & 255;
        this.inData = par1NBTTagCompound.getByte("inData") & 255;
        this.arrowShake = par1NBTTagCompound.getByte("shake") & 255;
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
        this.doesArrowBelongToPlayer = par1NBTTagCompound.getBoolean("player");

        if (par1NBTTagCompound.hasKey("damage"))
        {
            this.damage = par1NBTTagCompound.getDouble("damage");
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer)
    {
        if (!this.worldObj.isRemote)
        {
            if (this.inGround && this.doesArrowBelongToPlayer && this.arrowShake <= 0 && par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.arrow, 1)))
            {
                this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, 1);
                this.setEntityDead();
            }
        }
    }

    public void setDamage(double par1)
    {
        this.damage = par1;
    }

    public double getDamage()
    {
        return this.damage;
    }

    public void func_46007_b(int par1)
    {
        this.field_46010_n = par1;
    }

    public boolean func_48313_k_()
    {
        return false;
    }
}
