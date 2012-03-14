package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.forge.IShearable;

public class BlockVine extends Block implements IShearable
{
    public BlockVine(int par1)
    {
        super(par1, 143, Material.vine);
        this.setTickRandomly(true);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 20;
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
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        float var7 = 1.0F;
        float var8 = 1.0F;
        float var9 = 1.0F;
        float var10 = 0.0F;
        float var11 = 0.0F;
        float var12 = 0.0F;
        boolean var13 = var6 > 0;

        if ((var6 & 2) != 0)
        {
            var10 = Math.max(var10, 0.0625F);
            var7 = 0.0F;
            var8 = 0.0F;
            var11 = 1.0F;
            var9 = 0.0F;
            var12 = 1.0F;
            var13 = true;
        }

        if ((var6 & 8) != 0)
        {
            var7 = Math.min(var7, 0.9375F);
            var10 = 1.0F;
            var8 = 0.0F;
            var11 = 1.0F;
            var9 = 0.0F;
            var12 = 1.0F;
            var13 = true;
        }

        if ((var6 & 4) != 0)
        {
            var12 = Math.max(var12, 0.0625F);
            var9 = 0.0F;
            var7 = 0.0F;
            var10 = 1.0F;
            var8 = 0.0F;
            var11 = 1.0F;
            var13 = true;
        }

        if ((var6 & 1) != 0)
        {
            var9 = Math.min(var9, 0.9375F);
            var12 = 1.0F;
            var7 = 0.0F;
            var10 = 1.0F;
            var8 = 0.0F;
            var11 = 1.0F;
            var13 = true;
        }

        if (!var13 && this.canBePlacedOn(par1IBlockAccess.getBlockId(par2, par3 + 1, par4)))
        {
            var8 = Math.min(var8, 0.9375F);
            var11 = 1.0F;
            var7 = 0.0F;
            var10 = 1.0F;
            var9 = 0.0F;
            var12 = 1.0F;
        }

        this.setBlockBounds(var7, var8, var9, var10, var11, var12);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5)
    {
        switch (par5)
        {
            case 1:
                return this.canBePlacedOn(par1World.getBlockId(par2, par3 + 1, par4));

            case 2:
                return this.canBePlacedOn(par1World.getBlockId(par2, par3, par4 + 1));

            case 3:
                return this.canBePlacedOn(par1World.getBlockId(par2, par3, par4 - 1));

            case 4:
                return this.canBePlacedOn(par1World.getBlockId(par2 + 1, par3, par4));

            case 5:
                return this.canBePlacedOn(par1World.getBlockId(par2 - 1, par3, par4));

            default:
                return false;
        }
    }

    /**
     * returns true if a vine can be placed on that block (checks for render as normal block and if it is solid)
     */
    private boolean canBePlacedOn(int par1)
    {
        if (par1 == 0)
        {
            return false;
        }
        else
        {
            Block var2 = Block.blocksList[par1];
            return var2.renderAsNormalBlock() && var2.blockMaterial.blocksMovement();
        }
    }

    /**
     * Returns if the vine can stay in the world. It also changes the metadata according to neighboring blocks.
     */
    private boolean canVineStay(World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockMetadata(par2, par3, par4);
        int var6 = var5;

        if (var5 > 0)
        {
            for (int var7 = 0; var7 <= 3; ++var7)
            {
                int var8 = 1 << var7;

                if ((var5 & var8) != 0 && !this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var7], par3, par4 + Direction.offsetZ[var7])) && (par1World.getBlockId(par2, par3 + 1, par4) != this.blockID || (par1World.getBlockMetadata(par2, par3 + 1, par4) & var8) == 0))
                {
                    var6 &= ~var8;
                }
            }
        }

        if (var6 == 0 && !this.canBePlacedOn(par1World.getBlockId(par2, par3 + 1, par4)))
        {
            return false;
        }
        else
        {
            if (var6 != var5)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, var6);
            }

            return true;
        }
    }

    public int getBlockColor()
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    public int getRenderColor(int par1)
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return par1IBlockAccess.func_48454_a(par2, par4).func_48412_k();
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote && !this.canVineStay(par1World, par2, par3, par4))
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockWithNotify(par2, par3, par4, 0);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote && par1World.rand.nextInt(4) == 0)
        {
            byte var6 = 4;
            int var7 = 5;
            boolean var8 = false;
            int var9;
            int var10;
            int var11;
            label138:

            for (var9 = par2 - var6; var9 <= par2 + var6; ++var9)
            {
                for (var10 = par4 - var6; var10 <= par4 + var6; ++var10)
                {
                    for (var11 = par3 - 1; var11 <= par3 + 1; ++var11)
                    {
                        if (par1World.getBlockId(var9, var11, var10) == this.blockID)
                        {
                            --var7;

                            if (var7 <= 0)
                            {
                                var8 = true;
                                break label138;
                            }
                        }
                    }
                }
            }

            var9 = par1World.getBlockMetadata(par2, par3, par4);
            var10 = par1World.rand.nextInt(6);
            var11 = Direction.vineGrowth[var10];
            int var12;
            int var13;

            if (var10 == 1 && par3 < 255 && par1World.isAirBlock(par2, par3 + 1, par4))
            {
                if (var8)
                {
                    return;
                }

                var12 = par1World.rand.nextInt(16) & var9;

                if (var12 > 0)
                {
                    for (var13 = 0; var13 <= 3; ++var13)
                    {
                        if (!this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var13], par3 + 1, par4 + Direction.offsetZ[var13])))
                        {
                            var12 &= ~(1 << var13);
                        }
                    }

                    if (var12 > 0)
                    {
                        par1World.setBlockAndMetadataWithNotify(par2, par3 + 1, par4, this.blockID, var12);
                    }
                }
            }
            else
            {
                int var14;

                if (var10 >= 2 && var10 <= 5 && (var9 & 1 << var11) == 0)
                {
                    if (var8)
                    {
                        return;
                    }

                    var12 = par1World.getBlockId(par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11]);

                    if (var12 != 0 && Block.blocksList[var12] != null)
                    {
                        if (Block.blocksList[var12].blockMaterial.isOpaque() && Block.blocksList[var12].renderAsNormalBlock())
                        {
                            par1World.setBlockMetadataWithNotify(par2, par3, par4, var9 | 1 << var11);
                        }
                    }
                    else
                    {
                        var13 = var11 + 1 & 3;
                        var14 = var11 + 3 & 3;

                        if ((var9 & 1 << var13) != 0 && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var11] + Direction.offsetX[var13], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var13])))
                        {
                            par1World.setBlockAndMetadataWithNotify(par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11], this.blockID, 1 << var13);
                        }
                        else if ((var9 & 1 << var14) != 0 && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var11] + Direction.offsetX[var14], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var14])))
                        {
                            par1World.setBlockAndMetadataWithNotify(par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11], this.blockID, 1 << var14);
                        }
                        else if ((var9 & 1 << var13) != 0 && par1World.isAirBlock(par2 + Direction.offsetX[var11] + Direction.offsetX[var13], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var13]) && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var13], par3, par4 + Direction.offsetZ[var13])))
                        {
                            par1World.setBlockAndMetadataWithNotify(par2 + Direction.offsetX[var11] + Direction.offsetX[var13], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var13], this.blockID, 1 << (var11 + 2 & 3));
                        }
                        else if ((var9 & 1 << var14) != 0 && par1World.isAirBlock(par2 + Direction.offsetX[var11] + Direction.offsetX[var14], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var14]) && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var14], par3, par4 + Direction.offsetZ[var14])))
                        {
                            par1World.setBlockAndMetadataWithNotify(par2 + Direction.offsetX[var11] + Direction.offsetX[var14], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var14], this.blockID, 1 << (var11 + 2 & 3));
                        }
                        else if (this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var11], par3 + 1, par4 + Direction.offsetZ[var11])))
                        {
                            par1World.setBlockAndMetadataWithNotify(par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11], this.blockID, 0);
                        }
                    }
                }
                else if (par3 > 1)
                {
                    var12 = par1World.getBlockId(par2, par3 - 1, par4);

                    if (var12 == 0)
                    {
                        var13 = par1World.rand.nextInt(16) & var9;

                        if (var13 > 0)
                        {
                            par1World.setBlockAndMetadataWithNotify(par2, par3 - 1, par4, this.blockID, var13);
                        }
                    }
                    else if (var12 == this.blockID)
                    {
                        var13 = par1World.rand.nextInt(16) & var9;
                        var14 = par1World.getBlockMetadata(par2, par3 - 1, par4);

                        if (var14 != (var14 | var13))
                        {
                            par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, var14 | var13);
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when a block is placed using an item. Used often for taking the facing and figuring out how to position
     * the item. Args: x, y, z, facing
     */
    public void onBlockPlaced(World par1World, int par2, int par3, int par4, int par5)
    {
        byte var6 = 0;

        switch (par5)
        {
            case 2:
                var6 = 1;
                break;

            case 3:
                var6 = 4;
                break;

            case 4:
                var6 = 8;
                break;

            case 5:
                var6 = 2;
        }

        if (var6 != 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6);
        }
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

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
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
        ret.add(new ItemStack(this, 1, 0));
        return ret;
    }
    
    @Override
    public boolean isLadder(World world, int x, int y, int z) 
    {
        return true;
    }
}
