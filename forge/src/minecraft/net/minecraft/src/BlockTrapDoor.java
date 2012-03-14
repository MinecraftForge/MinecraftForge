package net.minecraft.src;

public class BlockTrapDoor extends Block
{
    /** Set this to allow trapdoors to remain free-floating */
    public static boolean disableValidation = false;
    
    protected BlockTrapDoor(int par1, Material par2Material)
    {
        super(par1, par2Material);
        this.blockIndexInTexture = 84;

        if (par2Material == Material.iron)
        {
            ++this.blockIndexInTexture;
        }

        float var3 = 0.5F;
        float var4 = 1.0F;
        this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var4, 0.5F + var3);
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
        return !isTrapdoorOpen(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 0;
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        this.setBlockBoundsForBlockRender(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float var1 = 0.1875F;
        this.setBlockBounds(0.0F, 0.5F - var1 / 2.0F, 0.0F, 1.0F, 0.5F + var1 / 2.0F, 1.0F);
    }

    public void setBlockBoundsForBlockRender(int par1)
    {
        float var2 = 0.1875F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, var2, 1.0F);

        if (isTrapdoorOpen(par1))
        {
            if ((par1 & 3) == 0)
            {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            }

            if ((par1 & 3) == 1)
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            }

            if ((par1 & 3) == 2)
            {
                this.setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if ((par1 & 3) == 3)
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        this.blockActivated(par1World, par2, par3, par4, par5EntityPlayer);
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        if (this.blockMaterial == Material.iron)
        {
            return true;
        }
        else
        {
            int var6 = par1World.getBlockMetadata(par2, par3, par4);
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 ^ 4);
            par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
            return true;
        }
    }

    public void onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);
        boolean var7 = (var6 & 4) > 0;

        if (var7 != par5)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 ^ 4);
            par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
        }
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
            int var7 = par2;
            int var8 = par4;

            if ((var6 & 3) == 0)
            {
                var8 = par4 + 1;
            }

            if ((var6 & 3) == 1)
            {
                --var8;
            }

            if ((var6 & 3) == 2)
            {
                var7 = par2 + 1;
            }

            if ((var6 & 3) == 3)
            {
                --var7;
            }

            if (!(isValidSupportBlock(par1World.getBlockId(var7, par3, var8)) || par1World.isBlockSolidOnSide(var7, par3, var8, (var6 & 3) + 2)))
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);
                this.dropBlockAsItem(par1World, par2, par3, par4, var6, 0);
            }

            boolean var9 = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);

            if (var9 || par5 > 0 && Block.blocksList[par5].canProvidePower() || par5 == 0)
            {
                this.onPoweredBlockChange(par1World, par2, par3, par4, var9);
            }
        }
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3D par5Vec3D, Vec3D par6Vec3D)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3D, par6Vec3D);
    }

    /**
     * Called when a block is placed using an item. Used often for taking the facing and figuring out how to position
     * the item. Args: x, y, z, facing
     */
    public void onBlockPlaced(World par1World, int par2, int par3, int par4, int par5)
    {
        byte var6 = 0;

        if (par5 == 2)
        {
            var6 = 0;
        }

        if (par5 == 3)
        {
            var6 = 1;
        }

        if (par5 == 4)
        {
            var6 = 2;
        }

        if (par5 == 5)
        {
            var6 = 3;
        }

        par1World.setBlockMetadataWithNotify(par2, par3, par4, var6);
    }

    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5)
    {
        if (disableValidation)
        {
            return true;
        }
        if (par5 == 0)
        {
            return false;
        }
        else if (par5 == 1)
        {
            return false;
        }
        else
        {
            if (par5 == 2)
            {
                ++par4;
            }

            if (par5 == 3)
            {
                --par4;
            }

            if (par5 == 4)
            {
                ++par2;
            }

            if (par5 == 5)
            {
                --par2;
            }

            return isValidSupportBlock(par1World.getBlockId(par2, par3, par4)) || par1World.isBlockSolidOnSide(par2, par3, par4, 1);
        }
    }

    public static boolean isTrapdoorOpen(int par0)
    {
        return (par0 & 4) != 0;
    }

    /**
     * Checks if the block ID is a valid support block for the trap door to connect with. If it is not the trapdoor is
     * dropped into the world.
     */
    private static boolean isValidSupportBlock(int par0)
    {
        if (disableValidation)
        {
            return true;
        }
        
        if (par0 <= 0)
        {
            return false;
        }
        else
        {
            Block var1 = Block.blocksList[par0];
            return var1 != null && var1.blockMaterial.isOpaque() && var1.renderAsNormalBlock() || var1 == Block.glowStone;
        }
    }
}
