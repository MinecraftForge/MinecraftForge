package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class BlockBrewingStand extends BlockContainer
{
    private Random rand = new Random();

    public BlockBrewingStand(int par1)
    {
        super(par1, Material.iron);
        this.blockIndexInTexture = 157;
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
        return 25;
    }

    /**
     * Returns the TileEntity used by this block.
     */
    public TileEntity getBlockEntity()
    {
        return new TileEntityBrewingStand();
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Adds to the supplied array any colliding bounding boxes with the passed in bounding box. Args: world, x, y, z,
     * axisAlignedBB, arrayList
     */
    public void getCollidingBoundingBoxes(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, ArrayList par6ArrayList)
    {
        this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
        super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
        this.setBlockBoundsForItemRender();
        super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
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
            TileEntityBrewingStand var6 = (TileEntityBrewingStand)par1World.getBlockTileEntity(par2, par3, par4);

            if (var6 != null)
            {
                par5EntityPlayer.displayGUIBrewingStand(var6);
            }

            return true;
        }
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        double var6 = (double)((float)par2 + 0.4F + par5Random.nextFloat() * 0.2F);
        double var8 = (double)((float)par3 + 0.7F + par5Random.nextFloat() * 0.3F);
        double var10 = (double)((float)par4 + 0.4F + par5Random.nextFloat() * 0.2F);
        par1World.spawnParticle("smoke", var6, var8, var10, 0.0D, 0.0D, 0.0D);
    }

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        TileEntity var5 = par1World.getBlockTileEntity(par2, par3, par4);

        if (var5 != null && var5 instanceof TileEntityBrewingStand)
        {
            TileEntityBrewingStand var6 = (TileEntityBrewingStand)var5;

            for (int var7 = 0; var7 < var6.getSizeInventory(); ++var7)
            {
                ItemStack var8 = var6.getStackInSlot(var7);

                if (var8 != null)
                {
                    float var9 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float var10 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float var11 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (var8.stackSize > 0)
                    {
                        int var12 = this.rand.nextInt(21) + 10;

                        if (var12 > var8.stackSize)
                        {
                            var12 = var8.stackSize;
                        }

                        var8.stackSize -= var12;
                        EntityItem var13 = new EntityItem(par1World, (double)((float)par2 + var9), (double)((float)par3 + var10), (double)((float)par4 + var11), new ItemStack(var8.itemID, var12, var8.getItemDamage()));
                        float var14 = 0.05F;
                        var13.motionX = (double)((float)this.rand.nextGaussian() * var14);
                        var13.motionY = (double)((float)this.rand.nextGaussian() * var14 + 0.2F);
                        var13.motionZ = (double)((float)this.rand.nextGaussian() * var14);
                        par1World.spawnEntityInWorld(var13);
                    }
                }
            }
        }

        super.onBlockRemoval(par1World, par2, par3, par4);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Item.brewingStand.shiftedIndex;
    }
}
