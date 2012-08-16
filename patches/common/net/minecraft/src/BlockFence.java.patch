package net.minecraft.src;

public class BlockFence extends Block
{
    public BlockFence(int par1, int par2)
    {
        super(par1, par2, Material.wood);
        this.setCreativeTab(CreativeTabs.tabDeco);
    }

    public BlockFence(int par1, int par2, Material par3Material)
    {
        super(par1, par2, par3Material);
        this.setCreativeTab(CreativeTabs.tabDeco);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        boolean var5 = this.canConnectFenceTo(par1World, par2, par3, par4 - 1);
        boolean var6 = this.canConnectFenceTo(par1World, par2, par3, par4 + 1);
        boolean var7 = this.canConnectFenceTo(par1World, par2 - 1, par3, par4);
        boolean var8 = this.canConnectFenceTo(par1World, par2 + 1, par3, par4);
        float var9 = 0.375F;
        float var10 = 0.625F;
        float var11 = 0.375F;
        float var12 = 0.625F;

        if (var5)
        {
            var11 = 0.0F;
        }

        if (var6)
        {
            var12 = 1.0F;
        }

        if (var7)
        {
            var9 = 0.0F;
        }

        if (var8)
        {
            var10 = 1.0F;
        }

        return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)((float)par2 + var9), (double)par3, (double)((float)par4 + var11), (double)((float)par2 + var10), (double)((float)par3 + 1.5F), (double)((float)par4 + var12));
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        boolean var5 = this.canConnectFenceTo(par1IBlockAccess, par2, par3, par4 - 1);
        boolean var6 = this.canConnectFenceTo(par1IBlockAccess, par2, par3, par4 + 1);
        boolean var7 = this.canConnectFenceTo(par1IBlockAccess, par2 - 1, par3, par4);
        boolean var8 = this.canConnectFenceTo(par1IBlockAccess, par2 + 1, par3, par4);
        float var9 = 0.375F;
        float var10 = 0.625F;
        float var11 = 0.375F;
        float var12 = 0.625F;

        if (var5)
        {
            var11 = 0.0F;
        }

        if (var6)
        {
            var12 = 1.0F;
        }

        if (var7)
        {
            var9 = 0.0F;
        }

        if (var8)
        {
            var10 = 1.0F;
        }

        this.setBlockBounds(var9, 0.0F, var11, var10, 1.0F, var12);
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
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 11;
    }

    /**
     * Returns true if the specified block can be connected by a fence
     */
    public boolean canConnectFenceTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = par1IBlockAccess.getBlockId(par2, par3, par4);

        if (var5 != this.blockID && var5 != Block.fenceGate.blockID)
        {
            Block var6 = Block.blocksList[var5];
            return var6 != null && var6.blockMaterial.isOpaque() && var6.renderAsNormalBlock() ? var6.blockMaterial != Material.pumpkin : false;
        }
        else
        {
            return true;
        }
    }

    public static boolean isIdAFence(int par0)
    {
        return par0 == Block.fence.blockID || par0 == Block.netherFence.blockID;
    }
}
