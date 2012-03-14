package net.minecraft.src;

public class ChunkCoordinates implements Comparable
{
    public int posX;

    /** the y coordinate */
    public int posY;

    /** the z coordinate */
    public int posZ;

    public ChunkCoordinates() {}

    public ChunkCoordinates(int par1, int par2, int par3)
    {
        this.posX = par1;
        this.posY = par2;
        this.posZ = par3;
    }

    public ChunkCoordinates(ChunkCoordinates par1ChunkCoordinates)
    {
        this.posX = par1ChunkCoordinates.posX;
        this.posY = par1ChunkCoordinates.posY;
        this.posZ = par1ChunkCoordinates.posZ;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof ChunkCoordinates))
        {
            return false;
        }
        else
        {
            ChunkCoordinates var2 = (ChunkCoordinates)par1Obj;
            return this.posX == var2.posX && this.posY == var2.posY && this.posZ == var2.posZ;
        }
    }

    public int hashCode()
    {
        return this.posX + this.posZ << 8 + this.posY << 16;
    }

    /**
     * Compare the coordinate with another coordinate
     */
    public int compareChunkCoordinate(ChunkCoordinates par1ChunkCoordinates)
    {
        return this.posY == par1ChunkCoordinates.posY ? (this.posZ == par1ChunkCoordinates.posZ ? this.posX - par1ChunkCoordinates.posX : this.posZ - par1ChunkCoordinates.posZ) : this.posY - par1ChunkCoordinates.posY;
    }

    public void setChunkCoordinates(int par1, int par2, int par3)
    {
        this.posX = par1;
        this.posY = par2;
        this.posZ = par3;
    }

    /**
     * Returns the euclidean distance of the chunk coordinate to the x, y, z parameters passed.
     */
    public double getEuclideanDistanceTo(int par1, int par2, int par3)
    {
        int var4 = this.posX - par1;
        int var5 = this.posY - par2;
        int var6 = this.posZ - par3;
        return Math.sqrt((double)(var4 * var4 + var5 * var5 + var6 * var6));
    }

    /**
     * Returns the squared distance between this coordinates and the coordinates given as argument.
     */
    public float getDistanceSquared(int par1, int par2, int par3)
    {
        int var4 = this.posX - par1;
        int var5 = this.posY - par2;
        int var6 = this.posZ - par3;
        return (float)(var4 * var4 + var5 * var5 + var6 * var6);
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareChunkCoordinate((ChunkCoordinates)par1Obj);
    }
}
