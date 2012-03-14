package net.minecraft.src.steamvent;
import net.minecraft.src.*;

import java.util.Random;

public class SteamPistonMoving extends BlockContainer
{
    public SteamPistonMoving(int par1)
    {
        super(par1, Material.piston);
        this.setHardness(-1.0F);
    }

    /**
     * Returns the TileEntity used by this block.
     */
    public TileEntity getBlockEntity()
    {
        return null;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {}

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4)
    {
        TileEntity var5 = par1World.getBlockTileEntity(par2, par3, par4);

        if (var5 != null && var5 instanceof SteamPistonLogic)
        {
            ((SteamPistonLogic)var5).clearPistonTileEntity();
        }
        else
        {
            super.onBlockRemoval(par1World, par2, par3, par4);
        }
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
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return -1;
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
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        if (!par1World.isRemote && par1World.getBlockTileEntity(par2, par3, par4) == null)
        {
            par1World.setBlockWithNotify(par2, par3, par4, 0);
            return true;
        }
        else
        {
            return false;
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
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        if (!par1World.isRemote)
        {
            SteamPistonLogic var8 = this.getTileEntityAtLocation(par1World, par2, par3, par4);

            if (var8 != null)
            {
                Block.blocksList[var8.getStoredBlockID()].dropBlockAsItem(par1World, par2, par3, par4, var8.getBlockMetadata(), 0);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote && par1World.getBlockTileEntity(par2, par3, par4) == null)
        {
            ;
        }
    }

    /**
     * gets a new SteamPistonLogic created with the arguments provided.
     */
    public static TileEntity getTileEntity(int par0, int par1, int par2, boolean par3, boolean par4)
    {
        return new SteamPistonLogic(par0, par1, par2, par3, par4);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        SteamPistonLogic var5 = this.getTileEntityAtLocation(par1World, par2, par3, par4);

        if (var5 == null)
        {
            return null;
        }
        else
        {
            float var6 = var5.getProgress(0.0F);

            if (var5.isExtending())
            {
                var6 = 1.0F - var6;
            }

            return this.getAxisAlignedBB(par1World, par2, par3, par4, var5.getStoredBlockID(), var6, var5.getPistonOrientation());
        }
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        SteamPistonLogic var5 = this.getTileEntityAtLocation(par1IBlockAccess, par2, par3, par4);

        if (var5 != null)
        {
            Block var6 = Block.blocksList[var5.getStoredBlockID()];

            if (var6 == null || var6 == this)
            {
                return;
            }

            var6.setBlockBoundsBasedOnState(par1IBlockAccess, par2, par3, par4);
            float var7 = var5.getProgress(0.0F);

            if (var5.isExtending())
            {
                var7 = 1.0F - var7;
            }

            int var8 = var5.getPistonOrientation();
            this.minX = var6.minX - (double)((float)Facing.offsetsXForSide[var8] * var7);
            this.minY = var6.minY - (double)((float)Facing.offsetsYForSide[var8] * var7);
            this.minZ = var6.minZ - (double)((float)Facing.offsetsZForSide[var8] * var7);
            this.maxX = var6.maxX - (double)((float)Facing.offsetsXForSide[var8] * var7);
            this.maxY = var6.maxY - (double)((float)Facing.offsetsYForSide[var8] * var7);
            this.maxZ = var6.maxZ - (double)((float)Facing.offsetsZForSide[var8] * var7);
        }
    }

    /**
     * gets the axis-alignedbb of this piston
     */
    public AxisAlignedBB getAxisAlignedBB(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        if (par5 != 0 && par5 != this.blockID)
        {
            AxisAlignedBB var8 = Block.blocksList[par5].getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);

            if (var8 == null)
            {
                return null;
            }
            else
            {
                if (Facing.offsetsXForSide[par7] < 0)
                {
                    var8.minX -= (double)((float)Facing.offsetsXForSide[par7] * par6);
                }
                else
                {
                    var8.maxX -= (double)((float)Facing.offsetsXForSide[par7] * par6);
                }

                if (Facing.offsetsYForSide[par7] < 0)
                {
                    var8.minY -= (double)((float)Facing.offsetsYForSide[par7] * par6);
                }
                else
                {
                    var8.maxY -= (double)((float)Facing.offsetsYForSide[par7] * par6);
                }

                if (Facing.offsetsZForSide[par7] < 0)
                {
                    var8.minZ -= (double)((float)Facing.offsetsZForSide[par7] * par6);
                }
                else
                {
                    var8.maxZ -= (double)((float)Facing.offsetsZForSide[par7] * par6);
                }

                return var8;
            }
        }
        else
        {
            return null;
        }
    }

    private SteamPistonLogic getTileEntityAtLocation(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        TileEntity var5 = par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
        return var5 != null && var5 instanceof SteamPistonLogic ? (SteamPistonLogic)var5 : null;
    }
}
