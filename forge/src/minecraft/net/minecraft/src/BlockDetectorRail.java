package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockDetectorRail extends BlockRail
{
    public BlockDetectorRail(int par1, int par2)
    {
        super(par1, par2, true);
        this.setTickRandomly(true);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 20;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        if (!par1World.isRemote)
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);

            if ((var6 & 8) == 0)
            {
                this.setStateIfMinecartInteractsWithRail(par1World, par2, par3, par4, var6);
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);

            if ((var6 & 8) != 0)
            {
                this.setStateIfMinecartInteractsWithRail(par1World, par2, par3, par4, var6);
            }
        }
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean isPoweringTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) != 0;
    }

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean isIndirectlyPoweringTo(World par1World, int par2, int par3, int par4, int par5)
    {
        return (par1World.getBlockMetadata(par2, par3, par4) & 8) == 0 ? false : par5 == 1;
    }

    /**
     * Update the detector rail power state if a minecart enter, stays or leave the block.
     */
    private void setStateIfMinecartInteractsWithRail(World par1World, int par2, int par3, int par4, int par5)
    {
        boolean var6 = (par5 & 8) != 0;
        boolean var7 = false;
        float var8 = 0.125F;
        List var9 = par1World.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBoxFromPool((double)((float)par2 + var8), (double)par3, (double)((float)par4 + var8), (double)((float)(par2 + 1) - var8), (double)((float)(par3 + 1) - var8), (double)((float)(par4 + 1) - var8)));

        if (var9.size() > 0)
        {
            var7 = true;
        }

        if (var7 && !var6)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, par5 | 8);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            par1World.markBlocksDirty(par2, par3, par4, par2, par3, par4);
        }

        if (!var7 && var6)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, par5 & 7);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            par1World.markBlocksDirty(par2, par3, par4, par2, par3, par4);
        }

        if (var7)
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
        }
    }
}
