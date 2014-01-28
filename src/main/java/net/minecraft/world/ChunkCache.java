package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

public class ChunkCache implements IBlockAccess
{
    private int chunkX;
    private int chunkZ;
    private Chunk[][] chunkArray;
    // JAVADOC FIELD $$ field_72814_d
    private boolean isEmpty;
    // JAVADOC FIELD $$ field_72815_e
    private World worldObj;
    private static final String __OBFID = "CL_00000155";

    public ChunkCache(World par1World, int par2, int par3, int par4, int par5, int par6, int par7, int par8)
    {
        this.worldObj = par1World;
        this.chunkX = par2 - par8 >> 4;
        this.chunkZ = par4 - par8 >> 4;
        int l1 = par5 + par8 >> 4;
        int i2 = par7 + par8 >> 4;
        this.chunkArray = new Chunk[l1 - this.chunkX + 1][i2 - this.chunkZ + 1];
        this.isEmpty = true;
        int j2;
        int k2;
        Chunk chunk;

        for (j2 = this.chunkX; j2 <= l1; ++j2)
        {
            for (k2 = this.chunkZ; k2 <= i2; ++k2)
            {
                chunk = par1World.getChunkFromChunkCoords(j2, k2);

                if (chunk != null)
                {
                    this.chunkArray[j2 - this.chunkX][k2 - this.chunkZ] = chunk;
                }
            }
        }

        for (j2 = par2 >> 4; j2 <= par5 >> 4; ++j2)
        {
            for (k2 = par4 >> 4; k2 <= par7 >> 4; ++k2)
            {
                chunk = this.chunkArray[j2 - this.chunkX][k2 - this.chunkZ];

                if (chunk != null && !chunk.getAreLevelsEmpty(par3, par6))
                {
                    this.isEmpty = false;
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_72806_N
    @SideOnly(Side.CLIENT)
    public boolean extendedLevelsInChunkCache()
    {
        return this.isEmpty;
    }

    public Block func_147439_a(int p_147439_1_, int p_147439_2_, int p_147439_3_)
    {
        Block block = Blocks.air;

        if (p_147439_2_ >= 0 && p_147439_2_ < 256)
        {
            int l = (p_147439_1_ >> 4) - this.chunkX;
            int i1 = (p_147439_3_ >> 4) - this.chunkZ;

            if (l >= 0 && l < this.chunkArray.length && i1 >= 0 && i1 < this.chunkArray[l].length)
            {
                Chunk chunk = this.chunkArray[l][i1];

                if (chunk != null)
                {
                    block = chunk.func_150810_a(p_147439_1_ & 15, p_147439_2_, p_147439_3_ & 15);
                }
            }
        }

        return block;
    }

    public TileEntity func_147438_o(int p_147438_1_, int p_147438_2_, int p_147438_3_)
    {
        int l = (p_147438_1_ >> 4) - this.chunkX;
        int i1 = (p_147438_3_ >> 4) - this.chunkZ;
        if (l < 0 || l >= chunkArray.length || i1 < 0 || i1 >= chunkArray[l].length) return null;
        if (chunkArray[l][i1] == null) return null;
        return this.chunkArray[l][i1].func_150806_e(p_147438_1_ & 15, p_147438_2_, p_147438_3_ & 15);
    }

    // JAVADOC METHOD $$ func_72802_i
    @SideOnly(Side.CLIENT)
    public int getLightBrightnessForSkyBlocks(int par1, int par2, int par3, int par4)
    {
        int i1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
        int j1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);

        if (j1 < par4)
        {
            j1 = par4;
        }

        return i1 << 20 | j1 << 4;
    }

    // JAVADOC METHOD $$ func_72805_g
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        if (par2 < 0)
        {
            return 0;
        }
        else if (par2 >= 256)
        {
            return 0;
        }
        else
        {
            int l = (par1 >> 4) - this.chunkX;
            int i1 = (par3 >> 4) - this.chunkZ;
            if (l < 0 || l >= chunkArray.length || i1 < 0 || i1 >= chunkArray[l].length) return 0;
            if (chunkArray[l][i1] == null) return 0;
            return this.chunkArray[l][i1].getBlockMetadata(par1 & 15, par2, par3 & 15);
        }
    }

    // JAVADOC METHOD $$ func_72807_a
    @SideOnly(Side.CLIENT)
    public BiomeGenBase getBiomeGenForCoords(int par1, int par2)
    {
        return this.worldObj.getBiomeGenForCoords(par1, par2);
    }

    // JAVADOC METHOD $$ func_82732_R
    public Vec3Pool getWorldVec3Pool()
    {
        return this.worldObj.getWorldVec3Pool();
    }

    public boolean func_147437_c(int p_147437_1_, int p_147437_2_, int p_147437_3_)
    {
        return this.func_147439_a(p_147437_1_, p_147437_2_, p_147437_3_).isAir(this, p_147437_1_, p_147437_2_, p_147437_3_);
    }

    // JAVADOC METHOD $$ func_72810_a
    @SideOnly(Side.CLIENT)
    public int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (par3 < 0)
        {
            par3 = 0;
        }

        if (par3 >= 256)
        {
            par3 = 255;
        }

        if (par3 >= 0 && par3 < 256 && par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 <= 30000000)
        {
            if (par1EnumSkyBlock == EnumSkyBlock.Sky && this.worldObj.provider.hasNoSky)
            {
                return 0;
            }
            else
            {
                int l;
                int i1;

                if (this.func_147439_a(par2, par3, par4).func_149710_n())
                {
                    l = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3 + 1, par4);
                    i1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2 + 1, par3, par4);
                    int j1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2 - 1, par3, par4);
                    int k1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3, par4 + 1);
                    int l1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3, par4 - 1);

                    if (i1 > l)
                    {
                        l = i1;
                    }

                    if (j1 > l)
                    {
                        l = j1;
                    }

                    if (k1 > l)
                    {
                        l = k1;
                    }

                    if (l1 > l)
                    {
                        l = l1;
                    }

                    return l;
                }
                else
                {
                    l = (par2 >> 4) - this.chunkX;
                    i1 = (par4 >> 4) - this.chunkZ;
                    return this.chunkArray[l][i1].getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
                }
            }
        }
        else
        {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    // JAVADOC METHOD $$ func_72812_b
    @SideOnly(Side.CLIENT)
    public int getSpecialBlockBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (par3 < 0)
        {
            par3 = 0;
        }

        if (par3 >= 256)
        {
            par3 = 255;
        }

        if (par3 >= 0 && par3 < 256 && par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 <= 30000000)
        {
            int l = (par2 >> 4) - this.chunkX;
            int i1 = (par4 >> 4) - this.chunkZ;
            return this.chunkArray[l][i1].getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
        }
        else
        {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    // JAVADOC METHOD $$ func_72800_K
    @SideOnly(Side.CLIENT)
    public int getHeight()
    {
        return 256;
    }

    // JAVADOC METHOD $$ func_72879_k
    public int isBlockProvidingPowerTo(int par1, int par2, int par3, int par4)
    {
        return this.func_147439_a(par1, par2, par3).func_149748_c(this, par1, par2, par3, par4);
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default)
    {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000)
        {
            return _default;
        }

        return func_147439_a(x, y, z).isSideSolid(this, x, y, z, side);
    }
}