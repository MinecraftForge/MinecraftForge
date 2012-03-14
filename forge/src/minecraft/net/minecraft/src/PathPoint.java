package net.minecraft.src;

public class PathPoint
{
    /** The x coordinate of this point */
    public final int xCoord;

    /** The y coordinate of this point */
    public final int yCoord;

    /** The z coordinate of this point */
    public final int zCoord;

    /** A hash of the coordinates used to identify this point */
    private final int hash;

    /** The index of this point in its assigned path */
    int index = -1;

    /** The distance along the path to this point */
    float totalPathDistance;

    /** The linear distance to the next point */
    float distanceToNext;

    /** The distance to the target */
    float distanceToTarget;

    /** The point preceding this in its assigned path */
    PathPoint previous;

    /** Indicates this is the origin */
    public boolean isFirst = false;

    public PathPoint(int par1, int par2, int par3)
    {
        this.xCoord = par1;
        this.yCoord = par2;
        this.zCoord = par3;
        this.hash = makeHash(par1, par2, par3);
    }

    public static int makeHash(int par0, int par1, int par2)
    {
        return par1 & 255 | (par0 & 32767) << 8 | (par2 & 32767) << 24 | (par0 < 0 ? Integer.MIN_VALUE : 0) | (par2 < 0 ? 32768 : 0);
    }

    /**
     * Returns the linear distance to another path point
     */
    public float distanceTo(PathPoint par1PathPoint)
    {
        float var2 = (float)(par1PathPoint.xCoord - this.xCoord);
        float var3 = (float)(par1PathPoint.yCoord - this.yCoord);
        float var4 = (float)(par1PathPoint.zCoord - this.zCoord);
        return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof PathPoint))
        {
            return false;
        }
        else
        {
            PathPoint var2 = (PathPoint)par1Obj;
            return this.hash == var2.hash && this.xCoord == var2.xCoord && this.yCoord == var2.yCoord && this.zCoord == var2.zCoord;
        }
    }

    public int hashCode()
    {
        return this.hash;
    }

    /**
     * Returns true if this point has already been assigned to a path
     */
    public boolean isAssigned()
    {
        return this.index >= 0;
    }

    public String toString()
    {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}
