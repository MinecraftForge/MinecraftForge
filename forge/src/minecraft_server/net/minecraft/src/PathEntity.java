package net.minecraft.src;

public class PathEntity
{
    /** The actual points in the path */
    private final PathPoint[] points;
    private int field_48430_b;

    /** The total length of the path */
    private int pathLength;

    public PathEntity(PathPoint[] par1ArrayOfPathPoint)
    {
        this.points = par1ArrayOfPathPoint;
        this.pathLength = par1ArrayOfPathPoint.length;
    }

    /**
     * Directs this path to the next point in its array
     */
    public void incrementPathIndex()
    {
        ++this.field_48430_b;
    }

    /**
     * Returns true if this path has reached the end
     */
    public boolean isFinished()
    {
        return this.field_48430_b >= this.pathLength;
    }

    public PathPoint func_48425_c()
    {
        return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
    }

    public PathPoint func_48429_a(int par1)
    {
        return this.points[par1];
    }

    public int func_48424_d()
    {
        return this.pathLength;
    }

    public void func_48421_b(int par1)
    {
        this.pathLength = par1;
    }

    public int func_48423_e()
    {
        return this.field_48430_b;
    }

    public void func_48422_c(int par1)
    {
        this.field_48430_b = par1;
    }

    public Vec3D func_48428_a(Entity par1Entity, int par2)
    {
        double var3 = (double)this.points[par2].xCoord + (double)((int)(par1Entity.width + 1.0F)) * 0.5D;
        double var5 = (double)this.points[par2].yCoord;
        double var7 = (double)this.points[par2].zCoord + (double)((int)(par1Entity.width + 1.0F)) * 0.5D;
        return Vec3D.createVector(var3, var5, var7);
    }

    public Vec3D getPosition(Entity par1Entity)
    {
        return this.func_48428_a(par1Entity, this.field_48430_b);
    }

    public boolean func_48427_a(PathEntity par1PathEntity)
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
            for (int var2 = 0; var2 < this.points.length; ++var2)
            {
                if (this.points[var2].xCoord != par1PathEntity.points[var2].xCoord || this.points[var2].yCoord != par1PathEntity.points[var2].yCoord || this.points[var2].zCoord != par1PathEntity.points[var2].zCoord)
                {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean func_48426_a(Vec3D par1Vec3D)
    {
        PathPoint var2 = this.func_48425_c();
        return var2 == null ? false : var2.xCoord == (int)par1Vec3D.xCoord && var2.zCoord == (int)par1Vec3D.zCoord;
    }
}
