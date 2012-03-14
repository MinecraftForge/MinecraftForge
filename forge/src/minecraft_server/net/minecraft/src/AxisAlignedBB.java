package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class AxisAlignedBB
{
    /** List of bounding boxes (not all necessarily being actively used) */
    private static List boundingBoxes = new ArrayList();

    /** Tracks how many bounding boxes are being used */
    private static int numBoundingBoxesInUse = 0;
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    /**
     * Returns a bounding box with the specified bounds. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public static AxisAlignedBB getBoundingBox(double par0, double par2, double par4, double par6, double par8, double par10)
    {
        return new AxisAlignedBB(par0, par2, par4, par6, par8, par10);
    }

    /**
     * Sets the number of bounding boxes in use from the pool to 0 so they will be reused
     */
    public static void clearBoundingBoxPool()
    {
        numBoundingBoxesInUse = 0;
    }

    /**
     * Returns a bounding box with the specified bounds from the pool.  Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public static AxisAlignedBB getBoundingBoxFromPool(double par0, double par2, double par4, double par6, double par8, double par10)
    {
        if (numBoundingBoxesInUse >= boundingBoxes.size())
        {
            boundingBoxes.add(getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D));
        }

        return ((AxisAlignedBB)boundingBoxes.get(numBoundingBoxesInUse++)).setBounds(par0, par2, par4, par6, par8, par10);
    }

    private AxisAlignedBB(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        this.minX = par1;
        this.minY = par3;
        this.minZ = par5;
        this.maxX = par7;
        this.maxY = par9;
        this.maxZ = par11;
    }

    /**
     * Sets the bounds of the bounding box. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
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

    /**
     * Adds the coordinates to the bounding box extending it if the point lies outside the current ranges. Args: x, y, z
     */
    public AxisAlignedBB addCoord(double par1, double par3, double par5)
    {
        double var7 = this.minX;
        double var9 = this.minY;
        double var11 = this.minZ;
        double var13 = this.maxX;
        double var15 = this.maxY;
        double var17 = this.maxZ;

        if (par1 < 0.0D)
        {
            var7 += par1;
        }

        if (par1 > 0.0D)
        {
            var13 += par1;
        }

        if (par3 < 0.0D)
        {
            var9 += par3;
        }

        if (par3 > 0.0D)
        {
            var15 += par3;
        }

        if (par5 < 0.0D)
        {
            var11 += par5;
        }

        if (par5 > 0.0D)
        {
            var17 += par5;
        }

        return getBoundingBoxFromPool(var7, var9, var11, var13, var15, var17);
    }

    /**
     * Returns a bounding box expanded by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public AxisAlignedBB expand(double par1, double par3, double par5)
    {
        double var7 = this.minX - par1;
        double var9 = this.minY - par3;
        double var11 = this.minZ - par5;
        double var13 = this.maxX + par1;
        double var15 = this.maxY + par3;
        double var17 = this.maxZ + par5;
        return getBoundingBoxFromPool(var7, var9, var11, var13, var15, var17);
    }

    /**
     * Returns a bounding box offseted by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public AxisAlignedBB getOffsetBoundingBox(double par1, double par3, double par5)
    {
        return getBoundingBoxFromPool(this.minX + par1, this.minY + par3, this.minZ + par5, this.maxX + par1, this.maxY + par3, this.maxZ + par5);
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(AxisAlignedBB par1AxisAlignedBB, double par2)
    {
        if (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY)
        {
            if (par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ)
            {
                double var4;

                if (par2 > 0.0D && par1AxisAlignedBB.maxX <= this.minX)
                {
                    var4 = this.minX - par1AxisAlignedBB.maxX;

                    if (var4 < par2)
                    {
                        par2 = var4;
                    }
                }

                if (par2 < 0.0D && par1AxisAlignedBB.minX >= this.maxX)
                {
                    var4 = this.maxX - par1AxisAlignedBB.minX;

                    if (var4 > par2)
                    {
                        par2 = var4;
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

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(AxisAlignedBB par1AxisAlignedBB, double par2)
    {
        if (par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX)
        {
            if (par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ)
            {
                double var4;

                if (par2 > 0.0D && par1AxisAlignedBB.maxY <= this.minY)
                {
                    var4 = this.minY - par1AxisAlignedBB.maxY;

                    if (var4 < par2)
                    {
                        par2 = var4;
                    }
                }

                if (par2 < 0.0D && par1AxisAlignedBB.minY >= this.maxY)
                {
                    var4 = this.maxY - par1AxisAlignedBB.minY;

                    if (var4 > par2)
                    {
                        par2 = var4;
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

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(AxisAlignedBB par1AxisAlignedBB, double par2)
    {
        if (par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX)
        {
            if (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY)
            {
                double var4;

                if (par2 > 0.0D && par1AxisAlignedBB.maxZ <= this.minZ)
                {
                    var4 = this.minZ - par1AxisAlignedBB.maxZ;

                    if (var4 < par2)
                    {
                        par2 = var4;
                    }
                }

                if (par2 < 0.0D && par1AxisAlignedBB.minZ >= this.maxZ)
                {
                    var4 = this.maxZ - par1AxisAlignedBB.minZ;

                    if (var4 > par2)
                    {
                        par2 = var4;
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

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    public boolean intersectsWith(AxisAlignedBB par1AxisAlignedBB)
    {
        return par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX ? (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY ? par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ : false) : false;
    }

    /**
     * Offsets the current bounding box by the specified coordinates. Args: x, y, z
     */
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

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    public boolean isVecInside(Vec3D par1Vec3D)
    {
        return par1Vec3D.xCoord > this.minX && par1Vec3D.xCoord < this.maxX ? (par1Vec3D.yCoord > this.minY && par1Vec3D.yCoord < this.maxY ? par1Vec3D.zCoord > this.minZ && par1Vec3D.zCoord < this.maxZ : false) : false;
    }

    /**
     * Returns a bounding box that is inset by the specified amounts
     */
    public AxisAlignedBB contract(double par1, double par3, double par5)
    {
        double var7 = this.minX + par1;
        double var9 = this.minY + par3;
        double var11 = this.minZ + par5;
        double var13 = this.maxX - par1;
        double var15 = this.maxY - par3;
        double var17 = this.maxZ - par5;
        return getBoundingBoxFromPool(var7, var9, var11, var13, var15, var17);
    }

    /**
     * Returns a copy of the bounding box.
     */
    public AxisAlignedBB copy()
    {
        return getBoundingBoxFromPool(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public MovingObjectPosition calculateIntercept(Vec3D par1Vec3D, Vec3D par2Vec3D)
    {
        Vec3D var3 = par1Vec3D.getIntermediateWithXValue(par2Vec3D, this.minX);
        Vec3D var4 = par1Vec3D.getIntermediateWithXValue(par2Vec3D, this.maxX);
        Vec3D var5 = par1Vec3D.getIntermediateWithYValue(par2Vec3D, this.minY);
        Vec3D var6 = par1Vec3D.getIntermediateWithYValue(par2Vec3D, this.maxY);
        Vec3D var7 = par1Vec3D.getIntermediateWithZValue(par2Vec3D, this.minZ);
        Vec3D var8 = par1Vec3D.getIntermediateWithZValue(par2Vec3D, this.maxZ);

        if (!this.isVecInYZ(var3))
        {
            var3 = null;
        }

        if (!this.isVecInYZ(var4))
        {
            var4 = null;
        }

        if (!this.isVecInXZ(var5))
        {
            var5 = null;
        }

        if (!this.isVecInXZ(var6))
        {
            var6 = null;
        }

        if (!this.isVecInXY(var7))
        {
            var7 = null;
        }

        if (!this.isVecInXY(var8))
        {
            var8 = null;
        }

        Vec3D var9 = null;

        if (var3 != null && (var9 == null || par1Vec3D.squareDistanceTo(var3) < par1Vec3D.squareDistanceTo(var9)))
        {
            var9 = var3;
        }

        if (var4 != null && (var9 == null || par1Vec3D.squareDistanceTo(var4) < par1Vec3D.squareDistanceTo(var9)))
        {
            var9 = var4;
        }

        if (var5 != null && (var9 == null || par1Vec3D.squareDistanceTo(var5) < par1Vec3D.squareDistanceTo(var9)))
        {
            var9 = var5;
        }

        if (var6 != null && (var9 == null || par1Vec3D.squareDistanceTo(var6) < par1Vec3D.squareDistanceTo(var9)))
        {
            var9 = var6;
        }

        if (var7 != null && (var9 == null || par1Vec3D.squareDistanceTo(var7) < par1Vec3D.squareDistanceTo(var9)))
        {
            var9 = var7;
        }

        if (var8 != null && (var9 == null || par1Vec3D.squareDistanceTo(var8) < par1Vec3D.squareDistanceTo(var9)))
        {
            var9 = var8;
        }

        if (var9 == null)
        {
            return null;
        }
        else
        {
            byte var10 = -1;

            if (var9 == var3)
            {
                var10 = 4;
            }

            if (var9 == var4)
            {
                var10 = 5;
            }

            if (var9 == var5)
            {
                var10 = 0;
            }

            if (var9 == var6)
            {
                var10 = 1;
            }

            if (var9 == var7)
            {
                var10 = 2;
            }

            if (var9 == var8)
            {
                var10 = 3;
            }

            return new MovingObjectPosition(0, 0, 0, var10, var9);
        }
    }

    /**
     * Checks if the specified vector is within the YZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInYZ(Vec3D par1Vec3D)
    {
        return par1Vec3D == null ? false : par1Vec3D.yCoord >= this.minY && par1Vec3D.yCoord <= this.maxY && par1Vec3D.zCoord >= this.minZ && par1Vec3D.zCoord <= this.maxZ;
    }

    /**
     * Checks if the specified vector is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXZ(Vec3D par1Vec3D)
    {
        return par1Vec3D == null ? false : par1Vec3D.xCoord >= this.minX && par1Vec3D.xCoord <= this.maxX && par1Vec3D.zCoord >= this.minZ && par1Vec3D.zCoord <= this.maxZ;
    }

    /**
     * Checks if the specified vector is within the XY dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXY(Vec3D par1Vec3D)
    {
        return par1Vec3D == null ? false : par1Vec3D.xCoord >= this.minX && par1Vec3D.xCoord <= this.maxX && par1Vec3D.yCoord >= this.minY && par1Vec3D.yCoord <= this.maxY;
    }

    /**
     * Sets the bounding box to the same bounds as the bounding box passed in. Args: axisAlignedBB
     */
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
