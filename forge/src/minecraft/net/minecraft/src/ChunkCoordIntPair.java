package net.minecraft.src;

public class ChunkCoordIntPair
{
    /** The X position of this Chunk Coordinate Pair */
    public final int chunkXPos;

    /** The Z position of this Chunk Coordinate Pair */
    public final int chunkZPos;

    public ChunkCoordIntPair(int par1, int par2)
    {
        this.chunkXPos = par1;
        this.chunkZPos = par2;
    }

    /**
     * converts a chunk coordinate pair to an integer (suitable for hashing)
     */
    public static long chunkXZ2Int(int par0, int par1)
    {
        long var2 = (long)par0;
        long var4 = (long)par1;
        return var2 & 4294967295L | (var4 & 4294967295L) << 32;
    }

    public int hashCode()
    {
        long var1 = chunkXZ2Int(this.chunkXPos, this.chunkZPos);
        int var3 = (int)var1;
        int var4 = (int)(var1 >> 32);
        return var3 ^ var4;
    }

    public boolean equals(Object par1Obj)
    {
        ChunkCoordIntPair var2 = (ChunkCoordIntPair)par1Obj;
        return var2.chunkXPos == this.chunkXPos && var2.chunkZPos == this.chunkZPos;
    }

    public int getCenterXPos()
    {
        return (this.chunkXPos << 4) + 8;
    }

    public int getCenterZPos()
    {
        return (this.chunkZPos << 4) + 8;
    }

    public ChunkPosition getChunkPosition(int par1)
    {
        return new ChunkPosition(this.getCenterXPos(), par1, this.getCenterZPos());
    }

    public String toString()
    {
        return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
    }
}
