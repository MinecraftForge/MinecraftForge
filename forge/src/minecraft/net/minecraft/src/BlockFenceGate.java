package net.minecraft.src;

public class BlockFenceGate extends BlockDirectional
{
    public BlockFenceGate(int par1, int par2)
    {
        super(par1, par2, Material.wood);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return !par1World.getBlockMaterial(par2, par3 - 1, par4).isSolid() ? false : super.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockMetadata(par2, par3, par4);
        return isFenceGateOpen(var5) ? null : (var5 != 2 && var5 != 0 ? AxisAlignedBB.getBoundingBoxFromPool((double)((float)par2 + 0.375F), (double)par3, (double)par4, (double)((float)par2 + 0.625F), (double)((float)par3 + 1.5F), (double)(par4 + 1)) : AxisAlignedBB.getBoundingBoxFromPool((double)par2, (double)par3, (double)((float)par4 + 0.375F), (double)(par2 + 1), (double)((float)par3 + 1.5F), (double)((float)par4 + 0.625F)));
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = func_48216_a(par1IBlockAccess.getBlockMetadata(par2, par3, par4));

        if (var5 != 2 && var5 != 0)
        {
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        }
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

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return isFenceGateOpen(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 21;
    }

    /**
     * Called when a block is placed by using an ItemStack from inventory and passed in who placed it. Args:
     * x,y,z,entityliving
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
    {
        int var6 = (MathHelper.floor_double((double)(par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, var6);
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);

        if (isFenceGateOpen(var6))
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 & -5);
        }
        else
        {
            int var7 = (MathHelper.floor_double((double)(par5EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
            int var8 = func_48216_a(var6);

            if (var8 == (var7 + 2) % 4)
            {
                var6 = var7;
            }

            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 | 4);
        }

        par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
        return true;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote)
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);
            boolean var7 = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);

            if (var7 || par5 > 0 && Block.blocksList[par5].canProvidePower() || par5 == 0)
            {
                if (var7 && !isFenceGateOpen(var6))
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 | 4);
                    par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
                }
                else if (!var7 && isFenceGateOpen(var6))
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 & -5);
                    par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
                }
            }
        }
    }

    /**
     * Returns if the fence gate is open according to its metadata.
     */
    public static boolean isFenceGateOpen(int par0)
    {
        return (par0 & 4) != 0;
    }
}
