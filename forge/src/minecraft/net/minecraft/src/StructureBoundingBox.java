package net.minecraft.src;

public class StructureBoundingBox
{
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public StructureBoundingBox() {}

    /**
     * 'returns a new StructureBoundingBox with MAX values'
     */
    public static StructureBoundingBox getNewBoundingBox()
    {
        return new StructureBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /**
     * 'used to project a possible new component Bounding Box - to check if it would cut anything already spawned'
     */
    public static StructureBoundingBox getComponentToAddBoundingBox(int par0, int par1, int par2, int par3, int par4, int par5, int par6, int par7, int par8, int par9)
    {
        switch (par9)
        {
            case 0:
                return new StructureBoundingBox(par0 + par3, par1 + par4, par2 + par5, par0 + par6 - 1 + par3, par1 + par7 - 1 + par4, par2 + par8 - 1 + par5);

            case 1:
                return new StructureBoundingBox(par0 - par8 + 1 + par5, par1 + par4, par2 + par3, par0 + par5, par1 + par7 - 1 + par4, par2 + par6 - 1 + par3);

            case 2:
                return new StructureBoundingBox(par0 + par3, par1 + par4, par2 - par8 + 1 + par5, par0 + par6 - 1 + par3, par1 + par7 - 1 + par4, par2 + par5);

            case 3:
                return new StructureBoundingBox(par0 + par5, par1 + par4, par2 + par3, par0 + par8 - 1 + par5, par1 + par7 - 1 + par4, par2 + par6 - 1 + par3);

            default:
                return new StructureBoundingBox(par0 + par3, par1 + par4, par2 + par5, par0 + par6 - 1 + par3, par1 + par7 - 1 + par4, par2 + par8 - 1 + par5);
        }
    }

    public StructureBoundingBox(StructureBoundingBox par1StructureBoundingBox)
    {
        this.minX = par1StructureBoundingBox.minX;
        this.minY = par1StructureBoundingBox.minY;
        this.minZ = par1StructureBoundingBox.minZ;
        this.maxX = par1StructureBoundingBox.maxX;
        this.maxY = par1StructureBoundingBox.maxY;
        this.maxZ = par1StructureBoundingBox.maxZ;
    }

    public StructureBoundingBox(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        this.minX = par1;
        this.minY = par2;
        this.minZ = par3;
        this.maxX = par4;
        this.maxY = par5;
        this.maxZ = par6;
    }

    public StructureBoundingBox(int par1, int par2, int par3, int par4)
    {
        this.minX = par1;
        this.minZ = par2;
        this.maxX = par3;
        this.maxZ = par4;
        this.minY = 1;
        this.maxY = 512;
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: structureboundingbox
     */
    public boolean intersectsWith(StructureBoundingBox par1StructureBoundingBox)
    {
        return this.maxX >= par1StructureBoundingBox.minX && this.minX <= par1StructureBoundingBox.maxX && this.maxZ >= par1StructureBoundingBox.minZ && this.minZ <= par1StructureBoundingBox.maxZ && this.maxY >= par1StructureBoundingBox.minY && this.minY <= par1StructureBoundingBox.maxY;
    }

    public boolean intersectsWith(int par1, int par2, int par3, int par4)
    {
        return this.maxX >= par1 && this.minX <= par3 && this.maxZ >= par2 && this.minZ <= par4;
    }

    public void expandTo(StructureBoundingBox par1StructureBoundingBox)
    {
        this.minX = Math.min(this.minX, par1StructureBoundingBox.minX);
        this.minY = Math.min(this.minY, par1StructureBoundingBox.minY);
        this.minZ = Math.min(this.minZ, par1StructureBoundingBox.minZ);
        this.maxX = Math.max(this.maxX, par1StructureBoundingBox.maxX);
        this.maxY = Math.max(this.maxY, par1StructureBoundingBox.maxY);
        this.maxZ = Math.max(this.maxZ, par1StructureBoundingBox.maxZ);
    }

    /**
     * Offsets the current bounding box by the specified coordinates. Args: x, y, z
     */
    public void offset(int par1, int par2, int par3)
    {
        this.minX += par1;
        this.minY += par2;
        this.minZ += par3;
        this.maxX += par1;
        this.maxY += par2;
        this.maxZ += par3;
    }

    /**
     * Returns true if block is inside bounding box
     */
    public boolean isVecInside(int par1, int par2, int par3)
    {
        return par1 >= this.minX && par1 <= this.maxX && par3 >= this.minZ && par3 <= this.maxZ && par2 >= this.minY && par2 <= this.maxY;
    }

    /**
     * Returns width of a bounding box
     */
    public int getXSize()
    {
        return this.maxX - this.minX + 1;
    }

    /**
     * Returns height of a bounding box
     */
    public int getYSize()
    {
        return this.maxY - this.minY + 1;
    }

    /**
     * Returns length of a bounding box
     */
    public int getZSize()
    {
        return this.maxZ - this.minZ + 1;
    }

    public int getCenterX()
    {
        return this.minX + (this.maxX - this.minX + 1) / 2;
    }

    public int getCenterY()
    {
        return this.minY + (this.maxY - this.minY + 1) / 2;
    }

    public int getCenterZ()
    {
        return this.minZ + (this.maxZ - this.minZ + 1) / 2;
    }

    public String toString()
    {
        return "(" + this.minX + ", " + this.minY + ", " + this.minZ + "; " + this.maxX + ", " + this.maxY + ", " + this.maxZ + ")";
    }
}
