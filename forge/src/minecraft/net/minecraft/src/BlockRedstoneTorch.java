package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRedstoneTorch extends BlockTorch
{
    /** Whether the redstone torch is currently active or not. */
    private boolean torchActive = false;

    /**
     * An array of when redstone torches became active.  Used for redstone torches to burn out.
     */
    private static List torchUpdates = new ArrayList();

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par1 == 1 ? Block.redstoneWire.getBlockTextureFromSideAndMetadata(par1, par2) : super.getBlockTextureFromSideAndMetadata(par1, par2);
    }

    private boolean checkForBurnout(World par1World, int par2, int par3, int par4, boolean par5)
    {
        if (par5)
        {
            torchUpdates.add(new RedstoneUpdateInfo(par2, par3, par4, par1World.getWorldTime()));
        }

        int var6 = 0;

        for (int var7 = 0; var7 < torchUpdates.size(); ++var7)
        {
            RedstoneUpdateInfo var8 = (RedstoneUpdateInfo)torchUpdates.get(var7);

            if (var8.x == par2 && var8.y == par3 && var8.z == par4)
            {
                ++var6;

                if (var6 >= 8)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockRedstoneTorch(int par1, int par2, boolean par3)
    {
        super(par1, par2);
        this.torchActive = par3;
        this.setTickRandomly(true);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 2;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0)
        {
            super.onBlockAdded(par1World, par2, par3, par4);
        }

        if (this.torchActive)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        }
    }

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        if (this.torchActive)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        }
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean isPoweringTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (!this.torchActive)
        {
            return false;
        }
        else
        {
            int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
            return var6 == 5 && par5 == 1 ? false : (var6 == 3 && par5 == 3 ? false : (var6 == 4 && par5 == 2 ? false : (var6 == 1 && par5 == 5 ? false : var6 != 2 || par5 != 4)));
        }
    }

    /**
     * Returns true or false based on whether the block the torch is attached to is providing indirect power.
     */
    private boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockMetadata(par2, par3, par4);
        return var5 == 5 && par1World.isBlockIndirectlyProvidingPowerTo(par2, par3 - 1, par4, 0) ? true : (var5 == 3 && par1World.isBlockIndirectlyProvidingPowerTo(par2, par3, par4 - 1, 2) ? true : (var5 == 4 && par1World.isBlockIndirectlyProvidingPowerTo(par2, par3, par4 + 1, 3) ? true : (var5 == 1 && par1World.isBlockIndirectlyProvidingPowerTo(par2 - 1, par3, par4, 4) ? true : var5 == 2 && par1World.isBlockIndirectlyProvidingPowerTo(par2 + 1, par3, par4, 5))));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        boolean var6 = this.isIndirectlyPowered(par1World, par2, par3, par4);

        while (torchUpdates.size() > 0 && par1World.getWorldTime() - ((RedstoneUpdateInfo)torchUpdates.get(0)).updateTime > 60L)
        {
            torchUpdates.remove(0);
        }

        if (this.torchActive)
        {
            if (var6)
            {
                par1World.setBlockAndMetadataWithNotify(par2, par3, par4, Block.torchRedstoneIdle.blockID, par1World.getBlockMetadata(par2, par3, par4));

                if (this.checkForBurnout(par1World, par2, par3, par4, true))
                {
                    par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

                    for (int var7 = 0; var7 < 5; ++var7)
                    {
                        double var8 = (double)par2 + par5Random.nextDouble() * 0.6D + 0.2D;
                        double var10 = (double)par3 + par5Random.nextDouble() * 0.6D + 0.2D;
                        double var12 = (double)par4 + par5Random.nextDouble() * 0.6D + 0.2D;
                        par1World.spawnParticle("smoke", var8, var10, var12, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
        else if (!var6 && !this.checkForBurnout(par1World, par2, par3, par4, false))
        {
            par1World.setBlockAndMetadataWithNotify(par2, par3, par4, Block.torchRedstoneActive.blockID, par1World.getBlockMetadata(par2, par3, par4));
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
    }

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean isIndirectlyPoweringTo(World par1World, int par2, int par3, int par4, int par5)
    {
        return par5 == 0 ? this.isPoweringTo(par1World, par2, par3, par4, par5) : false;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Block.torchRedstoneActive.blockID;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (this.torchActive)
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);
            double var7 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double var9 = (double)((float)par3 + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double var11 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double var13 = 0.2199999988079071D;
            double var15 = 0.27000001072883606D;

            if (var6 == 1)
            {
                par1World.spawnParticle("reddust", var7 - var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
            }
            else if (var6 == 2)
            {
                par1World.spawnParticle("reddust", var7 + var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
            }
            else if (var6 == 3)
            {
                par1World.spawnParticle("reddust", var7, var9 + var13, var11 - var15, 0.0D, 0.0D, 0.0D);
            }
            else if (var6 == 4)
            {
                par1World.spawnParticle("reddust", var7, var9 + var13, var11 + var15, 0.0D, 0.0D, 0.0D);
            }
            else
            {
                par1World.spawnParticle("reddust", var7, var9, var11, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
