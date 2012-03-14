package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityDragon extends EntityDragonBase
{
    public double targetX;
    public double targetY;
    public double targetZ;
    public double[][] field_40144_d = new double[64][3];
    public int field_40145_e = -1;

    /** An array containing all body parts of this dragon */
    public EntityDragonPart[] dragonPartArray;

    /** The head bounding box of a dragon */
    public EntityDragonPart dragonPartHead;

    /** The body bounding box of a dragon */
    public EntityDragonPart dragonPartBody;
    public EntityDragonPart dragonPartTail1;
    public EntityDragonPart dragonPartTail2;
    public EntityDragonPart dragonPartTail3;
    public EntityDragonPart dragonPartWing1;
    public EntityDragonPart dragonPartWing2;
    public float field_40149_n = 0.0F;
    public float field_40150_o = 0.0F;
    public boolean field_40160_p = false;
    public boolean field_40159_q = false;
    private Entity target;
    public int field_40158_r = 0;

    /** The current endercrystal that is healing this dragon */
    public EntityEnderCrystal healingEnderCrystal = null;

    public EntityDragon(World par1World)
    {
        super(par1World);
        this.dragonPartArray = new EntityDragonPart[] {this.dragonPartHead = new EntityDragonPart(this, "head", 6.0F, 6.0F), this.dragonPartBody = new EntityDragonPart(this, "body", 8.0F, 8.0F), this.dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F), this.dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F)};
        this.maxHealth = 200;
        this.setEntityHealth(this.maxHealth);
        this.texture = "/mob/enderdragon/ender.png";
        this.setSize(16.0F, 8.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.targetY = 100.0D;
        this.ignoreFrustrumCheck = true;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Integer(this.maxHealth));
    }

    public double[] func_40139_a(int par1, float par2)
    {
        if (this.health <= 0)
        {
            par2 = 0.0F;
        }

        par2 = 1.0F - par2;
        int var3 = this.field_40145_e - par1 * 1 & 63;
        int var4 = this.field_40145_e - par1 * 1 - 1 & 63;
        double[] var5 = new double[3];
        double var6 = this.field_40144_d[var3][0];
        double var8;

        for (var8 = this.field_40144_d[var4][0] - var6; var8 < -180.0D; var8 += 360.0D)
        {
            ;
        }

        while (var8 >= 180.0D)
        {
            var8 -= 360.0D;
        }

        var5[0] = var6 + var8 * (double)par2;
        var6 = this.field_40144_d[var3][1];
        var8 = this.field_40144_d[var4][1] - var6;
        var5[1] = var6 + var8 * (double)par2;
        var5[2] = this.field_40144_d[var3][2] + (this.field_40144_d[var4][2] - this.field_40144_d[var3][2]) * (double)par2;
        return var5;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        this.field_40149_n = this.field_40150_o;

        if (!this.worldObj.isRemote)
        {
            this.dataWatcher.updateObject(16, Integer.valueOf(this.health));
        }

        float var1;
        float var3;
        float var26;

        if (this.health <= 0)
        {
            var1 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            var26 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            var3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle("largeexplode", this.posX + (double)var1, this.posY + 2.0D + (double)var26, this.posZ + (double)var3, 0.0D, 0.0D, 0.0D);
        }
        else
        {
            this.updateDragonEnderCrystal();
            var1 = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
            var1 *= (float)Math.pow(2.0D, this.motionY);

            if (this.field_40159_q)
            {
                this.field_40150_o += var1 * 0.5F;
            }
            else
            {
                this.field_40150_o += var1;
            }

            while (this.rotationYaw >= 180.0F)
            {
                this.rotationYaw -= 360.0F;
            }

            while (this.rotationYaw < -180.0F)
            {
                this.rotationYaw += 360.0F;
            }

            if (this.field_40145_e < 0)
            {
                for (int var2 = 0; var2 < this.field_40144_d.length; ++var2)
                {
                    this.field_40144_d[var2][0] = (double)this.rotationYaw;
                    this.field_40144_d[var2][1] = this.posY;
                }
            }

            if (++this.field_40145_e == this.field_40144_d.length)
            {
                this.field_40145_e = 0;
            }

            this.field_40144_d[this.field_40145_e][0] = (double)this.rotationYaw;
            this.field_40144_d[this.field_40145_e][1] = this.posY;
            double var4;
            double var6;
            double var8;
            double var25;
            float var33;

            if (this.worldObj.isRemote)
            {
                if (this.newPosRotationIncrements > 0)
                {
                    var25 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
                    var4 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
                    var6 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;

                    for (var8 = this.newRotationYaw - (double)this.rotationYaw; var8 < -180.0D; var8 += 360.0D)
                    {
                        ;
                    }

                    while (var8 >= 180.0D)
                    {
                        var8 -= 360.0D;
                    }

                    this.rotationYaw = (float)((double)this.rotationYaw + var8 / (double)this.newPosRotationIncrements);
                    this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
                    --this.newPosRotationIncrements;
                    this.setPosition(var25, var4, var6);
                    this.setRotation(this.rotationYaw, this.rotationPitch);
                }
            }
            else
            {
                var25 = this.targetX - this.posX;
                var4 = this.targetY - this.posY;
                var6 = this.targetZ - this.posZ;
                var8 = var25 * var25 + var4 * var4 + var6 * var6;

                if (this.target != null)
                {
                    this.targetX = this.target.posX;
                    this.targetZ = this.target.posZ;
                    double var10 = this.targetX - this.posX;
                    double var12 = this.targetZ - this.posZ;
                    double var14 = Math.sqrt(var10 * var10 + var12 * var12);
                    double var16 = 0.4000000059604645D + var14 / 80.0D - 1.0D;

                    if (var16 > 10.0D)
                    {
                        var16 = 10.0D;
                    }

                    this.targetY = this.target.boundingBox.minY + var16;
                }
                else
                {
                    this.targetX += this.rand.nextGaussian() * 2.0D;
                    this.targetZ += this.rand.nextGaussian() * 2.0D;
                }

                if (this.field_40160_p || var8 < 100.0D || var8 > 22500.0D || this.isCollidedHorizontally || this.isCollidedVertically)
                {
                    this.func_41037_w();
                }

                var4 /= (double)MathHelper.sqrt_double(var25 * var25 + var6 * var6);
                var33 = 0.6F;

                if (var4 < (double)(-var33))
                {
                    var4 = (double)(-var33);
                }

                if (var4 > (double)var33)
                {
                    var4 = (double)var33;
                }

                for (this.motionY += var4 * 0.10000000149011612D; this.rotationYaw < -180.0F; this.rotationYaw += 360.0F)
                {
                    ;
                }

                while (this.rotationYaw >= 180.0F)
                {
                    this.rotationYaw -= 360.0F;
                }

                double var11 = 180.0D - Math.atan2(var25, var6) * 180.0D / Math.PI;
                double var13;

                for (var13 = var11 - (double)this.rotationYaw; var13 < -180.0D; var13 += 360.0D)
                {
                    ;
                }

                while (var13 >= 180.0D)
                {
                    var13 -= 360.0D;
                }

                if (var13 > 50.0D)
                {
                    var13 = 50.0D;
                }

                if (var13 < -50.0D)
                {
                    var13 = -50.0D;
                }

                Vec3D var15 = Vec3D.createVector(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
                Vec3D var40 = Vec3D.createVector((double)MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F), this.motionY, (double)(-MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F))).normalize();
                float var17 = (float)(var40.dotProduct(var15) + 0.5D) / 1.5F;

                if (var17 < 0.0F)
                {
                    var17 = 0.0F;
                }

                this.randomYawVelocity *= 0.8F;
                float var18 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
                double var19 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0D + 1.0D;

                if (var19 > 40.0D)
                {
                    var19 = 40.0D;
                }

                this.randomYawVelocity = (float)((double)this.randomYawVelocity + var13 * (0.699999988079071D / var19 / (double)var18));
                this.rotationYaw += this.randomYawVelocity * 0.1F;
                float var21 = (float)(2.0D / (var19 + 1.0D));
                float var22 = 0.06F;
                this.moveFlying(0.0F, -1.0F, var22 * (var17 * var21 + (1.0F - var21)));

                if (this.field_40159_q)
                {
                    this.moveEntity(this.motionX * 0.800000011920929D, this.motionY * 0.800000011920929D, this.motionZ * 0.800000011920929D);
                }
                else
                {
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                }

                Vec3D var23 = Vec3D.createVector(this.motionX, this.motionY, this.motionZ).normalize();
                float var24 = (float)(var23.dotProduct(var40) + 1.0D) / 2.0F;
                var24 = 0.8F + 0.15F * var24;
                this.motionX *= (double)var24;
                this.motionZ *= (double)var24;
                this.motionY *= 0.9100000262260437D;
            }

            this.renderYawOffset = this.rotationYaw;
            this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
            this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
            this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
            this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
            this.dragonPartBody.height = 3.0F;
            this.dragonPartBody.width = 5.0F;
            this.dragonPartWing1.height = 2.0F;
            this.dragonPartWing1.width = 4.0F;
            this.dragonPartWing2.height = 3.0F;
            this.dragonPartWing2.width = 4.0F;
            var26 = (float)(this.func_40139_a(5, 1.0F)[1] - this.func_40139_a(10, 1.0F)[1]) * 10.0F / 180.0F * (float)Math.PI;
            var3 = MathHelper.cos(var26);
            float var28 = -MathHelper.sin(var26);
            float var5 = this.rotationYaw * (float)Math.PI / 180.0F;
            float var27 = MathHelper.sin(var5);
            float var7 = MathHelper.cos(var5);
            this.dragonPartBody.onUpdate();
            this.dragonPartBody.setLocationAndAngles(this.posX + (double)(var27 * 0.5F), this.posY, this.posZ - (double)(var7 * 0.5F), 0.0F, 0.0F);
            this.dragonPartWing1.onUpdate();
            this.dragonPartWing1.setLocationAndAngles(this.posX + (double)(var7 * 4.5F), this.posY + 2.0D, this.posZ + (double)(var27 * 4.5F), 0.0F, 0.0F);
            this.dragonPartWing2.onUpdate();
            this.dragonPartWing2.setLocationAndAngles(this.posX - (double)(var7 * 4.5F), this.posY + 2.0D, this.posZ - (double)(var27 * 4.5F), 0.0F, 0.0F);

            if (!this.worldObj.isRemote)
            {
                this.func_41033_v();
            }

            if (!this.worldObj.isRemote && this.maxHurtTime == 0)
            {
                this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.boundingBox.expand(1.0D, 1.0D, 1.0D)));
            }

            double[] var30 = this.func_40139_a(5, 1.0F);
            double[] var9 = this.func_40139_a(0, 1.0F);
            var33 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            float var32 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            this.dragonPartHead.onUpdate();
            this.dragonPartHead.setLocationAndAngles(this.posX + (double)(var33 * 5.5F * var3), this.posY + (var9[1] - var30[1]) * 1.0D + (double)(var28 * 5.5F), this.posZ - (double)(var32 * 5.5F * var3), 0.0F, 0.0F);

            for (int var29 = 0; var29 < 3; ++var29)
            {
                EntityDragonPart var31 = null;

                if (var29 == 0)
                {
                    var31 = this.dragonPartTail1;
                }

                if (var29 == 1)
                {
                    var31 = this.dragonPartTail2;
                }

                if (var29 == 2)
                {
                    var31 = this.dragonPartTail3;
                }

                double[] var35 = this.func_40139_a(12 + var29 * 2, 1.0F);
                float var34 = this.rotationYaw * (float)Math.PI / 180.0F + this.simplifyAngle(var35[0] - var30[0]) * (float)Math.PI / 180.0F * 1.0F;
                float var37 = MathHelper.sin(var34);
                float var38 = MathHelper.cos(var34);
                float var36 = 1.5F;
                float var39 = (float)(var29 + 1) * 2.0F;
                var31.onUpdate();
                var31.setLocationAndAngles(this.posX - (double)((var27 * var36 + var37 * var39) * var3), this.posY + (var35[1] - var30[1]) * 1.0D - (double)((var39 + var36) * var28) + 1.5D, this.posZ + (double)((var7 * var36 + var38 * var39) * var3), 0.0F, 0.0F);
            }

            if (!this.worldObj.isRemote)
            {
                this.field_40159_q = this.destroyBlocksInAABB(this.dragonPartHead.boundingBox) | this.destroyBlocksInAABB(this.dragonPartBody.boundingBox);
            }
        }
    }

    /**
     * Updates the state of the enderdragon's current endercrystal.
     */
    private void updateDragonEnderCrystal()
    {
        if (this.healingEnderCrystal != null)
        {
            if (this.healingEnderCrystal.isDead)
            {
                if (!this.worldObj.isRemote)
                {
                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.explosion, 10);
                }

                this.healingEnderCrystal = null;
            }
            else if (this.ticksExisted % 10 == 0 && this.health < this.maxHealth)
            {
                ++this.health;
            }
        }

        if (this.rand.nextInt(10) == 0)
        {
            float var1 = 32.0F;
            List var2 = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.boundingBox.expand((double)var1, (double)var1, (double)var1));
            EntityEnderCrystal var3 = null;
            double var4 = Double.MAX_VALUE;
            Iterator var6 = var2.iterator();

            while (var6.hasNext())
            {
                Entity var7 = (Entity)var6.next();
                double var8 = var7.getDistanceSqToEntity(this);

                if (var8 < var4)
                {
                    var4 = var8;
                    var3 = (EntityEnderCrystal)var7;
                }
            }

            this.healingEnderCrystal = var3;
        }
    }

    private void func_41033_v() {}

    /**
     * Pushes all entities inside the list away from the enderdragon.
     */
    private void collideWithEntities(List par1List)
    {
        double var2 = (this.dragonPartBody.boundingBox.minX + this.dragonPartBody.boundingBox.maxX) / 2.0D;
        double var4 = (this.dragonPartBody.boundingBox.minZ + this.dragonPartBody.boundingBox.maxZ) / 2.0D;
        Iterator var6 = par1List.iterator();

        while (var6.hasNext())
        {
            Entity var7 = (Entity)var6.next();

            if (var7 instanceof EntityLiving)
            {
                double var8 = var7.posX - var2;
                double var10 = var7.posZ - var4;
                double var12 = var8 * var8 + var10 * var10;
                var7.addVelocity(var8 / var12 * 4.0D, 0.20000000298023224D, var10 / var12 * 4.0D);
            }
        }
    }

    /**
     * Attacks all entities inside this list, dealing 5 hearts of damage.
     */
    private void attackEntitiesInList(List par1List)
    {
        for (int var2 = 0; var2 < par1List.size(); ++var2)
        {
            Entity var3 = (Entity)par1List.get(var2);

            if (var3 instanceof EntityLiving)
            {
                var3.attackEntityFrom(DamageSource.causeMobDamage(this), 10);
            }
        }
    }

    private void func_41037_w()
    {
        this.field_40160_p = false;

        if (this.rand.nextInt(2) == 0 && this.worldObj.playerEntities.size() > 0)
        {
            this.target = (Entity)this.worldObj.playerEntities.get(this.rand.nextInt(this.worldObj.playerEntities.size()));
        }
        else
        {
            boolean var1 = false;

            do
            {
                this.targetX = 0.0D;
                this.targetY = (double)(70.0F + this.rand.nextFloat() * 50.0F);
                this.targetZ = 0.0D;
                this.targetX += (double)(this.rand.nextFloat() * 120.0F - 60.0F);
                this.targetZ += (double)(this.rand.nextFloat() * 120.0F - 60.0F);
                double var2 = this.posX - this.targetX;
                double var4 = this.posY - this.targetY;
                double var6 = this.posZ - this.targetZ;
                var1 = var2 * var2 + var4 * var4 + var6 * var6 > 100.0D;
            }
            while (!var1);

            this.target = null;
        }
    }

    /**
     * Simplifies the value of a number by adding/subtracting 180 to the point that the number is between -180 and 180.
     */
    private float simplifyAngle(double par1)
    {
        while (par1 >= 180.0D)
        {
            par1 -= 360.0D;
        }

        while (par1 < -180.0D)
        {
            par1 += 360.0D;
        }

        return (float)par1;
    }

    /**
     * Destroys all blocks that aren't associated with 'The End' inside the given bounding box.
     */
    private boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB)
    {
        int var2 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var3 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.maxY);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
        boolean var8 = false;
        boolean var9 = false;

        for (int var10 = var2; var10 <= var5; ++var10)
        {
            for (int var11 = var3; var11 <= var6; ++var11)
            {
                for (int var12 = var4; var12 <= var7; ++var12)
                {
                    int var13 = this.worldObj.getBlockId(var10, var11, var12);

                    if (var13 != 0)
                    {
                        if (var13 != Block.obsidian.blockID && var13 != Block.whiteStone.blockID && var13 != Block.bedrock.blockID)
                        {
                            var9 = true;
                            this.worldObj.setBlockWithNotify(var10, var11, var12, 0);
                        }
                        else
                        {
                            var8 = true;
                        }
                    }
                }
            }
        }

        if (var9)
        {
            double var16 = par1AxisAlignedBB.minX + (par1AxisAlignedBB.maxX - par1AxisAlignedBB.minX) * (double)this.rand.nextFloat();
            double var17 = par1AxisAlignedBB.minY + (par1AxisAlignedBB.maxY - par1AxisAlignedBB.minY) * (double)this.rand.nextFloat();
            double var14 = par1AxisAlignedBB.minZ + (par1AxisAlignedBB.maxZ - par1AxisAlignedBB.minZ) * (double)this.rand.nextFloat();
            this.worldObj.spawnParticle("largeexplode", var16, var17, var14, 0.0D, 0.0D, 0.0D);
        }

        return var8;
    }

    public boolean attackEntityFromPart(EntityDragonPart par1EntityDragonPart, DamageSource par2DamageSource, int par3)
    {
        if (par1EntityDragonPart != this.dragonPartHead)
        {
            par3 = par3 / 4 + 1;
        }

        float var4 = this.rotationYaw * (float)Math.PI / 180.0F;
        float var5 = MathHelper.sin(var4);
        float var6 = MathHelper.cos(var4);
        this.targetX = this.posX + (double)(var5 * 5.0F) + (double)((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.targetY = this.posY + (double)(this.rand.nextFloat() * 3.0F) + 1.0D;
        this.targetZ = this.posZ - (double)(var6 * 5.0F) + (double)((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.target = null;

        if (par2DamageSource.getEntity() instanceof EntityPlayer || par2DamageSource == DamageSource.explosion)
        {
            this.superAttackFrom(par2DamageSource, par3);
        }

        return true;
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate()
    {
        ++this.field_40158_r;

        if (this.field_40158_r >= 180 && this.field_40158_r <= 200)
        {
            float var1 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float var2 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float var3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle("hugeexplosion", this.posX + (double)var1, this.posY + 2.0D + (double)var2, this.posZ + (double)var3, 0.0D, 0.0D, 0.0D);
        }

        int var4;
        int var5;

        if (!this.worldObj.isRemote && this.field_40158_r > 150 && this.field_40158_r % 5 == 0)
        {
            var4 = 1000;

            while (var4 > 0)
            {
                var5 = EntityXPOrb.getXPSplit(var4);
                var4 -= var5;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var5));
            }
        }

        this.moveEntity(0.0D, 0.10000000149011612D, 0.0D);
        this.renderYawOffset = this.rotationYaw += 20.0F;

        if (this.field_40158_r == 200)
        {
            var4 = 10000;

            while (var4 > 0)
            {
                var5 = EntityXPOrb.getXPSplit(var4);
                var4 -= var5;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var5));
            }

            this.createEnderPortal(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
            this.onEntityDeath();
            this.setEntityDead();
        }
    }

    /**
     * Creates the ender portal leading back to the normal world after defeating the enderdragon.
     */
    private void createEnderPortal(int par1, int par2)
    {
        byte var3 = 64;
        BlockEndPortal.bossDefeated = true;
        byte var4 = 4;

        for (int var5 = var3 - 1; var5 <= var3 + 32; ++var5)
        {
            for (int var6 = par1 - var4; var6 <= par1 + var4; ++var6)
            {
                for (int var7 = par2 - var4; var7 <= par2 + var4; ++var7)
                {
                    double var8 = (double)(var6 - par1);
                    double var10 = (double)(var7 - par2);
                    double var12 = (double)MathHelper.sqrt_double(var8 * var8 + var10 * var10);

                    if (var12 <= (double)var4 - 0.5D)
                    {
                        if (var5 < var3)
                        {
                            if (var12 <= (double)(var4 - 1) - 0.5D)
                            {
                                this.worldObj.setBlockWithNotify(var6, var5, var7, Block.bedrock.blockID);
                            }
                        }
                        else if (var5 > var3)
                        {
                            this.worldObj.setBlockWithNotify(var6, var5, var7, 0);
                        }
                        else if (var12 > (double)(var4 - 1) - 0.5D)
                        {
                            this.worldObj.setBlockWithNotify(var6, var5, var7, Block.bedrock.blockID);
                        }
                        else
                        {
                            this.worldObj.setBlockWithNotify(var6, var5, var7, Block.endPortal.blockID);
                        }
                    }
                }
            }
        }

        this.worldObj.setBlockWithNotify(par1, var3 + 0, par2, Block.bedrock.blockID);
        this.worldObj.setBlockWithNotify(par1, var3 + 1, par2, Block.bedrock.blockID);
        this.worldObj.setBlockWithNotify(par1, var3 + 2, par2, Block.bedrock.blockID);
        this.worldObj.setBlockWithNotify(par1 - 1, var3 + 2, par2, Block.torchWood.blockID);
        this.worldObj.setBlockWithNotify(par1 + 1, var3 + 2, par2, Block.torchWood.blockID);
        this.worldObj.setBlockWithNotify(par1, var3 + 2, par2 - 1, Block.torchWood.blockID);
        this.worldObj.setBlockWithNotify(par1, var3 + 2, par2 + 1, Block.torchWood.blockID);
        this.worldObj.setBlockWithNotify(par1, var3 + 3, par2, Block.bedrock.blockID);
        this.worldObj.setBlockWithNotify(par1, var3 + 4, par2, Block.dragonEgg.blockID);
        BlockEndPortal.bossDefeated = false;
    }

    protected void despawnEntity() {}

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] getParts()
    {
        return this.dragonPartArray;
    }

    public boolean canBeCollidedWith()
    {
        return false;
    }
}
