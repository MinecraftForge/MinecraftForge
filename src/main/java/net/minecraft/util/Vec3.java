package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Vec3
{
    // JAVADOC FIELD $$ field_82592_a
    public static final Vec3Pool fakePool = new Vec3Pool(-1, -1);
    public final Vec3Pool myVec3LocalPool;
    // JAVADOC FIELD $$ field_72450_a
    public double xCoord;
    // JAVADOC FIELD $$ field_72448_b
    public double yCoord;
    // JAVADOC FIELD $$ field_72449_c
    public double zCoord;
    private static final String __OBFID = "CL_00000612";

    // JAVADOC METHOD $$ func_72443_a
    public static Vec3 createVectorHelper(double par0, double par2, double par4)
    {
        return new Vec3(fakePool, par0, par2, par4);
    }

    protected Vec3(Vec3Pool par1Vec3Pool, double par2, double par4, double par6)
    {
        if (par2 == -0.0D)
        {
            par2 = 0.0D;
        }

        if (par4 == -0.0D)
        {
            par4 = 0.0D;
        }

        if (par6 == -0.0D)
        {
            par6 = 0.0D;
        }

        this.xCoord = par2;
        this.yCoord = par4;
        this.zCoord = par6;
        this.myVec3LocalPool = par1Vec3Pool;
    }

    // JAVADOC METHOD $$ func_72439_b
    protected Vec3 setComponents(double par1, double par3, double par5)
    {
        this.xCoord = par1;
        this.yCoord = par3;
        this.zCoord = par5;
        return this;
    }

    // JAVADOC METHOD $$ func_72444_a
    @SideOnly(Side.CLIENT)
    public Vec3 subtract(Vec3 par1Vec3)
    {
        return this.myVec3LocalPool.getVecFromPool(par1Vec3.xCoord - this.xCoord, par1Vec3.yCoord - this.yCoord, par1Vec3.zCoord - this.zCoord);
    }

    // JAVADOC METHOD $$ func_72432_b
    public Vec3 normalize()
    {
        double d0 = (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return d0 < 1.0E-4D ? this.myVec3LocalPool.getVecFromPool(0.0D, 0.0D, 0.0D) : this.myVec3LocalPool.getVecFromPool(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
    }

    public double dotProduct(Vec3 par1Vec3)
    {
        return this.xCoord * par1Vec3.xCoord + this.yCoord * par1Vec3.yCoord + this.zCoord * par1Vec3.zCoord;
    }

    // JAVADOC METHOD $$ func_72431_c
    @SideOnly(Side.CLIENT)
    public Vec3 crossProduct(Vec3 par1Vec3)
    {
        return this.myVec3LocalPool.getVecFromPool(this.yCoord * par1Vec3.zCoord - this.zCoord * par1Vec3.yCoord, this.zCoord * par1Vec3.xCoord - this.xCoord * par1Vec3.zCoord, this.xCoord * par1Vec3.yCoord - this.yCoord * par1Vec3.xCoord);
    }

    // JAVADOC METHOD $$ func_72441_c
    public Vec3 addVector(double par1, double par3, double par5)
    {
        return this.myVec3LocalPool.getVecFromPool(this.xCoord + par1, this.yCoord + par3, this.zCoord + par5);
    }

    // JAVADOC METHOD $$ func_72438_d
    public double distanceTo(Vec3 par1Vec3)
    {
        double d0 = par1Vec3.xCoord - this.xCoord;
        double d1 = par1Vec3.yCoord - this.yCoord;
        double d2 = par1Vec3.zCoord - this.zCoord;
        return (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    // JAVADOC METHOD $$ func_72436_e
    public double squareDistanceTo(Vec3 par1Vec3)
    {
        double d0 = par1Vec3.xCoord - this.xCoord;
        double d1 = par1Vec3.yCoord - this.yCoord;
        double d2 = par1Vec3.zCoord - this.zCoord;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    // JAVADOC METHOD $$ func_72445_d
    public double squareDistanceTo(double par1, double par3, double par5)
    {
        double d3 = par1 - this.xCoord;
        double d4 = par3 - this.yCoord;
        double d5 = par5 - this.zCoord;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    // JAVADOC METHOD $$ func_72433_c
    public double lengthVector()
    {
        return (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    // JAVADOC METHOD $$ func_72429_b
    public Vec3 getIntermediateWithXValue(Vec3 par1Vec3, double par2)
    {
        double d1 = par1Vec3.xCoord - this.xCoord;
        double d2 = par1Vec3.yCoord - this.yCoord;
        double d3 = par1Vec3.zCoord - this.zCoord;

        if (d1 * d1 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d4 = (par2 - this.xCoord) / d1;
            return d4 >= 0.0D && d4 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    // JAVADOC METHOD $$ func_72435_c
    public Vec3 getIntermediateWithYValue(Vec3 par1Vec3, double par2)
    {
        double d1 = par1Vec3.xCoord - this.xCoord;
        double d2 = par1Vec3.yCoord - this.yCoord;
        double d3 = par1Vec3.zCoord - this.zCoord;

        if (d2 * d2 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d4 = (par2 - this.yCoord) / d2;
            return d4 >= 0.0D && d4 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    // JAVADOC METHOD $$ func_72434_d
    public Vec3 getIntermediateWithZValue(Vec3 par1Vec3, double par2)
    {
        double d1 = par1Vec3.xCoord - this.xCoord;
        double d2 = par1Vec3.yCoord - this.yCoord;
        double d3 = par1Vec3.zCoord - this.zCoord;

        if (d3 * d3 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d4 = (par2 - this.zCoord) / d3;
            return d4 >= 0.0D && d4 <= 1.0D ? this.myVec3LocalPool.getVecFromPool(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }

    public String toString()
    {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    // JAVADOC METHOD $$ func_72440_a
    public void rotateAroundX(float par1)
    {
        float f1 = MathHelper.cos(par1);
        float f2 = MathHelper.sin(par1);
        double d0 = this.xCoord;
        double d1 = this.yCoord * (double)f1 + this.zCoord * (double)f2;
        double d2 = this.zCoord * (double)f1 - this.yCoord * (double)f2;
        this.xCoord = d0;
        this.yCoord = d1;
        this.zCoord = d2;
    }

    // JAVADOC METHOD $$ func_72442_b
    public void rotateAroundY(float par1)
    {
        float f1 = MathHelper.cos(par1);
        float f2 = MathHelper.sin(par1);
        double d0 = this.xCoord * (double)f1 + this.zCoord * (double)f2;
        double d1 = this.yCoord;
        double d2 = this.zCoord * (double)f1 - this.xCoord * (double)f2;
        this.xCoord = d0;
        this.yCoord = d1;
        this.zCoord = d2;
    }

    // JAVADOC METHOD $$ func_72446_c
    @SideOnly(Side.CLIENT)
    public void rotateAroundZ(float par1)
    {
        float f1 = MathHelper.cos(par1);
        float f2 = MathHelper.sin(par1);
        double d0 = this.xCoord * (double)f1 + this.yCoord * (double)f2;
        double d1 = this.yCoord * (double)f1 - this.xCoord * (double)f2;
        double d2 = this.zCoord;
        this.xCoord = d0;
        this.yCoord = d1;
        this.zCoord = d2;
    }
}