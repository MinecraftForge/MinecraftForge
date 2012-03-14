package net.minecraft.src;

import java.util.Random;

public class BlockSand extends Block
{
    /** Do blocks fall instantly to where they stop or do they fall over time */
    public static boolean fallInstantly = false;

    public BlockSand(int par1, int par2)
    {
        super(par1, par2, Material.sand);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        this.tryToFall(par1World, par2, par3, par4);
    }

    /**
     * If there is space to fall below will start this block falling
     */
    private void tryToFall(World par1World, int par2, int par3, int par4)
    {
        if (canFallBelow(par1World, par2, par3 - 1, par4) && par3 >= 0)
        {
            byte var8 = 32;

            if (!fallInstantly && par1World.checkChunksExist(par2 - var8, par3 - var8, par4 - var8, par2 + var8, par3 + var8, par4 + var8))
            {
                if (!par1World.isRemote)
                {
                    EntityFallingSand var9 = new EntityFallingSand(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), this.blockID);
                    par1World.spawnEntityInWorld(var9);
                }
            }
            else
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);

                while (canFallBelow(par1World, par2, par3 - 1, par4) && par3 > 0)
                {
                    --par3;
                }

                if (par3 > 0)
                {
                    par1World.setBlockWithNotify(par2, par3, par4, this.blockID);
                }
            }
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 3;
    }

    /**
     * Checks to see if the sand can fall into the block below it
     */
    public static boolean canFallBelow(World par0World, int par1, int par2, int par3)
    {
        int var4 = par0World.getBlockId(par1, par2, par3);

        if (var4 == 0)
        {
            return true;
        }
        else if (var4 == Block.fire.blockID)
        {
            return true;
        }
        else
        {
            Material var5 = Block.blocksList[var4].blockMaterial;
            return var5 == Material.water ? true : var5 == Material.lava;
        }
    }
}
