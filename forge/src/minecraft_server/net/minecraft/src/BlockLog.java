package net.minecraft.src;

import java.util.Random;

public class BlockLog extends Block
{
    protected BlockLog(int par1)
    {
        super(par1, Material.wood);
        this.blockIndexInTexture = 20;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Block.wood.blockID;
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
    }

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        byte var5 = 4;
        int var6 = var5 + 1;

        if (par1World.checkChunksExist(par2 - var6, par3 - var6, par4 - var6, par2 + var6, par3 + var6, par4 + var6))
        {
            for (int var7 = -var5; var7 <= var5; ++var7)
            {
                for (int var8 = -var5; var8 <= var5; ++var8)
                {
                    for (int var9 = -var5; var9 <= var5; ++var9)
                    {
                        int var10 = par1World.getBlockId(par2 + var7, par3 + var8, par4 + var9);

                        if (var10 == Block.leaves.blockID)
                        {
                            int var11 = par1World.getBlockMetadata(par2 + var7, par3 + var8, par4 + var9);

                            if ((var11 & 8) == 0)
                            {
                                par1World.setBlockMetadata(par2 + var7, par3 + var8, par4 + var9, var11 | 8);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par1 == 1 ? 21 : (par1 == 0 ? 21 : (par2 == 1 ? 116 : (par2 == 2 ? 117 : (par2 == 3 ? 153 : 20))));
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    protected int damageDropped(int par1)
    {
        return par1;
    }
}
