package net.minecraft.src;

public class BlockLeavesBase extends Block
{
    /**
     * Used to determine how to display leaves based on the graphics level. May also be used in rendering for
     * transparency, not sure.
     */
    protected boolean graphicsLevel;

    protected BlockLeavesBase(int par1, int par2, Material par3Material, boolean par4)
    {
        super(par1, par2, par3Material);
        this.graphicsLevel = par4;
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
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
        return !this.graphicsLevel && var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
}
