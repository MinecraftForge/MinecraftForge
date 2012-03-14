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
     * 'Any Light rendered on a 1.8 Block goes through here'
     */
    int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4);

    float getBrightness(int var1, int var2, int var3, int var4);

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     */
    float getLightBrightness(int var1, int var2, int var3);

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
    boolean isBlockOpaqueCube(int var1, int var2, int var3);

    /**
     * Indicate if a material is a normal solid opaque cube.
     */
    boolean isBlockNormalCube(int var1, int var2, int var3);

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    boolean isAirBlock(int var1, int var2, int var3);

    BiomeGenBase func_48454_a(int var1, int var2);

    /**
     * Returns current world height
     */
    int getWorldHeight();

    boolean func_48452_a();
}
