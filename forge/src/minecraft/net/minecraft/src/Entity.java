package net.minecraft.src;

import java.util.List;
import java.util.Random;

public abstract class Entity
{
    private static int nextEntityID = 0;
    public int entityId;
    public double renderDistanceWeight;

    /**
     * Blocks entities from spawning when they do their AABB check to make sure the spot is clear of entities that can
     * prevent spawning.
     */
    public boolean preventEntitySpawning;

    /** The entity that is riding this entity */
    public Entity riddenByEntity;

    /** The entity we are currently riding */
    public Entity ridingEntity;

    /** Reference to the World object. */
    public World worldObj;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;

    /** Entity position X */
    public double posX;

    /** Entity position Y */
    public double posY;

    /** Entity position Z */
    public double posZ;

    /** Entity motion X */
    public double motionX;

    /** Entity motion Y */
    public double motionY;

    /** Entity motion Z */
    public double motionZ;

    /** Entity rotation Yaw */
    public float rotationYaw;

    /** Entity rotation Pitch */
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;

    /** Axis aligned bounding box. */
    public final AxisAlignedBB boundingBox;
    public boolean onGround;

    /**
     * True if after a move this entity has collided with something on X- or Z-axis
     */
    public boolean isCollidedHorizontally;

    /**
     * True if after a move this entity has collided with something on Y-axis
     */
    public boolean isCollidedVertically;

    /**
     * True if after a move this entity has collided with something either vertically or horizontally
     */
    public boolean isCollided;

    /** Not used by client. Server sends Packet28EntityVelocity if set */
    public boolean velocityChanged;
    protected boolean isInWeb;
    public boolean field_9293_aM;

    /**
     * gets set by setEntityDead, so this must be the flag whether an Entity is dead (inactive may be better term)
     */
    public boolean isDead;
    public float yOffset;

    /** How wide this entity is considered to be */
    public float width;

    /** How high this entity is considered to be */
    public float height;

    /** The previous ticks distance walked multiplied by 0.6 */
    public float prevDistanceWalkedModified;

    /** The distance walked multiplied by 0.6 */
    public float distanceWalkedModified;
    public float fallDistance;

    /**
     * The distance that has to be exceeded in order to triger a new step sound and an onEntityWalking event on a block
     */
    private int nextStepDistance;

    /**
     * The entity's X coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosX;

    /**
     * The entity's Y coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosY;

    /**
     * The entity's Z coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosZ;
    public float ySize;

    /**
     * How high this entity can step up when running into a block to try to get over it (currently make note the entity
     * will always step up this amount and not just the amount needed)
     */
    public float stepHeight;

    /**
     * Whether this entity won't clip with collision or not (make note it won't disable gravity)
     */
    public boolean noClip;

    /**
     * Reduces the velocity applied by entity collisions by the specified percent.
     */
    public float entityCollisionReduction;
    protected Random rand;

    /** How many ticks has this entity had ran since being alive */
    public int ticksExisted;

    /**
     * The amount of ticks you have to stand inside of fire before be set on fire
     */
    public int fireResistance;
    private int fire;

    /**
     * Whether this entity is currently inside of water (if it handles water movement that is)
     */
    protected boolean inWater;
    public int heartsLife;

    /** Whether or not the entity is on its first update. */
    private boolean firstUpdate;

    /** downloadable location of player's skin */
    public String skinUrl;

    /** downloadable location of player's cloak */
    public String cloakUrl;
    protected boolean isImmuneToFire;
    protected DataWatcher dataWatcher;
    private double entityRiderPitchDelta;
    private double entityRiderYawDelta;

    /** Has this entity been added to the chunk its within */
    public boolean addedToChunk;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;

    /**
     * Render entity even if it is outside the camera frustum. Only true in EntityFish for now. Used in RenderGlobal:
     * render if ignoreFrustumCheck or in frustum
     */
    public boolean ignoreFrustrumCheck;
    public boolean isAirBorne;
    
    /** Forge: Used to store custom data for each entity. */
    private NBTTagCompound customEntityData;

    public Entity(World par1World)
    {
        this.entityId = nextEntityID++;
        this.renderDistanceWeight = 1.0D;
        this.preventEntitySpawning = false;
        this.boundingBox = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        this.onGround = false;
        this.isCollided = false;
        this.velocityChanged = false;
        this.field_9293_aM = true;
        this.isDead = false;
        this.yOffset = 0.0F;
        this.width = 0.6F;
        this.height = 1.8F;
        this.prevDistanceWalkedModified = 0.0F;
        this.distanceWalkedModified = 0.0F;
        this.fallDistance = 0.0F;
        this.nextStepDistance = 1;
        this.ySize = 0.0F;
        this.stepHeight = 0.0F;
        this.noClip = false;
        this.entityCollisionReduction = 0.0F;
        this.rand = new Random();
        this.ticksExisted = 0;
        this.fireResistance = 1;
        this.fire = 0;
        this.inWater = false;
        this.heartsLife = 0;
        this.firstUpdate = true;
        this.isImmuneToFire = false;
        this.dataWatcher = new DataWatcher();
        this.addedToChunk = false;
        this.worldObj = par1World;
        this.setPosition(0.0D, 0.0D, 0.0D);
        this.dataWatcher.addObject(0, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(1, Short.valueOf((short)300));
        this.entityInit();
    }

    protected abstract void entityInit();
    
    /**
     * Returns a NBTTagCompound that can be used to store custom data for this entity.
     * It will be written, and read from disc, so it persists over world saves.
     * @return A NBTTagCompound
     */
    public NBTTagCompound getEntityData()
    {
    	if (customEntityData == null)
    	{
    		customEntityData = new NBTTagCompound();
    	}
    	return customEntityData;
    }

    public DataWatcher getDataWatcher()
    {
        return this.dataWatcher;
    }

    public boolean equals(Object par1Obj)
    {
        return par1Obj instanceof Entity ? ((Entity)par1Obj).entityId == this.entityId : false;
    }

    public int hashCode()
    {
        return this.entityId;
    }

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    protected void preparePlayerToSpawn()
    {
        if (this.worldObj != null)
        {
            while (this.posY > 0.0D)
            {
                this.setPosition(this.posX, this.posY, this.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0)
                {
                    break;
                }

                ++this.posY;
            }

            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.rotationPitch = 0.0F;
        }
    }

    /**
     * Will get destroyed next tick
     */
    public void setEntityDead()
    {
        this.isDead = true;
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected void setSize(float par1, float par2)
    {
        this.width = par1;
        this.height = par2;
    }

    /**
     * Sets the rotation of the entity
     */
    protected void setRotation(float par1, float par2)
    {
        this.rotationYaw = par1 % 360.0F;
        this.rotationPitch = par2 % 360.0F;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(double par1, double par3, double par5)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        float var7 = this.width / 2.0F;
        float var8 = this.height;
        this.boundingBox.setBounds(par1 - (double)var7, par3 - (double)this.yOffset + (double)this.ySize, par5 - (double)var7, par1 + (double)var7, par3 - (double)this.yOffset + (double)this.ySize + (double)var8, par5 + (double)var7);
    }

    public void setAngles(float par1, float par2)
    {
        float var3 = this.rotationPitch;
        float var4 = this.rotationYaw;
        this.rotationYaw = (float)((double)this.rotationYaw + (double)par1 * 0.15D);
        this.rotationPitch = (float)((double)this.rotationPitch - (double)par2 * 0.15D);

        if (this.rotationPitch < -90.0F)
        {
            this.rotationPitch = -90.0F;
        }

        if (this.rotationPitch > 90.0F)
        {
            this.rotationPitch = 90.0F;
        }

        this.prevRotationPitch += this.rotationPitch - var3;
        this.prevRotationYaw += this.rotationYaw - var4;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.onEntityUpdate();
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        Profiler.startSection("entityBaseTick");

        if (this.ridingEntity != null && this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }

        ++this.ticksExisted;
        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        int var3;

        if (this.isSprinting() && !this.isInWater())
        {
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            var3 = MathHelper.floor_double(this.posZ);
            int var4 = this.worldObj.getBlockId(var1, var2, var3);

            if (var4 > 0)
            {
                this.worldObj.spawnParticle("tilecrack_" + var4, this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.boundingBox.minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D);
            }
        }

        if (this.handleWaterMovement())
        {
            if (!this.inWater && !this.firstUpdate)
            {
                float var6 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.2F;

                if (var6 > 1.0F)
                {
                    var6 = 1.0F;
                }

                this.worldObj.playSoundAtEntity(this, "random.splash", var6, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                float var7 = (float)MathHelper.floor_double(this.boundingBox.minY);
                float var5;
                float var8;

                for (var3 = 0; (float)var3 < 1.0F + this.width * 20.0F; ++var3)
                {
                    var8 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("bubble", this.posX + (double)var8, (double)(var7 + 1.0F), this.posZ + (double)var5, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
                }

                for (var3 = 0; (float)var3 < 1.0F + this.width * 20.0F; ++var3)
                {
                    var8 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("splash", this.posX + (double)var8, (double)(var7 + 1.0F), this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
                }
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        }
        else
        {
            this.inWater = false;
        }

        if (this.worldObj.isRemote)
        {
            this.fire = 0;
        }
        else if (this.fire > 0)
        {
            if (this.isImmuneToFire)
            {
                this.fire -= 4;

                if (this.fire < 0)
                {
                    this.fire = 0;
                }
            }
            else
            {
                if (this.fire % 20 == 0)
                {
                    this.attackEntityFrom(DamageSource.onFire, 1);
                }

                --this.fire;
            }
        }

        if (this.handleLavaMovement())
        {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        if (!this.worldObj.isRemote)
        {
            this.setFlag(0, this.fire > 0);
            this.setFlag(2, this.ridingEntity != null);
        }

        this.firstUpdate = false;
        Profiler.endSection();
    }

    /**
     * Called whenever the entity is walking inside of lava.
     */
    protected void setOnFireFromLava()
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.lava, 4);
            this.setFire(15);
        }
    }

    /**
     * Sets entity to burn for x amount of seconds, cannot lower amount of existing fire.
     */
    public void setFire(int par1)
    {
        int var2 = par1 * 20;

        if (this.fire < var2)
        {
            this.fire = var2;
        }
    }

    /**
     * Removes fire from entity.
     */
    public void extinguish()
    {
        this.fire = 0;
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill()
    {
        this.setEntityDead();
    }

    /**
     * Checks if the offset position from the entity's current position is inside of liquid. Args: x, y, z
     */
    public boolean isOffsetPositionInLiquid(double par1, double par3, double par5)
    {
        AxisAlignedBB var7 = this.boundingBox.getOffsetBoundingBox(par1, par3, par5);
        List var8 = this.worldObj.getCollidingBoundingBoxes(this, var7);
        return var8.size() > 0 ? false : !this.worldObj.isAnyLiquid(var7);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double par1, double par3, double par5)
    {
        if (this.noClip)
        {
            this.boundingBox.offset(par1, par3, par5);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        }
        else
        {
            Profiler.startSection("move");
            this.ySize *= 0.4F;
            double var7 = this.posX;
            double var9 = this.posZ;

            if (this.isInWeb)
            {
                this.isInWeb = false;
                par1 *= 0.25D;
                par3 *= 0.05000000074505806D;
                par5 *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double var11 = par1;
            double var13 = par3;
            double var15 = par5;
            AxisAlignedBB var17 = this.boundingBox.copy();
            boolean var18 = this.onGround && this.isSneaking() && this instanceof EntityPlayer;

            if (var18)
            {
                double var19;

                for (var19 = 0.05D; par1 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par1, -1.0D, 0.0D)).size() == 0; var11 = par1)
                {
                    if (par1 < var19 && par1 >= -var19)
                    {
                        par1 = 0.0D;
                    }
                    else if (par1 > 0.0D)
                    {
                        par1 -= var19;
                    }
                    else
                    {
                        par1 += var19;
                    }
                }

                for (; par5 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0D, -1.0D, par5)).size() == 0; var15 = par5)
                {
                    if (par5 < var19 && par5 >= -var19)
                    {
                        par5 = 0.0D;
                    }
                    else if (par5 > 0.0D)
                    {
                        par5 -= var19;
                    }
                    else
                    {
                        par5 += var19;
                    }
                }
            }

            List var35 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(par1, par3, par5));

            for (int var20 = 0; var20 < var35.size(); ++var20)
            {
                par3 = ((AxisAlignedBB)var35.get(var20)).calculateYOffset(this.boundingBox, par3);
            }

            this.boundingBox.offset(0.0D, par3, 0.0D);

            if (!this.field_9293_aM && var13 != par3)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            boolean var36 = this.onGround || var13 != par3 && var13 < 0.0D;
            int var21;

            for (var21 = 0; var21 < var35.size(); ++var21)
            {
                par1 = ((AxisAlignedBB)var35.get(var21)).calculateXOffset(this.boundingBox, par1);
            }

            this.boundingBox.offset(par1, 0.0D, 0.0D);

            if (!this.field_9293_aM && var11 != par1)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            for (var21 = 0; var21 < var35.size(); ++var21)
            {
                par5 = ((AxisAlignedBB)var35.get(var21)).calculateZOffset(this.boundingBox, par5);
            }

            this.boundingBox.offset(0.0D, 0.0D, par5);

            if (!this.field_9293_aM && var15 != par5)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            double var23;
            int var28;
            double var37;

            if (this.stepHeight > 0.0F && var36 && (var18 || this.ySize < 0.05F) && (var11 != par1 || var15 != par5))
            {
                var37 = par1;
                var23 = par3;
                double var25 = par5;
                par1 = var11;
                par3 = (double)this.stepHeight;
                par5 = var15;
                AxisAlignedBB var27 = this.boundingBox.copy();
                this.boundingBox.setBB(var17);
                var35 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(var11, par3, var15));

                for (var28 = 0; var28 < var35.size(); ++var28)
                {
                    par3 = ((AxisAlignedBB)var35.get(var28)).calculateYOffset(this.boundingBox, par3);
                }

                this.boundingBox.offset(0.0D, par3, 0.0D);

                if (!this.field_9293_aM && var13 != par3)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                for (var28 = 0; var28 < var35.size(); ++var28)
                {
                    par1 = ((AxisAlignedBB)var35.get(var28)).calculateXOffset(this.boundingBox, par1);
                }

                this.boundingBox.offset(par1, 0.0D, 0.0D);

                if (!this.field_9293_aM && var11 != par1)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                for (var28 = 0; var28 < var35.size(); ++var28)
                {
                    par5 = ((AxisAlignedBB)var35.get(var28)).calculateZOffset(this.boundingBox, par5);
                }

                this.boundingBox.offset(0.0D, 0.0D, par5);

                if (!this.field_9293_aM && var15 != par5)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                if (!this.field_9293_aM && var13 != par3)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }
                else
                {
                    par3 = (double)(-this.stepHeight);

                    for (var28 = 0; var28 < var35.size(); ++var28)
                    {
                        par3 = ((AxisAlignedBB)var35.get(var28)).calculateYOffset(this.boundingBox, par3);
                    }

                    this.boundingBox.offset(0.0D, par3, 0.0D);
                }

                if (var37 * var37 + var25 * var25 >= par1 * par1 + par5 * par5)
                {
                    par1 = var37;
                    par3 = var23;
                    par5 = var25;
                    this.boundingBox.setBB(var27);
                }
                else
                {
                    double var40 = this.boundingBox.minY - (double)((int)this.boundingBox.minY);

                    if (var40 > 0.0D)
                    {
                        this.ySize = (float)((double)this.ySize + var40 + 0.01D);
                    }
                }
            }

            Profiler.endSection();
            Profiler.startSection("rest");
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
            this.isCollidedHorizontally = var11 != par1 || var15 != par5;
            this.isCollidedVertically = var13 != par3;
            this.onGround = var13 != par3 && var13 < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            this.updateFallState(par3, this.onGround);

            if (var11 != par1)
            {
                this.motionX = 0.0D;
            }

            if (var13 != par3)
            {
                this.motionY = 0.0D;
            }

            if (var15 != par5)
            {
                this.motionZ = 0.0D;
            }

            var37 = this.posX - var7;
            var23 = this.posZ - var9;
            int var26;
            int var38;
            int var39;

            if (this.canTriggerWalking() && !var18 && this.ridingEntity == null)
            {
                this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt_double(var37 * var37 + var23 * var23) * 0.6D);
                var38 = MathHelper.floor_double(this.posX);
                var26 = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
                var39 = MathHelper.floor_double(this.posZ);
                var28 = this.worldObj.getBlockId(var38, var26, var39);

                if (var28 == 0 && this.worldObj.getBlockId(var38, var26 - 1, var39) == Block.fence.blockID)
                {
                    var28 = this.worldObj.getBlockId(var38, var26 - 1, var39);
                }

                if (this.distanceWalkedModified > (float)this.nextStepDistance && var28 > 0)
                {
                    this.nextStepDistance = (int)this.distanceWalkedModified + 1;
                    this.playStepSound(var38, var26, var39, var28);
                    Block.blocksList[var28].onEntityWalking(this.worldObj, var38, var26, var39, this);
                }
            }

            var38 = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
            var26 = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
            var39 = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
            var28 = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
            int var29 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
            int var30 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);

            if (this.worldObj.checkChunksExist(var38, var26, var39, var28, var29, var30))
            {
                for (int var31 = var38; var31 <= var28; ++var31)
                {
                    for (int var32 = var26; var32 <= var29; ++var32)
                    {
                        for (int var33 = var39; var33 <= var30; ++var33)
                        {
                            int var34 = this.worldObj.getBlockId(var31, var32, var33);

                            if (var34 > 0)
                            {
                                Block.blocksList[var34].onEntityCollidedWithBlock(this.worldObj, var31, var32, var33, this);
                            }
                        }
                    }
                }
            }

            boolean var41 = this.isWet();

            if (this.worldObj.isBoundingBoxBurning(this.boundingBox.contract(0.001D, 0.001D, 0.001D)))
            {
                this.dealFireDamage(1);

                if (!var41)
                {
                    ++this.fire;

                    if (this.fire == 0)
                    {
                        this.setFire(8);
                    }
                }
            }
            else if (this.fire <= 0)
            {
                this.fire = -this.fireResistance;
            }

            if (var41 && this.fire > 0)
            {
                this.worldObj.playSoundAtEntity(this, "random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -this.fireResistance;
            }

            Profiler.endSection();
        }
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        StepSound var5 = Block.blocksList[par4].stepSound;

        if (this.worldObj.getBlockId(par1, par2 + 1, par3) == Block.snow.blockID)
        {
            var5 = Block.snow.stepSound;
            this.worldObj.playSoundAtEntity(this, var5.getStepSound(), var5.getVolume() * 0.15F, var5.getPitch());
        }
        else if (!Block.blocksList[par4].blockMaterial.isLiquid())
        {
            this.worldObj.playSoundAtEntity(this, var5.getStepSound(), var5.getVolume() * 0.15F, var5.getPitch());
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return true;
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
        if (par3)
        {
            if (this.fallDistance > 0.0F)
            {
                if (this instanceof EntityLiving)
                {
                    int var4 = MathHelper.floor_double(this.posX);
                    int var5 = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
                    int var6 = MathHelper.floor_double(this.posZ);
                    int var7 = this.worldObj.getBlockId(var4, var5, var6);

                    if (var7 == 0 && this.worldObj.getBlockId(var4, var5 - 1, var6) == Block.fence.blockID)
                    {
                        var7 = this.worldObj.getBlockId(var4, var5 - 1, var6);
                    }

                    if (var7 > 0)
                    {
                        Block.blocksList[var7].onFallenUpon(this.worldObj, var4, var5, var6, this, this.fallDistance);
                    }
                }

                this.fall(this.fallDistance);
                this.fallDistance = 0.0F;
            }
        }
        else if (par1 < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - par1);
        }
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return null;
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(int par1)
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.inFire, par1);
        }
    }

    public final boolean isImmuneToFire()
    {
        return this.isImmuneToFire;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.fall(par1);
        }
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    public boolean isWet()
    {
        return this.inWater || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
     * true)
     */
    public boolean isInWater()
    {
        return this.inWater;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this);
    }

    /**
     * Checks if the current block the entity is within of the specified material type
     */
    public boolean isInsideOfMaterial(Material par1Material)
    {
        double var2 = this.posY + (double)this.getEyeHeight();
        int var4 = MathHelper.floor_double(this.posX);
        int var5 = MathHelper.floor_float((float)MathHelper.floor_double(var2));
        int var6 = MathHelper.floor_double(this.posZ);
        int var7 = this.worldObj.getBlockId(var4, var5, var6);

        if (var7 != 0 && Block.blocksList[var7].blockMaterial == par1Material)
        {
            float var8 = BlockFluid.getFluidHeightPercent(this.worldObj.getBlockMetadata(var4, var5, var6)) - 0.11111111F;
            float var9 = (float)(var5 + 1) - var8;
            return var2 < (double)var9;
        }
        else
        {
            return false;
        }
    }

    public float getEyeHeight()
    {
        return 0.0F;
    }

    /**
     * Whether or not the current entity is in lava
     */
    public boolean handleLavaMovement()
    {
        return this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
    }

    /**
     * Used in both water and by flying objects
     */
    public void moveFlying(float par1, float par2, float par3)
    {
        float var4 = MathHelper.sqrt_float(par1 * par1 + par2 * par2);

        if (var4 >= 0.01F)
        {
            if (var4 < 1.0F)
            {
                var4 = 1.0F;
            }

            var4 = par3 / var4;
            par1 *= var4;
            par2 *= var4;
            float var5 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
            float var6 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
            this.motionX += (double)(par1 * var6 - par2 * var5);
            this.motionZ += (double)(par2 * var6 + par1 * var5);
        }
    }

    public int getEntityBrightnessForRender(float par1)
    {
        int var2 = MathHelper.floor_double(this.posX);
        int var3 = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(var2, 0, var3))
        {
            double var4 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int var6 = MathHelper.floor_double(this.posY - (double)this.yOffset + var4);
            return this.worldObj.getLightBrightnessForSkyBlocks(var2, var6, var3, 0);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getEntityBrightness(float par1)
    {
        int var2 = MathHelper.floor_double(this.posX);
        int var3 = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(var2, 0, var3))
        {
            double var4 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int var6 = MathHelper.floor_double(this.posY - (double)this.yOffset + var4);
            return this.worldObj.getLightBrightness(var2, var6, var3);
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Sets the reference to the World object.
     */
    public void setWorld(World par1World)
    {
        this.worldObj = par1World;
    }

    /**
     * Sets the entity's position and rotation. Args: posX, posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation(double par1, double par3, double par5, float par7, float par8)
    {
        this.prevPosX = this.posX = par1;
        this.prevPosY = this.posY = par3;
        this.prevPosZ = this.posZ = par5;
        this.prevRotationYaw = this.rotationYaw = par7;
        this.prevRotationPitch = this.rotationPitch = par8;
        this.ySize = 0.0F;
        double var9 = (double)(this.prevRotationYaw - par7);

        if (var9 < -180.0D)
        {
            this.prevRotationYaw += 360.0F;
        }

        if (var9 >= 180.0D)
        {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(par7, par8);
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setLocationAndAngles(double par1, double par3, double par5, float par7, float par8)
    {
        this.lastTickPosX = this.prevPosX = this.posX = par1;
        this.lastTickPosY = this.prevPosY = this.posY = par3 + (double)this.yOffset;
        this.lastTickPosZ = this.prevPosZ = this.posZ = par5;
        this.rotationYaw = par7;
        this.rotationPitch = par8;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    /**
     * Returns the distance to the entity. Args: entity
     */
    public float getDistanceToEntity(Entity par1Entity)
    {
        float var2 = (float)(this.posX - par1Entity.posX);
        float var3 = (float)(this.posY - par1Entity.posY);
        float var4 = (float)(this.posZ - par1Entity.posZ);
        return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
    }

    /**
     * Gets the squared distance to the position. Args: x, y, z
     */
    public double getDistanceSq(double par1, double par3, double par5)
    {
        double var7 = this.posX - par1;
        double var9 = this.posY - par3;
        double var11 = this.posZ - par5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    /**
     * Gets the distance to the position. Args: x, y, z
     */
    public double getDistance(double par1, double par3, double par5)
    {
        double var7 = this.posX - par1;
        double var9 = this.posY - par3;
        double var11 = this.posZ - par5;
        return (double)MathHelper.sqrt_double(var7 * var7 + var9 * var9 + var11 * var11);
    }

    /**
     * Returns the squared distance to the entity. Args: entity
     */
    public double getDistanceSqToEntity(Entity par1Entity)
    {
        double var2 = this.posX - par1Entity.posX;
        double var4 = this.posY - par1Entity.posY;
        double var6 = this.posZ - par1Entity.posZ;
        return var2 * var2 + var4 * var4 + var6 * var6;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {}

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity par1Entity)
    {
        if (par1Entity.riddenByEntity != this && par1Entity.ridingEntity != this)
        {
            double var2 = par1Entity.posX - this.posX;
            double var4 = par1Entity.posZ - this.posZ;
            double var6 = MathHelper.abs_max(var2, var4);

            if (var6 >= 0.009999999776482582D)
            {
                var6 = (double)MathHelper.sqrt_double(var6);
                var2 /= var6;
                var4 /= var6;
                double var8 = 1.0D / var6;

                if (var8 > 1.0D)
                {
                    var8 = 1.0D;
                }

                var2 *= var8;
                var4 *= var8;
                var2 *= 0.05000000074505806D;
                var4 *= 0.05000000074505806D;
                var2 *= (double)(1.0F - this.entityCollisionReduction);
                var4 *= (double)(1.0F - this.entityCollisionReduction);
                this.addVelocity(-var2, 0.0D, -var4);
                par1Entity.addVelocity(var2, 0.0D, var4);
            }
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double par1, double par3, double par5)
    {
        this.motionX += par1;
        this.motionY += par3;
        this.motionZ += par5;
        this.isAirBorne = true;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked()
    {
        this.velocityChanged = true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        this.setBeenAttacked();
        return false;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return false;
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void addToPlayerScore(Entity par1Entity, int par2) {}

    /**
     * Checks using a Vec3d to determine if this entity is within range of that vector to be rendered. Args: vec3D
     */
    public boolean isInRangeToRenderVec3D(Vec3D par1Vec3D)
    {
        double var2 = this.posX - par1Vec3D.xCoord;
        double var4 = this.posY - par1Vec3D.yCoord;
        double var6 = this.posZ - par1Vec3D.zCoord;
        double var8 = var2 * var2 + var4 * var4 + var6 * var6;
        return this.isInRangeToRenderDist(var8);
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double par1)
    {
        double var3 = this.boundingBox.getAverageEdgeLength();
        var3 *= 64.0D * this.renderDistanceWeight;
        return par1 < var3 * var3;
    }

    /**
     * returns the directory and filename as a String
     */
    public String getEntityTexture()
    {
        return null;
    }

    /**
     * adds the ID of this entity to the NBT given
     */
    public boolean addEntityID(NBTTagCompound par1NBTTagCompound)
    {
        String var2 = this.getEntityString();

        if (!this.isDead && var2 != null)
        {
            par1NBTTagCompound.setString("id", var2);
            this.writeToNBT(par1NBTTagCompound);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Save the entity to NBT (calls an abstract helper method to write extra data)
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setTag("Pos", this.newDoubleNBTList(new double[] {this.posX, this.posY + (double)this.ySize, this.posZ}));
        par1NBTTagCompound.setTag("Motion", this.newDoubleNBTList(new double[] {this.motionX, this.motionY, this.motionZ}));
        par1NBTTagCompound.setTag("Rotation", this.newFloatNBTList(new float[] {this.rotationYaw, this.rotationPitch}));
        par1NBTTagCompound.setFloat("FallDistance", this.fallDistance);
        par1NBTTagCompound.setShort("Fire", (short)this.fire);
        par1NBTTagCompound.setShort("Air", (short)this.getAir());
        par1NBTTagCompound.setBoolean("OnGround", this.onGround);
        if (customEntityData != null)
        {
            par1NBTTagCompound.setCompoundTag("ForgeData", customEntityData);
        }
        this.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * Reads the entity from NBT (calls an abstract helper method to read specialized data)
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagList var2 = par1NBTTagCompound.getTagList("Pos");
        NBTTagList var3 = par1NBTTagCompound.getTagList("Motion");
        NBTTagList var4 = par1NBTTagCompound.getTagList("Rotation");
        this.motionX = ((NBTTagDouble)var3.tagAt(0)).data;
        this.motionY = ((NBTTagDouble)var3.tagAt(1)).data;
        this.motionZ = ((NBTTagDouble)var3.tagAt(2)).data;

        if (Math.abs(this.motionX) > 10.0D)
        {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) > 10.0D)
        {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) > 10.0D)
        {
            this.motionZ = 0.0D;
        }

        this.prevPosX = this.lastTickPosX = this.posX = ((NBTTagDouble)var2.tagAt(0)).data;
        this.prevPosY = this.lastTickPosY = this.posY = ((NBTTagDouble)var2.tagAt(1)).data;
        this.prevPosZ = this.lastTickPosZ = this.posZ = ((NBTTagDouble)var2.tagAt(2)).data;
        this.prevRotationYaw = this.rotationYaw = ((NBTTagFloat)var4.tagAt(0)).data;
        this.prevRotationPitch = this.rotationPitch = ((NBTTagFloat)var4.tagAt(1)).data;
        this.fallDistance = par1NBTTagCompound.getFloat("FallDistance");
        this.fire = par1NBTTagCompound.getShort("Fire");
        this.setAir(par1NBTTagCompound.getShort("Air"));
        this.onGround = par1NBTTagCompound.getBoolean("OnGround");
        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(this.rotationYaw, this.rotationPitch);
        if (par1NBTTagCompound.hasKey("ForgeData"))
        {
            customEntityData = par1NBTTagCompound.getCompoundTag("ForgeData");
        }
        this.readEntityFromNBT(par1NBTTagCompound);
    }

    /**
     * Returns the string that identifies this Entity's class
     */
    protected final String getEntityString()
    {
        return EntityList.getEntityString(this);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected abstract void readEntityFromNBT(NBTTagCompound var1);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected abstract void writeEntityToNBT(NBTTagCompound var1);

    /**
     * creates a NBT list from the array of doubles passed to this function
     */
    protected NBTTagList newDoubleNBTList(double ... par1ArrayOfDouble)
    {
        NBTTagList var2 = new NBTTagList();
        double[] var3 = par1ArrayOfDouble;
        int var4 = par1ArrayOfDouble.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            double var6 = var3[var5];
            var2.appendTag(new NBTTagDouble((String)null, var6));
        }

        return var2;
    }

    /**
     * creates a NBT list from the array of floats passed to this function
     */
    protected NBTTagList newFloatNBTList(float ... par1ArrayOfFloat)
    {
        NBTTagList var2 = new NBTTagList();
        float[] var3 = par1ArrayOfFloat;
        int var4 = par1ArrayOfFloat.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            float var6 = var3[var5];
            var2.appendTag(new NBTTagFloat((String)null, var6));
        }

        return var2;
    }

    public float getShadowSize()
    {
        return this.height / 2.0F;
    }

    /**
     * Drops an item stack at the entity's position. Args: itemID, count
     */
    public EntityItem dropItem(int par1, int par2)
    {
        return this.dropItemWithOffset(par1, par2, 0.0F);
    }

    /**
     * Drops an item stack with a specified y offset. Args: itemID, count, yOffset
     */
    public EntityItem dropItemWithOffset(int par1, int par2, float par3)
    {
        return this.entityDropItem(new ItemStack(par1, par2, 0), par3);
    }

    /**
     * Drops an item at the position of the entity.
     */
    public EntityItem entityDropItem(ItemStack par1ItemStack, float par2)
    {
        EntityItem var3 = new EntityItem(this.worldObj, this.posX, this.posY + (double)par2, this.posZ, par1ItemStack);
        var3.delayBeforeCanPickup = 10;
        this.worldObj.spawnEntityInWorld(var3);
        return var3;
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        for (int var1 = 0; var1 < 8; ++var1)
        {
            float var2 = ((float)((var1 >> 0) % 2) - 0.5F) * this.width * 0.8F;
            float var3 = ((float)((var1 >> 1) % 2) - 0.5F) * 0.1F;
            float var4 = ((float)((var1 >> 2) % 2) - 0.5F) * this.width * 0.8F;
            int var5 = MathHelper.floor_double(this.posX + (double)var2);
            int var6 = MathHelper.floor_double(this.posY + (double)this.getEyeHeight() + (double)var3);
            int var7 = MathHelper.floor_double(this.posZ + (double)var4);

            if (this.worldObj.isBlockNormalCube(var5, var6, var7))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        return false;
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return null;
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        if (this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.onUpdate();

            if (this.ridingEntity != null)
            {
                this.ridingEntity.updateRiderPosition();
                this.entityRiderYawDelta += (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);

                for (this.entityRiderPitchDelta += (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch); this.entityRiderYawDelta >= 180.0D; this.entityRiderYawDelta -= 360.0D)
                {
                    ;
                }

                while (this.entityRiderYawDelta < -180.0D)
                {
                    this.entityRiderYawDelta += 360.0D;
                }

                while (this.entityRiderPitchDelta >= 180.0D)
                {
                    this.entityRiderPitchDelta -= 360.0D;
                }

                while (this.entityRiderPitchDelta < -180.0D)
                {
                    this.entityRiderPitchDelta += 360.0D;
                }

                double var1 = this.entityRiderYawDelta * 0.5D;
                double var3 = this.entityRiderPitchDelta * 0.5D;
                float var5 = 10.0F;

                if (var1 > (double)var5)
                {
                    var1 = (double)var5;
                }

                if (var1 < (double)(-var5))
                {
                    var1 = (double)(-var5);
                }

                if (var3 > (double)var5)
                {
                    var3 = (double)var5;
                }

                if (var3 < (double)(-var5))
                {
                    var3 = (double)(-var5);
                }

                this.entityRiderYawDelta -= var1;
                this.entityRiderPitchDelta -= var3;
                this.rotationYaw = (float)((double)this.rotationYaw + var1);
                this.rotationPitch = (float)((double)this.rotationPitch + var3);
            }
        }
    }

    public void updateRiderPosition()
    {
        this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return (double)this.yOffset;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.75D;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity par1Entity)
    {
        this.entityRiderPitchDelta = 0.0D;
        this.entityRiderYawDelta = 0.0D;

        if (par1Entity == null)
        {
            if (this.ridingEntity != null)
            {
                this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double)this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        }
        else if (this.ridingEntity == par1Entity)
        {
            this.ridingEntity.riddenByEntity = null;
            this.ridingEntity = null;
            this.setLocationAndAngles(par1Entity.posX, par1Entity.boundingBox.minY + (double)par1Entity.height, par1Entity.posZ, this.rotationYaw, this.rotationPitch);
        }
        else
        {
            if (this.ridingEntity != null)
            {
                this.ridingEntity.riddenByEntity = null;
            }

            if (par1Entity.riddenByEntity != null)
            {
                par1Entity.riddenByEntity.ridingEntity = null;
            }

            this.ridingEntity = par1Entity;
            par1Entity.riddenByEntity = this;
        }
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
        List var10 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.contract(0.03125D, 0.0D, 0.03125D));

        if (var10.size() > 0)
        {
            double var11 = 0.0D;

            for (int var13 = 0; var13 < var10.size(); ++var13)
            {
                AxisAlignedBB var14 = (AxisAlignedBB)var10.get(var13);

                if (var14.maxY > var11)
                {
                    var11 = var14.maxY;
                }
            }

            par3 += var11 - this.boundingBox.minY;
            this.setPosition(par1, par3, par5);
        }
    }

    public float getCollisionBorderSize()
    {
        return 0.1F;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3D getLookVec()
    {
        return null;
    }

    /**
     * Called by portal blocks when an entity is within it.
     */
    public void setInPortal() {}

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double par1, double par3, double par5)
    {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
    }

    public void handleHealthUpdate(byte par1) {}

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation() {}

    public void updateCloak() {}

    /**
     * args: if j >= 0 a new item will be generated with j as itemid and; k as damage - ignored if j < 0; i 0 for held
     * item, 1-4 for armor slots 0-3, o;
     */
    public void outfitWithItem(int par1, int par2, int par3) {}

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return this.fire > 0 || this.getFlag(0);
    }

    /**
     * Returns true if the entity is riding another entity, used by render to rotate the legs to be in 'sit' position
     * for players.
     */
    public boolean isRiding()
    {
        return this.ridingEntity != null || this.getFlag(2);
    }

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking()
    {
        return this.getFlag(1);
    }

    public void setSneaking(boolean par1)
    {
        this.setFlag(1, par1);
    }

    public boolean isSprinting()
    {
        return this.getFlag(3);
    }

    /**
     * Sets the player's sprinting state.
     */
    public void setSprinting(boolean par1)
    {
        this.setFlag(3, par1);
    }

    public boolean isEating()
    {
        return this.getFlag(4);
    }

    public void setEating(boolean par1)
    {
        this.setFlag(4, par1);
    }

    /**
     * Returns true if the flag is active for the entity. Known flags: 0) is burning; 1) is sneaking; 2) is riding
     * something; 3) is sprinting; 4) is eating
     */
    protected boolean getFlag(int par1)
    {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << par1) != 0;
    }

    /**
     * Enable or disable a entity flag, see getEntityFlag to read the know flags.
     */
    protected void setFlag(int par1, boolean par2)
    {
        byte var3 = this.dataWatcher.getWatchableObjectByte(0);

        if (par2)
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(var3 | 1 << par1)));
        }
        else
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(var3 & ~(1 << par1))));
        }
    }

    public int getAir()
    {
        return this.dataWatcher.getWatchableObjectShort(1);
    }

    public void setAir(int par1)
    {
        this.dataWatcher.updateObject(1, Short.valueOf((short)par1));
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt)
    {
        this.dealFireDamage(5);
        ++this.fire;

        if (this.fire == 0)
        {
            this.setFire(8);
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(EntityLiving par1EntityLiving) {}

    /**
     * Adds velocity to push the entity out of blocks at the specified x, y, z position Args: x, y, z
     */
    protected boolean pushOutOfBlocks(double par1, double par3, double par5)
    {
        int var7 = MathHelper.floor_double(par1);
        int var8 = MathHelper.floor_double(par3);
        int var9 = MathHelper.floor_double(par5);
        double var10 = par1 - (double)var7;
        double var12 = par3 - (double)var8;
        double var14 = par5 - (double)var9;

        if (this.worldObj.isBlockNormalCube(var7, var8, var9))
        {
            boolean var16 = !this.worldObj.isBlockNormalCube(var7 - 1, var8, var9);
            boolean var17 = !this.worldObj.isBlockNormalCube(var7 + 1, var8, var9);
            boolean var18 = !this.worldObj.isBlockNormalCube(var7, var8 - 1, var9);
            boolean var19 = !this.worldObj.isBlockNormalCube(var7, var8 + 1, var9);
            boolean var20 = !this.worldObj.isBlockNormalCube(var7, var8, var9 - 1);
            boolean var21 = !this.worldObj.isBlockNormalCube(var7, var8, var9 + 1);
            byte var22 = -1;
            double var23 = 9999.0D;

            if (var16 && var10 < var23)
            {
                var23 = var10;
                var22 = 0;
            }

            if (var17 && 1.0D - var10 < var23)
            {
                var23 = 1.0D - var10;
                var22 = 1;
            }

            if (var18 && var12 < var23)
            {
                var23 = var12;
                var22 = 2;
            }

            if (var19 && 1.0D - var12 < var23)
            {
                var23 = 1.0D - var12;
                var22 = 3;
            }

            if (var20 && var14 < var23)
            {
                var23 = var14;
                var22 = 4;
            }

            if (var21 && 1.0D - var14 < var23)
            {
                var23 = 1.0D - var14;
                var22 = 5;
            }

            float var25 = this.rand.nextFloat() * 0.2F + 0.1F;

            if (var22 == 0)
            {
                this.motionX = (double)(-var25);
            }

            if (var22 == 1)
            {
                this.motionX = (double)var25;
            }

            if (var22 == 2)
            {
                this.motionY = (double)(-var25);
            }

            if (var22 == 3)
            {
                this.motionY = (double)var25;
            }

            if (var22 == 4)
            {
                this.motionZ = (double)(-var25);
            }

            if (var22 == 5)
            {
                this.motionZ = (double)var25;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets isInWeb to true
     */
    public void setInWeb()
    {
        this.isInWeb = true;
        this.fallDistance = 0.0F;
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] getParts()
    {
        return null;
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean isEntityEqual(Entity par1Entity)
    {
        return this == par1Entity;
    }

    public void func_48079_f(float par1) {}

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return true;
    }
}
