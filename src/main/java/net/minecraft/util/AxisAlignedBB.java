package net.minecraft.util;

public class AxisAlignedBB
{
    // JAVADOC FIELD $$ field_72335_g
    private static final ThreadLocal theAABBLocalPool = new ThreadLocal()
    {
        private static final String __OBFID = "CL_00000608";
        protected AABBPool initialValue()
        {
            return new AABBPool(300, 2000);
        }
    };
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;
    private static final String __OBFID = "CL_00000607";

    // JAVADOC METHOD $$ func_72330_a
    public static AxisAlignedBB getBoundingBox(double par0, double par2, double par4, double par6, double par8, double par10)
    {
        return new AxisAlignedBB(par0, par2, par4, par6, par8, par10);
    }

    // JAVADOC METHOD $$ func_72332_a
    public static AABBPool getAABBPool()
    {
        return (AABBPool)theAABBLocalPool.get();
    }

    protected AxisAlignedBB(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        this.minX = par1;
        this.minY = par3;
        this.minZ = par5;
        this.maxX = par7;
        this.maxY = par9;
        this.maxZ = par11;
    }

    // JAVADOC METHOD $$ func_72324_b
    public AxisAlignedBB setBounds(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        this.minX = par1;
        this.minY = par3;
        this.minZ = par5;
        this.maxX = par7;
        this.maxY = par9;
        this.maxZ = par11;
        return this;
    }

    // JAVADOC METHOD $$ func_72321_a
    public AxisAlignedBB addCoord(double par1, double par3, double par5)
    {
        double d3 = this.minX;
        double d4 = this.minY;
        double d5 = this.minZ;
        double d6 = this.maxX;
        double d7 = this.maxY;
        double d8 = this.maxZ;

        if (par1 < 0.0D)
        {
            d3 += par1;
        }

        if (par1 > 0.0D)
        {
            d6 += par1;
        }

        if (par3 < 0.0D)
        {
            d4 += par3;
        }

        if (par3 > 0.0D)
        {
            d7 += par3;
        }

        if (par5 < 0.0D)
        {
            d5 += par5;
        }

        if (par5 > 0.0D)
        {
            d8 += par5;
        }

        // JAVADOC METHOD $$ func_72332_a
        return getAABBPool().getAABB(d3, d4, d5, d6, d7, d8);
    }

    // JAVADOC METHOD $$ func_72314_b
    public AxisAlignedBB expand(double par1, double par3, double par5)
    {
        double d3 = this.minX - par1;
        double d4 = this.minY - par3;
        double d5 = this.minZ - par5;
        double d6 = this.maxX + par1;
        double d7 = this.maxY + par3;
        double d8 = this.maxZ + par5;
        // JAVADOC METHOD $$ func_72332_a
        return getAABBPool().getAABB(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB func_111270_a(AxisAlignedBB par1AxisAlignedBB)
    {
        double d0 = Math.min(this.minX, par1AxisAlignedBB.minX);
        double d1 = Math.min(this.minY, par1AxisAlignedBB.minY);
        double d2 = Math.min(this.minZ, par1AxisAlignedBB.minZ);
        double d3 = Math.max(this.maxX, par1AxisAlignedBB.maxX);
        double d4 = Math.max(this.maxY, par1AxisAlignedBB.maxY);
        double d5 = Math.max(this.maxZ, par1AxisAlignedBB.maxZ);
        // JAVADOC METHOD $$ func_72332_a
        return getAABBPool().getAABB(d0, d1, d2, d3, d4, d5);
    }

    // JAVADOC METHOD $$ func_72325_c
    public AxisAlignedBB getOffsetBoundingBox(double par1, double par3, double par5)
    {
        // JAVADOC METHOD $$ func_72332_a
        return getAABBPool().getAABB(this.minX + par1, this.minY + par3, this.minZ + par5, this.maxX + par1, this.maxY + par3, this.maxZ + par5);
    }

    // JAVADOC METHOD $$ func_72316_a
    public double calculateXOffset(AxisAlignedBB par1AxisAlignedBB, double par2)
    {
        if (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY)
        {
            if (par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ)
            {
                double d1;

                if (par2 > 0.0D && par1AxisAlignedBB.maxX <= this.minX)
                {
                    d1 = this.minX - par1AxisAlignedBB.maxX;

                    if (d1 < par2)
                    {
                        par2 = d1;
                    }
                }

                if (par2 < 0.0D && par1AxisAlignedBB.minX >= this.maxX)
                {
                    d1 = this.maxX - par1AxisAlignedBB.minX;

                    if (d1 > par2)
                    {
                        par2 = d1;
                    }
                }

                return par2;
            }
            else
            {
                return par2;
            }
        }
        else
        {
            return par2;
        }
    }

    // JAVADOC METHOD $$ func_72323_b
    public double calculateYOffset(AxisAlignedBB par1AxisAlignedBB, double par2)
    {
        if (par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX)
        {
            if (par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ)
            {
                double d1;

                if (par2 > 0.0D && par1AxisAlignedBB.maxY <= this.minY)
                {
                    d1 = this.minY - par1AxisAlignedBB.maxY;

                    if (d1 < par2)
                    {
                        par2 = d1;
                    }
                }

                if (par2 < 0.0D && par1AxisAlignedBB.minY >= this.maxY)
                {
                    d1 = this.maxY - par1AxisAlignedBB.minY;

                    if (d1 > par2)
                    {
                        par2 = d1;
                    }
                }

                return par2;
            }
            else
            {
                return par2;
            }
        }
        else
        {
            return par2;
        }
    }

    // JAVADOC METHOD $$ func_72322_c
    public double calculateZOffset(AxisAlignedBB par1AxisAlignedBB, double par2)
    {
        if (par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX)
        {
            if (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY)
            {
                double d1;

                if (par2 > 0.0D && par1AxisAlignedBB.maxZ <= this.minZ)
                {
                    d1 = this.minZ - par1AxisAlignedBB.maxZ;

                    if (d1 < par2)
                    {
                        par2 = d1;
                    }
                }

                if (par2 < 0.0D && par1AxisAlignedBB.minZ >= this.maxZ)
                {
                    d1 = this.maxZ - par1AxisAlignedBB.minZ;

                    if (d1 > par2)
                    {
                        par2 = d1;
                    }
                }

                return par2;
            }
            else
            {
                return par2;
            }
        }
        else
        {
            return par2;
        }
    }

    // JAVADOC METHOD $$ func_72326_a
    public boolean intersectsWith(AxisAlignedBB par1AxisAlignedBB)
    {
        return par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX ? (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY ? par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ : false) : false;
    }

    // JAVADOC METHOD $$ func_72317_d
    public AxisAlignedBB offset(double par1, double par3, double par5)
    {
        this.minX += par1;
        this.minY += par3;
        this.minZ += par5;
        this.maxX += par1;
        this.maxY += par3;
        this.maxZ += par5;
        return this;
    }

    // JAVADOC METHOD $$ func_72318_a
    public boolean isVecInside(Vec3 par1Vec3)
    {
        return par1Vec3.xCoord > this.minX && par1Vec3.xCoord < this.maxX ? (par1Vec3.yCoord > this.minY && par1Vec3.yCoord < this.maxY ? par1Vec3.zCoord > this.minZ && par1Vec3.zCoord < this.maxZ : false) : false;
    }

    // JAVADOC METHOD $$ func_72320_b
    public double getAverageEdgeLength()
    {
        double d0 = this.maxX - this.minX;
        double d1 = this.maxY - this.minY;
        double d2 = this.maxZ - this.minZ;
        return (d0 + d1 + d2) / 3.0D;
    }

    // JAVADOC METHOD $$ func_72331_e
    public AxisAlignedBB contract(double par1, double par3, double par5)
    {
        double d3 = this.minX + par1;
        double d4 = this.minY + par3;
        double d5 = this.minZ + par5;
        double d6 = this.maxX - par1;
        double d7 = this.maxY - par3;
        double d8 = this.maxZ - par5;
        // JAVADOC METHOD $$ func_72332_a
        return getAABBPool().getAABB(d3, d4, d5, d6, d7, d8);
    }

    // JAVADOC METHOD $$ func_72329_c
    public AxisAlignedBB copy()
    {
        // JAVADOC METHOD $$ func_72332_a
        return getAABBPool().getAABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public MovingObjectPosition calculateIntercept(Vec3 par1Vec3, Vec3 par2Vec3)
    {
        Vec3 vec32 = par1Vec3.getIntermediateWithXValue(par2Vec3, this.minX);
        Vec3 vec33 = par1Vec3.getIntermediateWithXValue(par2Vec3, this.maxX);
        Vec3 vec34 = par1Vec3.getIntermediateWithYValue(par2Vec3, this.minY);
        Vec3 vec35 = par1Vec3.getIntermediateWithYValue(par2Vec3, this.maxY);
        Vec3 vec36 = par1Vec3.getIntermediateWithZValue(par2Vec3, this.minZ);
        Vec3 vec37 = par1Vec3.getIntermediateWithZValue(par2Vec3, this.maxZ);

        if (!this.isVecInYZ(vec32))
        {
            vec32 = null;
        }

        if (!this.isVecInYZ(vec33))
        {
            vec33 = null;
        }

        if (!this.isVecInXZ(vec34))
        {
            vec34 = null;
        }

        if (!this.isVecInXZ(vec35))
        {
            vec35 = null;
        }

        if (!this.isVecInXY(vec36))
        {
            vec36 = null;
        }

        if (!this.isVecInXY(vec37))
        {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec32) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec33) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec34) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec35) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec36) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || par1Vec3.squareDistanceTo(vec37) < par1Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec37;
        }

        if (vec38 == null)
        {
            return null;
        }
        else
        {
            byte b0 = -1;

            if (vec38 == vec32)
            {
                b0 = 4;
            }

            if (vec38 == vec33)
            {
                b0 = 5;
            }

            if (vec38 == vec34)
            {
                b0 = 0;
            }

            if (vec38 == vec35)
            {
                b0 = 1;
            }

            if (vec38 == vec36)
            {
                b0 = 2;
            }

            if (vec38 == vec37)
            {
                b0 = 3;
            }

            return new MovingObjectPosition(0, 0, 0, b0, vec38);
        }
    }

    // JAVADOC METHOD $$ func_72333_b
    private boolean isVecInYZ(Vec3 par1Vec3)
    {
        return par1Vec3 == null ? false : par1Vec3.yCoord >= this.minY && par1Vec3.yCoord <= this.maxY && par1Vec3.zCoord >= this.minZ && par1Vec3.zCoord <= this.maxZ;
    }

    // JAVADOC METHOD $$ func_72315_c
    private boolean isVecInXZ(Vec3 par1Vec3)
    {
        return par1Vec3 == null ? false : par1Vec3.xCoord >= this.minX && par1Vec3.xCoord <= this.maxX && par1Vec3.zCoord >= this.minZ && par1Vec3.zCoord <= this.maxZ;
    }

    // JAVADOC METHOD $$ func_72319_d
    private boolean isVecInXY(Vec3 par1Vec3)
    {
        return par1Vec3 == null ? false : par1Vec3.xCoord >= this.minX && par1Vec3.xCoord <= this.maxX && par1Vec3.yCoord >= this.minY && par1Vec3.yCoord <= this.maxY;
    }

    // JAVADOC METHOD $$ func_72328_c
    public void setBB(AxisAlignedBB par1AxisAlignedBB)
    {
        this.minX = par1AxisAlignedBB.minX;
        this.minY = par1AxisAlignedBB.minY;
        this.minZ = par1AxisAlignedBB.minZ;
        this.maxX = par1AxisAlignedBB.maxX;
        this.maxY = par1AxisAlignedBB.maxY;
        this.maxZ = par1AxisAlignedBB.maxZ;
    }

    public String toString()
    {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
}