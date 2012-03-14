package net.minecraft.src;

public abstract class EntityCreature extends EntityLiving
{
    private PathEntity pathToEntity;

    /** The Entity this EntityCreature is set to attack. */
    protected Entity entityToAttack;

    /**
     * returns true if a creature has attacked recently only used for creepers and skeletons
     */
    protected boolean hasAttacked = false;

    /** Used to make a creature speed up and wander away when hit. */
    protected int fleeingTick = 0;

    public EntityCreature(World par1World)
    {
        super(par1World);
    }

    /**
     * Disables a mob's ability to move on its own while true.
     */
    protected boolean isMovementCeased()
    {
    	return frozen;
    }

    protected void updateEntityActionState()
    {
    	if(frozen)
    	{
    		return;
    	}
    	
        Profiler.startSection("ai");

        if (this.fleeingTick > 0)
        {
            --this.fleeingTick;
        }

        this.hasAttacked = this.isMovementCeased();
        float var1 = 16.0F;

        if (this.entityToAttack == null)
        {
            this.entityToAttack = this.findPlayerToAttack();

            if (this.entityToAttack != null)
            {
                this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, var1, true, false, false, true);
            }
        }
        else if (!this.entityToAttack.isEntityAlive())
        {
            this.entityToAttack = null;
        }
        else
        {
            float var2 = this.entityToAttack.getDistanceToEntity(this);

            if (this.canEntityBeSeen(this.entityToAttack))
            {
                this.attackEntity(this.entityToAttack, var2);
            }
            else
            {
                this.attackBlockedEntity(this.entityToAttack, var2);
            }
        }

        Profiler.endSection();

        if (!this.hasAttacked && this.entityToAttack != null && (this.pathToEntity == null || this.rand.nextInt(20) == 0))
        {
            this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, var1, true, false, false, true);
        }
        else if (!this.hasAttacked && (this.pathToEntity == null && this.rand.nextInt(180) == 0 || this.rand.nextInt(120) == 0 || this.fleeingTick > 0) && this.entityAge < 100)
        {
            this.updateWanderPath();
        }

        int var21 = MathHelper.floor_double(this.boundingBox.minY + 0.5D);
        boolean var3 = this.isInWater();
        boolean var4 = this.handleLavaMovement();
        this.rotationPitch = 0.0F;

        if (this.pathToEntity != null && this.rand.nextInt(100) != 0)
        {
            Profiler.startSection("followpath");
            Vec3D var5 = this.pathToEntity.getCurrentNodeVec3d(this);
            double var6 = (double)(this.width * 2.0F);

            while (var5 != null && var5.squareDistanceTo(this.posX, var5.yCoord, this.posZ) < var6 * var6)
            {
                this.pathToEntity.incrementPathIndex();

                if (this.pathToEntity.isFinished())
                {
                    var5 = null;
                    this.pathToEntity = null;
                }
                else
                {
                    var5 = this.pathToEntity.getCurrentNodeVec3d(this);
                }
            }

            this.isJumping = false;

            if (var5 != null)
            {
                double var8 = var5.xCoord - this.posX;
                double var10 = var5.zCoord - this.posZ;
                double var12 = var5.yCoord - (double)var21;
                float var14 = (float)(Math.atan2(var10, var8) * 180.0D / Math.PI) - 90.0F;
                float var15 = var14 - this.rotationYaw;

                for (this.moveForward = this.moveSpeed; var15 < -180.0F; var15 += 360.0F)
                {
                    ;
                }

                while (var15 >= 180.0F)
                {
                    var15 -= 360.0F;
                }

                if (var15 > 30.0F)
                {
                    var15 = 30.0F;
                }

                if (var15 < -30.0F)
                {
                    var15 = -30.0F;
                }

                this.rotationYaw += var15;

                if (this.hasAttacked && this.entityToAttack != null)
                {
                    double var16 = this.entityToAttack.posX - this.posX;
                    double var18 = this.entityToAttack.posZ - this.posZ;
                    float var20 = this.rotationYaw;
                    this.rotationYaw = (float)(Math.atan2(var18, var16) * 180.0D / Math.PI) - 90.0F;
                    var15 = (var20 - this.rotationYaw + 90.0F) * (float)Math.PI / 180.0F;
                    this.moveStrafing = -MathHelper.sin(var15) * this.moveForward * 1.0F;
                    this.moveForward = MathHelper.cos(var15) * this.moveForward * 1.0F;
                }

                if (var12 > 0.0D)
                {
                    this.isJumping = true;
                }
            }

            if (this.entityToAttack != null)
            {
                this.faceEntity(this.entityToAttack, 30.0F, 30.0F);
            }

            if (this.isCollidedHorizontally && !this.hasPath())
            {
                this.isJumping = true;
            }

            if (this.rand.nextFloat() < 0.8F && (var3 || var4))
            {
                this.isJumping = true;
            }

            Profiler.endSection();
        }
        else
        {
            super.updateEntityActionState();
            this.pathToEntity = null;
        }
    }

    /**
     * Time remaining during which the Animal is sped up and flees.
     */
    protected void updateWanderPath()
    {
        Profiler.startSection("stroll");
        boolean var1 = false;
        int var2 = -1;
        int var3 = -1;
        int var4 = -1;
        float var5 = -99999.0F;

        for (int var6 = 0; var6 < 10; ++var6)
        {
            int var7 = MathHelper.floor_double(this.posX + (double)this.rand.nextInt(13) - 6.0D);
            int var8 = MathHelper.floor_double(this.posY + (double)this.rand.nextInt(7) - 3.0D);
            int var9 = MathHelper.floor_double(this.posZ + (double)this.rand.nextInt(13) - 6.0D);
            float var10 = this.getBlockPathWeight(var7, var8, var9);

            if (var10 > var5)
            {
                var5 = var10;
                var2 = var7;
                var3 = var8;
                var4 = var9;
                var1 = true;
            }
        }

        if (var1)
        {
            this.pathToEntity = this.worldObj.getEntityPathToXYZ(this, var2, var3, var4, 10.0F, true, false, false, true);
        }

        Profiler.endSection();
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2) {}

    /**
     * Used when an entity is close enough to attack but cannot be seen (Creeper de-fuse)
     */
    protected void attackBlockedEntity(Entity par1Entity, float par2) {}

    /**
     * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
     * Args: x, y, z
     */
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return 0.0F;
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        return null;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        int var1 = MathHelper.floor_double(this.posX);
        int var2 = MathHelper.floor_double(this.boundingBox.minY);
        int var3 = MathHelper.floor_double(this.posZ);
        return super.getCanSpawnHere() && this.getBlockPathWeight(var1, var2, var3) >= 0.0F;
    }

    /**
     * Returns true if entity has a path to follow
     */
    public boolean hasPath()
    {
        return this.pathToEntity != null;
    }

    /**
     * sets the Entities walk path in EntityCreature
     */
    public void setPathToEntity(PathEntity par1PathEntity)
    {
        this.pathToEntity = par1PathEntity;
    }

    /**
     * Returns current entities target
     */
    public Entity getEntityToAttack()
    {
        return this.entityToAttack;
    }

    /**
     * Sets entities target to attack
     */
    public void setTarget(Entity par1Entity)
    {
        this.entityToAttack = par1Entity;
    }

    /**
     * This method return a value to be applyed directly to entity speed, this factor is less than 1 when a slowdown
     * potion effect is applyed, more than 1 when a haste potion effect is applyed and 2 for fleeing entities.
     */
    protected float getSpeedModifier()
    {
        if (this.isAIEnabled())
        {
            return 1.0F;
        }
        else
        {
            float var1 = super.getSpeedModifier();

            if (this.fleeingTick > 0)
            {
                var1 *= 2.0F;
            }

            return var1;
        }
    }
}
