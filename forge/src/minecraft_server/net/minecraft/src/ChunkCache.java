package net.minecraft.src;

public class ChunkCache implements IBlockAccess
{
    private int chunkX;
    private int chunkZ;
    private Chunk[][] chunkArray;
    private boolean field_48098_d;

    /** Reference to the World object. */
    private World worldObj;

    public ChunkCache(World par1World, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        this.worldObj = par1World;
        this.chunkX = par2 >> 4;
        this.chunkZ = par4 >> 4;
        int var8 = par5 >> 4;
        int var9 = par7 >> 4;
        this.chunkArray = new Chunk[var8 - this.chunkX + 1][var9 - this.chunkZ + 1];
        this.field_48098_d = true;

        for (int var10 = this.chunkX; var10 <= var8; ++var10)
        {
            for (int var11 = this.chunkZ; var11 <= var9; ++var11)
            {
                Chunk var12 = par1World.getChunkFromChunkCoords(var10, var11);

                if (var12 != null)
                {
                    this.chunkArray[var10 - this.chunkX][var11 - this.chunkZ] = var12;

                    if (!var12.func_48556_c(par3, par6))
                    {
                        this.field_48098_d = false;
                    }
                }
            }
        }
    }

    /**
     * Returns the block ID at coords x,y,z
     */
    public int getBlockId(int par1, int par2, int par3)
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
            int var4 = (par1 >> 4) - this.chunkX;
            int var5 = (par3 >> 4) - this.chunkZ;

            if (var4 >= 0 && var4 < this.chunkArray.length && var5 >= 0 && var5 < this.chunkArray[var4].length)
            {
                Chunk var6 = this.chunkArray[var4][var5];
                return var6 == null ? 0 : var6.getBlockID(par1 & 15, par2, par3 & 15);
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    public TileEntity getBlockTileEntity(int par1, int par2, int par3)
    {
        int var4 = (par1 >> 4) - this.chunkX;
        int var5 = (par3 >> 4) - this.chunkZ;
        return this.chunkArray[var4][var5].getChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
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
            int var4 = (par1 >> 4) - this.chunkX;
            int var5 = (par3 >> 4) - this.chunkZ;
            return this.chunkArray[var4][var5].getBlockMetadata(par1 & 15, par2, par3 & 15);
        }
    }

    /**
     * Returns the block's material.
     */
    public Material getBlockMaterial(int par1, int par2, int par3)
    {
        int var4 = this.getBlockId(par1, par2, par3);
        return var4 == 0 ? Material.air : Block.blocksList[var4].blockMaterial;
    }

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean isBlockNormalCube(int par1, int par2, int par3)
    {
        Block var4 = Block.blocksList[this.getBlockId(par1, par2, par3)];
        return var4 == null ? false : var4.blockMaterial.blocksMovement() && var4.renderAsNormalBlock();
    }
}
