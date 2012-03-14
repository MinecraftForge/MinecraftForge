package net.minecraft.src;

import java.util.Random;

public class BlockIce extends BlockBreakable
{
    public BlockIce(int par1, int par2)
    {
        super(par1, par2, Material.ice, false);
        this.slipperiness = 0.98F;
        this.setTickRandomly(true);
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
        Material var7 = par1World.getBlockMaterial(par3, par4 - 1, par5);

        if (var7.blocksMovement() || var7.isLiquid())
        {
            par1World.setBlockWithNotify(par3, par4, par5, Block.waterMoving.blockID);
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (par1World.getSavedLightValue(EnumSkyBlock.Block, par2, par3, par4) > 11 - Block.lightOpacity[this.blockID])
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockWithNotify(par2, par3, par4, Block.waterStill.blockID);
        }
    }

    /**
     * returns the mobility flag of a block's material
     */
    public int getMobilityFlag()
    {
        return 0;
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int par1)
    {
        return null;
    }
}
