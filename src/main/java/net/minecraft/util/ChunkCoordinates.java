package net.minecraft.util;

public class ChunkCoordinates implements Comparable
{
    public int posX;
    // JAVADOC FIELD $$ field_71572_b
    public int posY;
    // JAVADOC FIELD $$ field_71573_c
    public int posZ;
    private static final String __OBFID = "CL_00001555";

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
            ChunkCoordinates chunkcoordinates = (ChunkCoordinates)par1Obj;
            return this.posX == chunkcoordinates.posX && this.posY == chunkcoordinates.posY && this.posZ == chunkcoordinates.posZ;
        }
    }

    public int hashCode()
    {
        return this.posX + this.posZ << 8 + this.posY << 16;
    }

    public int compareTo(ChunkCoordinates par1ChunkCoordinates)
    {
        return this.posY == par1ChunkCoordinates.posY ? (this.posZ == par1ChunkCoordinates.posZ ? this.posX - par1ChunkCoordinates.posX : this.posZ - par1ChunkCoordinates.posZ) : this.posY - par1ChunkCoordinates.posY;
    }

    public void set(int par1, int par2, int par3)
    {
        this.posX = par1;
        this.posY = par2;
        this.posZ = par3;
    }

    // JAVADOC METHOD $$ func_71569_e
    public float getDistanceSquared(int par1, int par2, int par3)
    {
        float f = (float)(this.posX - par1);
        float f1 = (float)(this.posY - par2);
        float f2 = (float)(this.posZ - par3);
        return f * f + f1 * f1 + f2 * f2;
    }

    // JAVADOC METHOD $$ func_82371_e
    public float getDistanceSquaredToChunkCoordinates(ChunkCoordinates par1ChunkCoordinates)
    {
        return this.getDistanceSquared(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ);
    }

    public String toString()
    {
        return "Pos{x=" + this.posX + ", y=" + this.posY + ", z=" + this.posZ + '}';
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ChunkCoordinates)par1Obj);
    }
}