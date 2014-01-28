package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint
{
    // JAVADOC FIELD $$ field_75839_a
    public final int xCoord;
    // JAVADOC FIELD $$ field_75837_b
    public final int yCoord;
    // JAVADOC FIELD $$ field_75838_c
    public final int zCoord;
    // JAVADOC FIELD $$ field_75840_j
    private final int hash;
    // JAVADOC FIELD $$ field_75835_d
    int index = -1;
    // JAVADOC FIELD $$ field_75836_e
    float totalPathDistance;
    // JAVADOC FIELD $$ field_75833_f
    float distanceToNext;
    // JAVADOC FIELD $$ field_75834_g
    float distanceToTarget;
    // JAVADOC FIELD $$ field_75841_h
    PathPoint previous;
    // JAVADOC FIELD $$ field_75842_i
    public boolean isFirst;
    private static final String __OBFID = "CL_00000574";

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

    // JAVADOC METHOD $$ func_75829_a
    public float distanceTo(PathPoint par1PathPoint)
    {
        float f = (float)(par1PathPoint.xCoord - this.xCoord);
        float f1 = (float)(par1PathPoint.yCoord - this.yCoord);
        float f2 = (float)(par1PathPoint.zCoord - this.zCoord);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    public float func_75832_b(PathPoint par1PathPoint)
    {
        float f = (float)(par1PathPoint.xCoord - this.xCoord);
        float f1 = (float)(par1PathPoint.yCoord - this.yCoord);
        float f2 = (float)(par1PathPoint.zCoord - this.zCoord);
        return f * f + f1 * f1 + f2 * f2;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof PathPoint))
        {
            return false;
        }
        else
        {
            PathPoint pathpoint = (PathPoint)par1Obj;
            return this.hash == pathpoint.hash && this.xCoord == pathpoint.xCoord && this.yCoord == pathpoint.yCoord && this.zCoord == pathpoint.zCoord;
        }
    }

    public int hashCode()
    {
        return this.hash;
    }

    // JAVADOC METHOD $$ func_75831_a
    public boolean isAssigned()
    {
        return this.index >= 0;
    }

    public String toString()
    {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}