package net.minecraft.src;

import java.util.Random;

public class BlockLockedChest extends Block
{
    protected BlockLockedChest(int par1)
    {
        super(par1, Material.wood);
        this.blockIndexInTexture = 26;
    }

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public int getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (par5 == 1)
        {
            return this.blockIndexInTexture - 1;
        }
        else if (par5 == 0)
        {
            return this.blockIndexInTexture - 1;
        }
        else
        {
            int var6 = par1IBlockAccess.getBlockId(par2, par3, par4 - 1);
            int var7 = par1IBlockAccess.getBlockId(par2, par3, par4 + 1);
            int var8 = par1IBlockAccess.getBlockId(par2 - 1, par3, par4);
            int var9 = par1IBlockAccess.getBlockId(par2 + 1, par3, par4);
            byte var10 = 3;

            if (Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var7])
            {
                var10 = 3;
            }

            if (Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var6])
            {
                var10 = 2;
            }

            if (Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var9])
            {
                var10 = 5;
            }

            if (Block.opaqueCubeLookup[var9] && !Block.opaqueCubeLookup[var8])
            {
                var10 = 4;
            }

            return par5 == var10 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture;
        }
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return par1 == 1 ? this.blockIndexInTexture - 1 : (par1 == 0 ? this.blockIndexInTexture - 1 : (par1 == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return true;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        par1World.setBlockWithNotify(par2, par3, par4, 0);
    }
}
