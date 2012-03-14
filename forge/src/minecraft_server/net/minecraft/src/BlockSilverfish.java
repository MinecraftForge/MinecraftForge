package net.minecraft.src;

import java.util.Random;

public class BlockSilverfish extends Block
{
    public BlockSilverfish(int par1)
    {
        super(par1, 1, Material.clay);
        this.setHardness(0.0F);
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
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par2 == 1 ? Block.cobblestone.blockIndexInTexture : (par2 == 2 ? Block.stoneBrick.blockIndexInTexture : Block.stone.blockIndexInTexture);
    }

    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote)
        {
            EntitySilverfish var6 = new EntitySilverfish(par1World);
            var6.setLocationAndAngles((double)par2 + 0.5D, (double)par3, (double)par4 + 0.5D, 0.0F, 0.0F);
            par1World.spawnEntityInWorld(var6);
            var6.spawnExplosionParticle();
        }

        super.onBlockDestroyedByPlayer(par1World, par2, par3, par4, par5);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /**
     * Gets the blockID of the block this block is pretending to be according to this block's metadata.
     */
    public static boolean getPosingIdByMetadata(int par0)
    {
        return par0 == Block.stone.blockID || par0 == Block.cobblestone.blockID || par0 == Block.stoneBrick.blockID;
    }

    /**
     * Returns the metadata to use when a Silverfish hides in the block. Sets the block to BlockSilverfish with this
     * metadata. It changes the displayed texture client side to look like a normal block.
     */
    public static int getMetadataForBlockType(int par0)
    {
        return par0 == Block.cobblestone.blockID ? 1 : (par0 == Block.stoneBrick.blockID ? 2 : 0);
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int par1)
    {
        Block var2 = Block.stone;

        if (par1 == 1)
        {
            var2 = Block.cobblestone;
        }

        if (par1 == 2)
        {
            var2 = Block.stoneBrick;
        }

        return new ItemStack(var2);
    }
}
