package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class BlockStep extends Block
{
    public static final String[] blockStepTypes = new String[] {"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick"};

    /** Boolean used to seperate different states of blocks */
    private boolean blockType;

    public BlockStep(int par1, boolean par2)
    {
        super(par1, 6, Material.rock);
        this.blockType = par2;

        if (!par2)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
        else
        {
            opaqueCubeLookup[par1] = true;
        }

        this.setLightOpacity(255);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        if (this.blockType)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            boolean var5 = (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) != 0;

            if (var5)
            {
                this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        if (this.blockType)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    /**
     * Adds to the supplied array any colliding bounding boxes with the passed in bounding box. Args: world, x, y, z,
     * axisAlignedBB, arrayList
     */
    public void getCollidingBoundingBoxes(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, ArrayList par6ArrayList)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        int var3 = par2 & 7;
        return var3 == 0 ? (par1 <= 1 ? 6 : 5) : (var3 == 1 ? (par1 == 0 ? 208 : (par1 == 1 ? 176 : 192)) : (var3 == 2 ? 4 : (var3 == 3 ? 16 : (var3 == 4 ? Block.brick.blockIndexInTexture : (var3 == 5 ? Block.stoneBrick.blockIndexInTexture : 6)))));
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return this.getBlockTextureFromSideAndMetadata(par1, 0);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return this.blockType;
    }

    /**
     * Called when a block is placed using an item. Used often for taking the facing and figuring out how to position
     * the item. Args: x, y, z, facing
     */
    public void onBlockPlaced(World par1World, int par2, int par3, int par4, int par5)
    {
        if (par5 == 0 && !this.blockType)
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4) & 7;
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 | 8);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Block.stairSingle.blockID;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return this.blockType ? 2 : 1;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    protected int damageDropped(int par1)
    {
        return par1 & 7;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return this.blockType;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (this.blockType)
        {
            super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
        }

        if (par5 != 1 && par5 != 0 && !super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5))
        {
            return false;
        }
        else
        {
            int var6 = par2 + Facing.offsetsXForSide[Facing.faceToSide[par5]];
            int var7 = par3 + Facing.offsetsYForSide[Facing.faceToSide[par5]];
            int var8 = par4 + Facing.offsetsZForSide[Facing.faceToSide[par5]];
            boolean var9 = (par1IBlockAccess.getBlockMetadata(var6, var7, var8) & 8) != 0;
            return !var9 ? (par5 == 1 ? true : (par5 == 0 && super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5) ? true : par1IBlockAccess.getBlockId(par2, par3, par4) != this.blockID || (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) != 0)) : (par5 == 0 ? true : (par5 == 1 && super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5) ? true : par1IBlockAccess.getBlockId(par2, par3, par4) != this.blockID || (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) == 0));
        }
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int par1)
    {
        return new ItemStack(Block.stairSingle.blockID, 1, par1 & 7);
    }
}
