package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class BlockCauldron extends Block
{
    public BlockCauldron(int par1)
    {
        super(par1, Material.iron);
        this.blockIndexInTexture = 154;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return par1 == 1 ? 138 : (par1 == 0 ? 155 : 154);
    }

    /**
     * Adds to the supplied array any colliding bounding boxes with the passed in bounding box. Args: world, x, y, z,
     * axisAlignedBB, arrayList
     */
    public void getCollidingBoundingBoxes(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, ArrayList par6ArrayList)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
        float var7 = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, var7, 1.0F, 1.0F);
        super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var7);
        super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
        this.setBlockBounds(1.0F - var7, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - var7, 1.0F, 1.0F, 1.0F);
        super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
        this.setBlockBoundsForItemRender();
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 24;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            ItemStack var6 = par5EntityPlayer.inventory.getCurrentItem();

            if (var6 == null)
            {
                return true;
            }
            else
            {
                int var7 = par1World.getBlockMetadata(par2, par3, par4);

                if (var6.itemID == Item.bucketWater.shiftedIndex)
                {
                    if (var7 < 3)
                    {
                        if (!par5EntityPlayer.capabilities.depleteBuckets)
                        {
                            par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, new ItemStack(Item.bucketEmpty));
                        }

                        par1World.setBlockMetadataWithNotify(par2, par3, par4, 3);
                    }

                    return true;
                }
                else
                {
                    if (var6.itemID == Item.glassBottle.shiftedIndex && var7 > 0)
                    {
                        ItemStack var8 = new ItemStack(Item.potion, 1, 0);

                        if (!par5EntityPlayer.inventory.addItemStackToInventory(var8))
                        {
                            par1World.spawnEntityInWorld(new EntityItem(par1World, (double)par2 + 0.5D, (double)par3 + 1.5D, (double)par4 + 0.5D, var8));
                        }

                        --var6.stackSize;

                        if (var6.stackSize <= 0)
                        {
                            par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, (ItemStack)null);
                        }

                        par1World.setBlockMetadataWithNotify(par2, par3, par4, var7 - 1);
                    }

                    return true;
                }
            }
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Item.cauldron.shiftedIndex;
    }
}
