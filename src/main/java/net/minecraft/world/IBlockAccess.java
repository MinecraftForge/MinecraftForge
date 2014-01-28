package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public interface IBlockAccess
{
    Block func_147439_a(int var1, int var2, int var3);

    TileEntity func_147438_o(int var1, int var2, int var3);

    // JAVADOC METHOD $$ func_72802_i
    @SideOnly(Side.CLIENT)
    int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4);

    // JAVADOC METHOD $$ func_72805_g
    int getBlockMetadata(int var1, int var2, int var3);

    boolean func_147437_c(int var1, int var2, int var3);

    // JAVADOC METHOD $$ func_72807_a
    @SideOnly(Side.CLIENT)
    BiomeGenBase getBiomeGenForCoords(int var1, int var2);

    // JAVADOC METHOD $$ func_72800_K
    @SideOnly(Side.CLIENT)
    int getHeight();

    // JAVADOC METHOD $$ func_72806_N
    @SideOnly(Side.CLIENT)
    boolean extendedLevelsInChunkCache();

    // JAVADOC METHOD $$ func_82732_R
    Vec3Pool getWorldVec3Pool();

    // JAVADOC METHOD $$ func_72879_k
    int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4);

    /**
     * FORGE: isSideSolid, pulled up from {@link World}
     *
     * @param x X coord
     * @param y Y coord
     * @param z Z coord
     * @param side Side
     * @param _default default return value
     * @return if the block is solid on the side
     */
    boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default);
}