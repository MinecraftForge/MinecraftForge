package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.IShearable;

public class BlockTallGrass extends BlockFlower implements IShearable
{
    protected BlockTallGrass(int par1, int par2)
    {
        super(par1, par2, Material.vine);
        float var3 = 0.4F;
        this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, 0.8F, 0.5F + var3);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par2 == 1 ? this.blockIndexInTexture : (par2 == 2 ? this.blockIndexInTexture + 16 + 1 : (par2 == 0 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture));
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return -1;
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    public int quantityDroppedWithBonus(int par1, Random par2Random)
    {
        return 1 + par2Random.nextInt(par1 * 2 + 1);
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
    }
    
    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        if (world.rand.nextInt(8) != 0)
        {
            return ret;
        }
        
        ItemStack item = ForgeHooks.getGrassSeed(world);
        if (item != null)
        {
            ret.add(item);
        }
        return ret;
    }

    @Override
    public boolean isShearable(ItemStack item, World world, int x, int y, int z) 
    {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune) 
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z)));
        return ret;
    }
}
