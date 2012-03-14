package net.minecraft.src;

import java.util.Random;

public class BlockRedstoneOre extends Block
{
    private boolean glowing;

    public BlockRedstoneOre(int par1, int par2, boolean par3)
    {
        super(par1, par2, Material.rock);

        if (par3)
        {
            this.setTickRandomly(true);
        }

        this.glowing = par3;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 30;
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        this.glow(par1World, par2, par3, par4);
        super.onBlockClicked(par1World, par2, par3, par4, par5EntityPlayer);
    }

    /**
     * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
     */
    public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        this.glow(par1World, par2, par3, par4);
        super.onEntityWalking(par1World, par2, par3, par4, par5Entity);
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        this.glow(par1World, par2, par3, par4);
        return super.blockActivated(par1World, par2, par3, par4, par5EntityPlayer);
    }

    /**
     * The redstone ore glows.
     */
    private void glow(World par1World, int par2, int par3, int par4)
    {
        this.sparkle(par1World, par2, par3, par4);

        if (this.blockID == Block.oreRedstone.blockID)
        {
            par1World.setBlockWithNotify(par2, par3, par4, Block.oreRedstoneGlowing.blockID);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (this.blockID == Block.oreRedstoneGlowing.blockID)
        {
            par1World.setBlockWithNotify(par2, par3, par4, Block.oreRedstone.blockID);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Item.redstone.shiftedIndex;
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    public int quantityDroppedWithBonus(int par1, Random par2Random)
    {
        return this.quantityDropped(par2Random) + par2Random.nextInt(par1 + 1);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 4 + par1Random.nextInt(2);
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (this.glowing)
        {
            this.sparkle(par1World, par2, par3, par4);
        }
    }

    /**
     * The redstone ore sparkles.
     */
    private void sparkle(World par1World, int par2, int par3, int par4)
    {
        Random var5 = par1World.rand;
        double var6 = 0.0625D;

        for (int var8 = 0; var8 < 6; ++var8)
        {
            double var9 = (double)((float)par2 + var5.nextFloat());
            double var11 = (double)((float)par3 + var5.nextFloat());
            double var13 = (double)((float)par4 + var5.nextFloat());

            if (var8 == 0 && !par1World.isBlockOpaqueCube(par2, par3 + 1, par4))
            {
                var11 = (double)(par3 + 1) + var6;
            }

            if (var8 == 1 && !par1World.isBlockOpaqueCube(par2, par3 - 1, par4))
            {
                var11 = (double)(par3 + 0) - var6;
            }

            if (var8 == 2 && !par1World.isBlockOpaqueCube(par2, par3, par4 + 1))
            {
                var13 = (double)(par4 + 1) + var6;
            }

            if (var8 == 3 && !par1World.isBlockOpaqueCube(par2, par3, par4 - 1))
            {
                var13 = (double)(par4 + 0) - var6;
            }

            if (var8 == 4 && !par1World.isBlockOpaqueCube(par2 + 1, par3, par4))
            {
                var9 = (double)(par2 + 1) + var6;
            }

            if (var8 == 5 && !par1World.isBlockOpaqueCube(par2 - 1, par3, par4))
            {
                var9 = (double)(par2 + 0) - var6;
            }

            if (var9 < (double)par2 || var9 > (double)(par2 + 1) || var11 < 0.0D || var11 > (double)(par3 + 1) || var13 < (double)par4 || var13 > (double)(par4 + 1))
            {
                par1World.spawnParticle("reddust", var9, var11, var13, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int par1)
    {
        return new ItemStack(Block.oreRedstone);
    }
}
