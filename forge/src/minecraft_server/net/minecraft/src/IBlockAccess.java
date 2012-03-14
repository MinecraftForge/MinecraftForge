package net.minecraft.src;

public interface IBlockAccess
{
    /**
     * Returns the block ID at coords x,y,z
     */
    int getBlockId(int var1, int var2, int var3);

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    TileEntity getBlockTileEntity(int var1, int var2, int var3);

    /**
     * Returns the block metadata at coords x,y,z
     */
    int getBlockMetadata(int var1, int var2, int var3);

    /**
     * Returns the block's material.
     */
    Material getBlockMaterial(int var1, int var2, int var3);

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    boolean isBlockNormalCube(int var1, int var2, int var3);
}
