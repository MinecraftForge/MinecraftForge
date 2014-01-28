package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class PathEntity
{
    // JAVADOC FIELD $$ field_75884_a
    private final PathPoint[] points;
    // JAVADOC FIELD $$ field_75882_b
    private int currentPathIndex;
    // JAVADOC FIELD $$ field_75883_c
    private int pathLength;
    private static final String __OBFID = "CL_00000575";

    public PathEntity(PathPoint[] par1ArrayOfPathPoint)
    {
        this.points = par1ArrayOfPathPoint;
        this.pathLength = par1ArrayOfPathPoint.length;
    }

    // JAVADOC METHOD $$ func_75875_a
    public void incrementPathIndex()
    {
        ++this.currentPathIndex;
    }

    // JAVADOC METHOD $$ func_75879_b
    public boolean isFinished()
    {
        return this.currentPathIndex >= this.pathLength;
    }

    // JAVADOC METHOD $$ func_75870_c
    public PathPoint getFinalPathPoint()
    {
        return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
    }

    // JAVADOC METHOD $$ func_75877_a
    public PathPoint getPathPointFromIndex(int par1)
    {
        return this.points[par1];
    }

    public int getCurrentPathLength()
    {
        return this.pathLength;
    }

    public void setCurrentPathLength(int par1)
    {
        this.pathLength = par1;
    }

    public int getCurrentPathIndex()
    {
        return this.currentPathIndex;
    }

    public void setCurrentPathIndex(int par1)
    {
        this.currentPathIndex = par1;
    }

    // JAVADOC METHOD $$ func_75881_a
    public Vec3 getVectorFromIndex(Entity par1Entity, int par2)
    {
        double d0 = (double)this.points[par2].xCoord + (double)((int)(par1Entity.width + 1.0F)) * 0.5D;
        double d1 = (double)this.points[par2].yCoord;
        double d2 = (double)this.points[par2].zCoord + (double)((int)(par1Entity.width + 1.0F)) * 0.5D;
        return par1Entity.worldObj.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
    }

    // JAVADOC METHOD $$ func_75878_a
    public Vec3 getPosition(Entity par1Entity)
    {
        return this.getVectorFromIndex(par1Entity, this.currentPathIndex);
    }

    // JAVADOC METHOD $$ func_75876_a
    public boolean isSamePath(PathEntity par1PathEntity)
    {
        if (par1PathEntity == null)
        {
            return false;
        }
        else if (par1PathEntity.points.length != this.points.length)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < this.points.length; ++i)
            {
                if (this.points[i].xCoord != par1PathEntity.points[i].xCoord || this.points[i].yCoord != par1PathEntity.points[i].yCoord || this.points[i].zCoord != par1PathEntity.points[i].zCoord)
                {
                    return false;
                }
            }

            return true;
        }
    }

    // JAVADOC METHOD $$ func_75880_b
    public boolean isDestinationSame(Vec3 par1Vec3)
    {
        PathPoint pathpoint = this.getFinalPathPoint();
        return pathpoint == null ? false : pathpoint.xCoord == (int)par1Vec3.xCoord && pathpoint.zCoord == (int)par1Vec3.zCoord;
    }
}