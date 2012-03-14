package net.minecraft.src;

import java.util.Random;

public class BlockStationary extends BlockFluid
{
    protected BlockStationary(int par1, Material par2Material)
    {
        super(par1, par2Material);
        this.setTickRandomly(false);

        if (par2Material == Material.lava)
        {
            this.setTickRandomly(true);
        }
    }

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return this.blockMaterial != Material.lava;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);

        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            this.setNotStationary(par1World, par2, par3, par4);
        }
    }

    /**
     * Changes the block ID to that of an updating fluid.
     */
    private void setNotStationary(World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockMetadata(par2, par3, par4);
        par1World.editingBlocks = true;
        par1World.setBlockAndMetadata(par2, par3, par4, this.blockID - 1, var5);
        par1World.markBlocksDirty(par2, par3, par4, par2, par3, par4);
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID - 1, this.tickRate());
        par1World.editingBlocks = false;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (this.blockMaterial == Material.lava)
        {
            int var6 = par5Random.nextInt(3);
            int var7;
            int var8;

            for (var7 = 0; var7 < var6; ++var7)
            {
                par2 += par5Random.nextInt(3) - 1;
                ++par3;
                par4 += par5Random.nextInt(3) - 1;
                var8 = par1World.getBlockId(par2, par3, par4);

                if (var8 == 0)
                {
                    if (this.isFlammable(par1World, par2 - 1, par3, par4) || this.isFlammable(par1World, par2 + 1, par3, par4) || this.isFlammable(par1World, par2, par3, par4 - 1) || this.isFlammable(par1World, par2, par3, par4 + 1) || this.isFlammable(par1World, par2, par3 - 1, par4) || this.isFlammable(par1World, par2, par3 + 1, par4))
                    {
                        par1World.setBlockWithNotify(par2, par3, par4, Block.fire.blockID);
                        return;
                    }
                }
                else if (Block.blocksList[var8].blockMaterial.blocksMovement())
                {
                    return;
                }
            }

            if (var6 == 0)
            {
                var7 = par2;
                var8 = par4;

                for (int var9 = 0; var9 < 3; ++var9)
                {
                    par2 = var7 + par5Random.nextInt(3) - 1;
                    par4 = var8 + par5Random.nextInt(3) - 1;

                    if (par1World.isAirBlock(par2, par3 + 1, par4) && this.isFlammable(par1World, par2, par3, par4))
                    {
                        par1World.setBlockWithNotify(par2, par3 + 1, par4, Block.fire.blockID);
                    }
                }
            }
        }
    }

    /**
     * Checks to see if the block is flammable.
     */
    private boolean isFlammable(World par1World, int par2, int par3, int par4)
    {
        return par1World.getBlockMaterial(par2, par3, par4).getCanBurn();
    }
}
