package net.minecraft.src.balkon;

import java.util.List;
import java.util.Random;
import net.minecraft.src.*;

public class BalkFlailEntity extends Entity
{
    private int xTile;
    private int yTile;
    private int zTile;
    private int inTile;
    private int inData;
    private boolean inGround;
    public boolean doesArrowBelongToPlayer;
    public Entity shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private BalkToolFlail toolFlail;
    public ItemStack itemStackFlail;
    public EnumToolMaterial enumToolMaterial;
    public boolean isSwinging;
    private int weaponDamage;
    private int maxDamage;
    private int currentDamage;
    private double distanceTotal;
    private double distanceX;
    private double distanceY;
    private double distanceZ;
    private int itemID;

    public BalkFlailEntity(World world)
    {
        super(world);
        xTile = -1;
        yTile = -1;
        zTile = -1;
        inTile = 0;
        inData = 0;
        inGround = false;
        doesArrowBelongToPlayer = false;
        ticksInAir = 0;
        setSize(0.5F, 0.5F);
    }

    public BalkFlailEntity(World world, double d, double d1, double d2)
    {
        super(world);
        xTile = -1;
        yTile = -1;
        zTile = -1;
        inTile = 0;
        inData = 0;
        inGround = false;
        doesArrowBelongToPlayer = false;
        ticksInAir = 0;
        setSize(0.5F, 0.5F);
        setPosition(d, d1, d2);
        yOffset = 0.0F;
    }

    public BalkFlailEntity(World world, EntityLiving entityliving, int i, int j, BalkToolFlail balktoolflail, ItemStack itemstack)
    {
        super(world);
        toolFlail = balktoolflail;
        itemStackFlail = itemstack;
        maxDamage = j;
        weaponDamage = i;
        currentDamage = itemstack.getItemDamage();
        xTile = -1;
        yTile = -1;
        zTile = -1;
        inTile = 0;
        inData = 0;
        inGround = false;
        doesArrowBelongToPlayer = false;
        ticksInAir = 0;
        shootingEntity = entityliving;
        doesArrowBelongToPlayer = entityliving instanceof EntityPlayer;
        distanceTotal = 0.0D;
        setSize(0.5F, 0.5F);
        setLocationAndAngles(entityliving.posX, entityliving.posY + (double)entityliving.getEyeHeight(), entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
        posX -= MathHelper.cos((rotationYaw / 180F) * 3.141593F) * 0.16F;
        posY -= 0.10000000000000001D;
        posZ -= MathHelper.sin((rotationYaw / 180F) * 3.141593F) * 0.16F;
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        swing();
    }

    protected void entityInit()
    {
    }

    public void setArrowHeading(double d, double d1, double d2, float f,
            float f1)
    {
        float f2 = MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2);
        d /= f2;
        d1 /= f2;
        d2 /= f2;
        d += rand.nextGaussian() * 0.0074999999999999997D * (double)f1;
        d1 += rand.nextGaussian() * 0.0074999999999999997D * (double)f1;
        d2 += rand.nextGaussian() * 0.0074999999999999997D * (double)f1;
        d *= f;
        d1 *= f;
        d2 *= f;
        motionX = d;
        motionY = d1;
        motionZ = d2;
        float f3 = MathHelper.sqrt_double(d * d + d2 * d2);
        prevRotationYaw = rotationYaw = (float)((Math.atan2(d, d2) * 180D) / 3.1415927410125732D);
        prevRotationPitch = rotationPitch = (float)((Math.atan2(d1, f3) * 180D) / 3.1415927410125732D);
        ticksInGround = 0;
    }

    public void setVelocity(double d, double d1, double d2)
    {
        motionX = d;
        motionY = d1;
        motionZ = d2;
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(d * d + d2 * d2);
            prevRotationYaw = rotationYaw = (float)((Math.atan2(d, d2) * 180D) / 3.1415927410125732D);
            prevRotationPitch = rotationPitch = (float)((Math.atan2(d1, f) * 180D) / 3.1415927410125732D);
            prevRotationPitch = rotationPitch;
            prevRotationYaw = rotationYaw;
            setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
            ticksInGround = 0;
        }
    }

    public void onUpdate()
    {
        super.onUpdate();
        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float)((Math.atan2(motionX, motionZ) * 180D) / 3.1415927410125732D);
            prevRotationPitch = rotationPitch = (float)((Math.atan2(motionY, f) * 180D) / 3.1415927410125732D);
        }
        int i = worldObj.getBlockId(xTile, yTile, zTile);
        if (i > 0)
        {
            Block.blocksList[i].setBlockBoundsBasedOnState(worldObj, xTile, yTile, zTile);
            AxisAlignedBB axisalignedbb = Block.blocksList[i].getCollisionBoundingBoxFromPool(worldObj, xTile, yTile, zTile);
            if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3D.createVector(posX, posY, posZ)))
            {
                inGround = true;
            }
        }
        distanceX = shootingEntity.posX - posX;
        distanceY = shootingEntity.posY - posY;
        distanceZ = shootingEntity.posZ - posZ;
        distanceTotal = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
        if (distanceTotal > 3D)
        {
            returnToOwner(shootingEntity, true);
        }
        try
        {
            if (((EntityPlayer)shootingEntity).getCurrentEquippedItem().getItem() != toolFlail)
            {
                pickUpByOwner();
            }
        }
        catch (NullPointerException nullpointerexception)
        {
            pickUpByOwner();
        }
        if (inGround)
        {
            int j = worldObj.getBlockId(xTile, yTile, zTile);
            int k = worldObj.getBlockMetadata(xTile, yTile, zTile);
            if (j != inTile || k != inData)
            {
                inGround = false;
                motionX *= -rand.nextFloat() * 0.2F + 0.5F;
                motionY *= -rand.nextFloat() * 0.2F + 0.5F;
                motionZ *= -rand.nextFloat() * 0.2F + 0.5F;
                ticksInGround = 0;
                ticksInAir = 0;
                return;
            }
            ticksInGround++;
            if (ticksInGround == 200)
            {
                pickUpByOwner();
            }
            return;
        }
        ticksInAir++;
        Vec3D vec3d = Vec3D.createVector(posX, posY, posZ);
        Vec3D vec3d1 = Vec3D.createVector(posX + motionX, posY + motionY, posZ + motionZ);
        MovingObjectPosition movingobjectposition = worldObj.rayTraceBlocks_do_do(vec3d, vec3d1, false, true);
        vec3d = Vec3D.createVector(posX, posY, posZ);
        vec3d1 = Vec3D.createVector(posX + motionX, posY + motionY, posZ + motionZ);
        if (movingobjectposition != null)
        {
            vec3d1 = Vec3D.createVector(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
        }
        Entity entity = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
        double d = 0.0D;
        for (int l = 0; l < list.size(); l++)
        {
            Entity entity1 = (Entity)list.get(l);
            if (!entity1.canBeCollidedWith() || entity1 == shootingEntity && ticksInAir < 5)
            {
                continue;
            }
            float f4 = 0.3F;
            AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f4, f4, f4);
            MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3d, vec3d1);
            if (movingobjectposition1 == null)
            {
                continue;
            }
            double d1 = vec3d.distanceTo(movingobjectposition1.hitVec);
            if (d1 < d || d == 0.0D)
            {
                entity = entity1;
                d = d1;
            }
        }

        if (entity != null)
        {
            movingobjectposition = new MovingObjectPosition(entity);
        }
        if (movingobjectposition != null)
        {
            if (movingobjectposition.entityHit != null && movingobjectposition.entityHit != shootingEntity)
            {
                if (!movingobjectposition.entityHit.equals(shootingEntity))
                {
                    if (movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeMobDamage((EntityLiving)shootingEntity), weaponDamage))
                    {
                        currentDamage += 2;
                        itemStackFlail.setItemDamage(currentDamage);
                        returnToOwner(shootingEntity, true);
                    }
                    else
                    {
                        motionX *= -0.80000000000000004D;
                        motionY *= -0.80000000000000004D;
                        motionZ *= -0.80000000000000004D;
                        rotationYaw += 180F;
                        prevRotationYaw += 180F;
                        ticksInAir = 0;
                    }
                }
                else
                {
                    pickUpByOwner();
                }
            }
            else
            {
                xTile = movingobjectposition.blockX;
                yTile = movingobjectposition.blockY;
                zTile = movingobjectposition.blockZ;
                inTile = worldObj.getBlockId(xTile, yTile, zTile);
                inData = worldObj.getBlockMetadata(xTile, yTile, zTile);
                motionX = (float)(movingobjectposition.hitVec.xCoord - posX);
                motionY = (float)(movingobjectposition.hitVec.yCoord - posY);
                motionZ = (float)(movingobjectposition.hitVec.zCoord - posZ);
                float f1 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
                posX -= (motionX / (double)f1) * 0.050000000000000003D;
                posY -= (motionY / (double)f1) * 0.050000000000000003D;
                posZ -= (motionZ / (double)f1) * 0.050000000000000003D;
                inGround = true;
            }
        }
        returnToOwner(shootingEntity, false);
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        float f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)((Math.atan2(motionX, motionZ) * 180D) / 3.1415927410125732D);
        for (rotationPitch = (float)((Math.atan2(motionY, f2) * 180D) / 3.1415927410125732D); rotationPitch - prevRotationPitch < -180F; prevRotationPitch -= 360F) { }
        for (; rotationPitch - prevRotationPitch >= 180F; prevRotationPitch += 360F) { }
        for (; rotationYaw - prevRotationYaw < -180F; prevRotationYaw -= 360F) { }
        for (; rotationYaw - prevRotationYaw >= 180F; prevRotationYaw += 360F) { }
        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
        float f3 = 0.99F;
        float f5 = 0.03F;
        if (isInWater())
        {
            for (int i1 = 0; i1 < 4; i1++)
            {
                float f6 = 0.25F;
                worldObj.spawnParticle("bubble", posX - motionX * (double)f6, posY - motionY * (double)f6, posZ - motionZ * (double)f6, motionX, motionY, motionZ);
            }

            f3 = 0.8F;
        }
        motionX *= f3;
        motionY *= f3;
        motionZ *= f3;
        motionY -= f5;
        setPosition(posX, posY, posZ);
    }

    public void returnToOwner(Entity entity, boolean flag)
    {
        if (entity != null)
        {
            if (flag)
            {
                inGround = false;
            }
            double d = shootingEntity.posX;
            double d1 = shootingEntity.boundingBox.minY + 0.40000000596046448D;
            double d2 = shootingEntity.posZ;
            float f = 27F;
            d += -MathHelper.sin(((shootingEntity.rotationYaw + f) / 180F) * 3.141593F) * MathHelper.cos((shootingEntity.rotationPitch / 180F) * 3.141593F) * 2.0F;
            d2 += MathHelper.cos(((shootingEntity.rotationYaw + f) / 180F) * 3.141593F) * MathHelper.cos((shootingEntity.rotationPitch / 180F) * 3.141593F) * 2.0F;
            distanceX = d - posX;
            distanceY = d1 - posY;
            distanceZ = d2 - posZ;
            distanceTotal = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
            if (distanceTotal > 3D)
            {
                posX = d;
                posY = d1;
                posZ = d2;
            }
            else if (distanceTotal > 2.5D)
            {
                isSwinging = false;
                motionX *= -0.5D;
                motionY *= -0.5D;
                motionZ *= -0.5D;
            }
            if (!isSwinging)
            {
                float f1 = 0.2F;
                motionX = distanceX * (double)f1 * distanceTotal;
                motionY = distanceY * (double)f1 * distanceTotal;
                motionZ = distanceZ * (double)f1 * distanceTotal;
            }
        }
    }

    public void pickUpByOwner()
    {
        setEntityDead();
        toolFlail.setThrown(false);
    }

    public void swing()
    {
        if (isSwinging)
        {
            return;
        }
        else
        {
            worldObj.playSoundAtEntity(shootingEntity, "random.bow", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
            motionX = -MathHelper.sin((shootingEntity.rotationYaw / 180F) * 3.141593F) * MathHelper.cos((shootingEntity.rotationPitch / 180F) * 3.141593F);
            motionY = -MathHelper.sin((shootingEntity.rotationPitch / 180F) * 3.141593F);
            motionZ = MathHelper.cos((shootingEntity.rotationYaw / 180F) * 3.141593F) * MathHelper.cos((shootingEntity.rotationPitch / 180F) * 3.141593F);
            setArrowHeading(motionX, motionY, motionZ, 0.75F, 3F);
            isSwinging = true;
            inGround = false;
            return;
        }
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setShort("xTile", (short)xTile);
        nbttagcompound.setShort("yTile", (short)yTile);
        nbttagcompound.setShort("zTile", (short)zTile);
        nbttagcompound.setByte("inTile", (byte)inTile);
        nbttagcompound.setByte("inData", (byte)inData);
        nbttagcompound.setByte("inGround", (byte)(inGround ? 1 : 0));
        nbttagcompound.setBoolean("player", doesArrowBelongToPlayer);
        nbttagcompound.setByte("damage", (byte)weaponDamage);
        nbttagcompound.setShort("durability", (byte)currentDamage);
        nbttagcompound.setShort("maxDurability", (byte)maxDamage);
        nbttagcompound.setShort("dX", (short)(int)distanceX);
        nbttagcompound.setShort("dY", (short)(int)distanceY);
        nbttagcompound.setShort("dZ", (short)(int)distanceZ);
        nbttagcompound.setInteger("item", (short)itemID);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        xTile = nbttagcompound.getShort("xTile");
        yTile = nbttagcompound.getShort("yTile");
        zTile = nbttagcompound.getShort("zTile");
        inTile = nbttagcompound.getByte("inTile") & 0xff;
        inData = nbttagcompound.getByte("inData") & 0xff;
        inGround = nbttagcompound.getByte("inGround") == 1;
        doesArrowBelongToPlayer = nbttagcompound.getBoolean("player");
        weaponDamage = nbttagcompound.getByte("damage");
        currentDamage = nbttagcompound.getShort("durability");
        maxDamage = nbttagcompound.getShort("maxDurability");
        distanceX = nbttagcompound.getShort("dX");
        distanceY = nbttagcompound.getShort("dY");
        distanceZ = nbttagcompound.getShort("dZ");
        itemID = nbttagcompound.getInteger("item");
        setEntityDead();
    }

    public void onCollideWithPlayer(EntityPlayer entityplayer)
    {
        if (worldObj.isRemote)
        {
            return;
        }
        if (inGround)
        {
            if (doesArrowBelongToPlayer);
        }
    }

    public float getShadowSize()
    {
        return 0.2F;
    }
}
