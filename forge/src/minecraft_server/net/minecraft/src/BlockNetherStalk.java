package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class BlockNetherStalk extends BlockFlower
{
    protected BlockNetherStalk(int par1)
    {
        super(par1, 226);
        this.setTickRandomly(true);
        float var2 = 0.5F;
        this.setBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
    }

    /**
     * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
     * blockID passed in. Args: blockID
     */
    protected boolean canThisPlantGrowOnThisBlockID(int par1)
    {
        return par1 == Block.slowSand.blockID;
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World par1World, int par2, int par3, int par4)
    {
        return this.canThisPlantGrowOnThisBlockID(par1World.getBlockId(par2, par3 - 1, par4));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);

        if (var6 < 3)
        {
            BiomeGenBase var7 = par1World.func_48091_a(par2, par4);

            if (var7 instanceof BiomeGenHell && par5Random.nextInt(10) == 0)
            {
                ++var6;
                par1World.setBlockMetadataWithNotify(par2, par3, par4, var6);
            }
        }

        super.updateTick(par1World, par2, par3, par4, par5Random);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par2 >= 3 ? this.blockIndexInTexture + 2 : (par2 > 0 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 6;
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int n = 1;
        if (metadata >= 3)
        {
            n = 2 + world.rand.nextInt(3) + (fortune > 0 ? world.rand.nextInt(fortune + 1) : 0);
        }
        for (int m = 0; m < n; m++)
        {
            ret.add(new ItemStack(Item.netherStalkSeeds));
        }
        return ret;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }
}
