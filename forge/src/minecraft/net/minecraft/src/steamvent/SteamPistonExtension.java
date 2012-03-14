package net.minecraft.src.steamvent;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Random;

public class SteamPistonExtension extends BlockPistonExtension
{
    /** The texture for the 'head' of the piston. Sticky or normal. */
    private int headTexture = -1;

    public SteamPistonExtension(int par1, int par2)
    {
        super(par1, par2);
        this.setStepSound(soundStoneFootstep);
        this.setHardness(0.5F);
    }

    public void setHeadTexture(int par1)
    {
        this.headTexture = par1;
    }

    public void clearHeadTexture()
    {
        this.headTexture = -1;
    }

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        super.onBlockRemoval(par1World, par2, par3, par4);
        int var5 = par1World.getBlockMetadata(par2, par3, par4);
        int var6 = Facing.faceToSide[getDirectionMeta(var5)];
        par2 += Facing.offsetsXForSide[var6];
        par3 += Facing.offsetsYForSide[var6];
        par4 += Facing.offsetsZForSide[var6];
        int var7 = par1World.getBlockId(par2, par3, par4);

        if (var7 == Block.pistonBase.blockID || var7 == Block.pistonStickyBase.blockID)
        {
            var5 = par1World.getBlockMetadata(par2, par3, par4);

            if (SteamPistonBase.isExtended(var5))
            {
                Block.blocksList[var7].dropBlockAsItem(par1World, par2, par3, par4, var5, 0);
                par1World.setBlockWithNotify(par2, par3, par4, 0);
            }
        }
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        int var3 = getDirectionMeta(par2);
        return par1 == var3 ? (this.headTexture >= 0 ? this.headTexture : ((par2 & 8) != 0 ? this.blockIndexInTexture - 1 : this.blockIndexInTexture)) : (par1 == Facing.faceToSide[var3] ? 107 : 108);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 17;
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
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return false;
    }

    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5)
    {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /**
     * Adds to the supplied array any colliding bounding boxes with the passed in bounding box. Args: world, x, y, z,
     * axisAlignedBB, arrayList
     */
    public void getCollidingBoundingBoxes(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, ArrayList par6ArrayList)
    {
        int var7 = par1World.getBlockMetadata(par2, par3, par4);

        switch (getDirectionMeta(var7))
        {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                break;

            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                break;

            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                break;

            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                break;

            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                break;

            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
                this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
                super.getCollidingBoundingBoxes(par1World, par2, par3, par4, par5AxisAlignedBB, par6ArrayList);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);

        switch (getDirectionMeta(var5))
        {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                break;

            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;

            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                break;

            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                break;

            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                break;

            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        int var6 = getDirectionMeta(par1World.getBlockMetadata(par2, par3, par4));
        int var7 = par1World.getBlockId(par2 - Facing.offsetsXForSide[var6], par3 - Facing.offsetsYForSide[var6], par4 - Facing.offsetsZForSide[var6]);

        if (var7 != mod_Steamvent.steamPiston.blockID )// && var7 != Block.pistonStickyBase.blockID)
        {
            par1World.setBlockWithNotify(par2, par3, par4, 0);
        }
        else
        {
            Block.blocksList[var7].onNeighborBlockChange(par1World, par2 - Facing.offsetsXForSide[var6], par3 - Facing.offsetsYForSide[var6], par4 - Facing.offsetsZForSide[var6], par5);
        }
    }

    public static int getDirectionMeta(int par0)
    {
        return par0 & 7;
    }
}
