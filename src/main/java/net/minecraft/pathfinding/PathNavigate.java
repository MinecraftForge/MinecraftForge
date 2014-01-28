package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigate
{
    private EntityLiving theEntity;
    private World worldObj;
    // JAVADOC FIELD $$ field_75514_c
    private PathEntity currentPath;
    private double speed;
    // JAVADOC FIELD $$ field_75512_e
    private IAttributeInstance pathSearchRange;
    private boolean noSunPathfind;
    // JAVADOC FIELD $$ field_75510_g
    private int totalTicks;
    // JAVADOC FIELD $$ field_75520_h
    private int ticksAtLastPos;
    // JAVADOC FIELD $$ field_75521_i
    private Vec3 lastPosCheck = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
    // JAVADOC FIELD $$ field_75518_j
    private boolean canPassOpenWoodenDoors = true;
    // JAVADOC FIELD $$ field_75519_k
    private boolean canPassClosedWoodenDoors;
    // JAVADOC FIELD $$ field_75516_l
    private boolean avoidsWater;
    // JAVADOC FIELD $$ field_75517_m
    private boolean canSwim;
    private static final String __OBFID = "CL_00001627";

    public PathNavigate(EntityLiving par1EntityLiving, World par2World)
    {
        this.theEntity = par1EntityLiving;
        this.worldObj = par2World;
        this.pathSearchRange = par1EntityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
    }

    public void setAvoidsWater(boolean par1)
    {
        this.avoidsWater = par1;
    }

    public boolean getAvoidsWater()
    {
        return this.avoidsWater;
    }

    public void setBreakDoors(boolean par1)
    {
        this.canPassClosedWoodenDoors = par1;
    }

    // JAVADOC METHOD $$ func_75490_c
    public void setEnterDoors(boolean par1)
    {
        this.canPassOpenWoodenDoors = par1;
    }

    // JAVADOC METHOD $$ func_75507_c
    public boolean getCanBreakDoors()
    {
        return this.canPassClosedWoodenDoors;
    }

    // JAVADOC METHOD $$ func_75504_d
    public void setAvoidSun(boolean par1)
    {
        this.noSunPathfind = par1;
    }

    // JAVADOC METHOD $$ func_75489_a
    public void setSpeed(double par1)
    {
        this.speed = par1;
    }

    // JAVADOC METHOD $$ func_75495_e
    public void setCanSwim(boolean par1)
    {
        this.canSwim = par1;
    }

    // JAVADOC METHOD $$ func_111269_d
    public float getPathSearchRange()
    {
        return (float)this.pathSearchRange.getAttributeValue();
    }

    // JAVADOC METHOD $$ func_75488_a
    public PathEntity getPathToXYZ(double par1, double par3, double par5)
    {
        return !this.canNavigate() ? null : this.worldObj.getEntityPathToXYZ(this.theEntity, MathHelper.floor_double(par1), (int)par3, MathHelper.floor_double(par5), this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
    }

    // JAVADOC METHOD $$ func_75492_a
    public boolean tryMoveToXYZ(double par1, double par3, double par5, double par7)
    {
        PathEntity pathentity = this.getPathToXYZ((double)MathHelper.floor_double(par1), (double)((int)par3), (double)MathHelper.floor_double(par5));
        return this.setPath(pathentity, par7);
    }

    // JAVADOC METHOD $$ func_75494_a
    public PathEntity getPathToEntityLiving(Entity par1Entity)
    {
        return !this.canNavigate() ? null : this.worldObj.getPathEntityToEntity(this.theEntity, par1Entity, this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
    }

    // JAVADOC METHOD $$ func_75497_a
    public boolean tryMoveToEntityLiving(Entity par1Entity, double par2)
    {
        PathEntity pathentity = this.getPathToEntityLiving(par1Entity);
        return pathentity != null ? this.setPath(pathentity, par2) : false;
    }

    // JAVADOC METHOD $$ func_75484_a
    public boolean setPath(PathEntity par1PathEntity, double par2)
    {
        if (par1PathEntity == null)
        {
            this.currentPath = null;
            return false;
        }
        else
        {
            if (!par1PathEntity.isSamePath(this.currentPath))
            {
                this.currentPath = par1PathEntity;
            }

            if (this.noSunPathfind)
            {
                this.removeSunnyPath();
            }

            if (this.currentPath.getCurrentPathLength() == 0)
            {
                return false;
            }
            else
            {
                this.speed = par2;
                Vec3 vec3 = this.getEntityPosition();
                this.ticksAtLastPos = this.totalTicks;
                this.lastPosCheck.xCoord = vec3.xCoord;
                this.lastPosCheck.yCoord = vec3.yCoord;
                this.lastPosCheck.zCoord = vec3.zCoord;
                return true;
            }
        }
    }

    // JAVADOC METHOD $$ func_75505_d
    public PathEntity getPath()
    {
        return this.currentPath;
    }

    public void onUpdateNavigation()
    {
        ++this.totalTicks;

        if (!this.noPath())
        {
            if (this.canNavigate())
            {
                this.pathFollow();
            }

            if (!this.noPath())
            {
                Vec3 vec3 = this.currentPath.getPosition(this.theEntity);

                if (vec3 != null)
                {
                    this.theEntity.getMoveHelper().setMoveTo(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.speed);
                }
            }
        }
    }

    private void pathFollow()
    {
        Vec3 vec3 = this.getEntityPosition();
        int i = this.currentPath.getCurrentPathLength();

        for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j)
        {
            if (this.currentPath.getPathPointFromIndex(j).yCoord != (int)vec3.yCoord)
            {
                i = j;
                break;
            }
        }

        float f = this.theEntity.width * this.theEntity.width;
        int k;

        for (k = this.currentPath.getCurrentPathIndex(); k < i; ++k)
        {
            if (vec3.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, k)) < (double)f)
            {
                this.currentPath.setCurrentPathIndex(k + 1);
            }
        }

        k = MathHelper.ceiling_float_int(this.theEntity.width);
        int l = (int)this.theEntity.height + 1;
        int i1 = k;

        for (int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1)
        {
            if (this.isDirectPathBetweenPoints(vec3, this.currentPath.getVectorFromIndex(this.theEntity, j1), k, l, i1))
            {
                this.currentPath.setCurrentPathIndex(j1);
                break;
            }
        }

        if (this.totalTicks - this.ticksAtLastPos > 100)
        {
            if (vec3.squareDistanceTo(this.lastPosCheck) < 2.25D)
            {
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck.xCoord = vec3.xCoord;
            this.lastPosCheck.yCoord = vec3.yCoord;
            this.lastPosCheck.zCoord = vec3.zCoord;
        }
    }

    // JAVADOC METHOD $$ func_75500_f
    public boolean noPath()
    {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    // JAVADOC METHOD $$ func_75499_g
    public void clearPathEntity()
    {
        this.currentPath = null;
    }

    private Vec3 getEntityPosition()
    {
        return this.worldObj.getWorldVec3Pool().getVecFromPool(this.theEntity.posX, (double)this.getPathableYPos(), this.theEntity.posZ);
    }

    // JAVADOC METHOD $$ func_75503_j
    private int getPathableYPos()
    {
        if (this.theEntity.isInWater() && this.canSwim)
        {
            int i = (int)this.theEntity.boundingBox.minY;
            Block block = this.worldObj.func_147439_a(MathHelper.floor_double(this.theEntity.posX), i, MathHelper.floor_double(this.theEntity.posZ));
            int j = 0;

            do
            {
                if (block != Blocks.flowing_water && block != Blocks.water)
                {
                    return i;
                }

                ++i;
                block = this.worldObj.func_147439_a(MathHelper.floor_double(this.theEntity.posX), i, MathHelper.floor_double(this.theEntity.posZ));
                ++j;
            }
            while (j <= 16);

            return (int)this.theEntity.boundingBox.minY;
        }
        else
        {
            return (int)(this.theEntity.boundingBox.minY + 0.5D);
        }
    }

    // JAVADOC METHOD $$ func_75485_k
    private boolean canNavigate()
    {
        return this.theEntity.onGround || this.canSwim && this.isInFluid();
    }

    // JAVADOC METHOD $$ func_75506_l
    private boolean isInFluid()
    {
        return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
    }

    // JAVADOC METHOD $$ func_75487_m
    private void removeSunnyPath()
    {
        if (!this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.theEntity.posX), (int)(this.theEntity.boundingBox.minY + 0.5D), MathHelper.floor_double(this.theEntity.posZ)))
        {
            for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i)
            {
                PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);

                if (this.worldObj.canBlockSeeTheSky(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord))
                {
                    this.currentPath.setCurrentPathLength(i - 1);
                    return;
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_75493_a
    private boolean isDirectPathBetweenPoints(Vec3 par1Vec3, Vec3 par2Vec3, int par3, int par4, int par5)
    {
        int l = MathHelper.floor_double(par1Vec3.xCoord);
        int i1 = MathHelper.floor_double(par1Vec3.zCoord);
        double d0 = par2Vec3.xCoord - par1Vec3.xCoord;
        double d1 = par2Vec3.zCoord - par1Vec3.zCoord;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D)
        {
            return false;
        }
        else
        {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 *= d3;
            d1 *= d3;
            par3 += 2;
            par5 += 2;

            if (!this.isSafeToStandAt(l, (int)par1Vec3.yCoord, i1, par3, par4, par5, par1Vec3, d0, d1))
            {
                return false;
            }
            else
            {
                par3 -= 2;
                par5 -= 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double)(l * 1) - par1Vec3.xCoord;
                double d7 = (double)(i1 * 1) - par1Vec3.zCoord;

                if (d0 >= 0.0D)
                {
                    ++d6;
                }

                if (d1 >= 0.0D)
                {
                    ++d7;
                }

                d6 /= d0;
                d7 /= d1;
                int j1 = d0 < 0.0D ? -1 : 1;
                int k1 = d1 < 0.0D ? -1 : 1;
                int l1 = MathHelper.floor_double(par2Vec3.xCoord);
                int i2 = MathHelper.floor_double(par2Vec3.zCoord);
                int j2 = l1 - l;
                int k2 = i2 - i1;

                do
                {
                    if (j2 * j1 <= 0 && k2 * k1 <= 0)
                    {
                        return true;
                    }

                    if (d6 < d7)
                    {
                        d6 += d4;
                        l += j1;
                        j2 = l1 - l;
                    }
                    else
                    {
                        d7 += d5;
                        i1 += k1;
                        k2 = i2 - i1;
                    }
                }
                while (this.isSafeToStandAt(l, (int)par1Vec3.yCoord, i1, par3, par4, par5, par1Vec3, d0, d1));

                return false;
            }
        }
    }

    // JAVADOC METHOD $$ func_75483_a
    private boolean isSafeToStandAt(int par1, int par2, int par3, int par4, int par5, int par6, Vec3 par7Vec3, double par8, double par10)
    {
        int k1 = par1 - par4 / 2;
        int l1 = par3 - par6 / 2;

        if (!this.isPositionClear(k1, par2, l1, par4, par5, par6, par7Vec3, par8, par10))
        {
            return false;
        }
        else
        {
            for (int i2 = k1; i2 < k1 + par4; ++i2)
            {
                for (int j2 = l1; j2 < l1 + par6; ++j2)
                {
                    double d2 = (double)i2 + 0.5D - par7Vec3.xCoord;
                    double d3 = (double)j2 + 0.5D - par7Vec3.zCoord;

                    if (d2 * par8 + d3 * par10 >= 0.0D)
                    {
                        Block block = this.worldObj.func_147439_a(i2, par2 - 1, j2);
                        Material material = block.func_149688_o();

                        if (material == Material.field_151579_a)
                        {
                            return false;
                        }

                        if (material == Material.field_151586_h && !this.theEntity.isInWater())
                        {
                            return false;
                        }

                        if (material == Material.field_151587_i)
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    // JAVADOC METHOD $$ func_75496_b
    private boolean isPositionClear(int par1, int par2, int par3, int par4, int par5, int par6, Vec3 par7Vec3, double par8, double par10)
    {
        for (int k1 = par1; k1 < par1 + par4; ++k1)
        {
            for (int l1 = par2; l1 < par2 + par5; ++l1)
            {
                for (int i2 = par3; i2 < par3 + par6; ++i2)
                {
                    double d2 = (double)k1 + 0.5D - par7Vec3.xCoord;
                    double d3 = (double)i2 + 0.5D - par7Vec3.zCoord;

                    if (d2 * par8 + d3 * par10 >= 0.0D)
                    {
                        Block block = this.worldObj.func_147439_a(k1, l1, i2);

                        if (!block.func_149655_b(this.worldObj, k1, l1, i2))
                        {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}