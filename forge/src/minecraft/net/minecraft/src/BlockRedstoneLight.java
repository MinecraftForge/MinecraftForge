package net.minecraft.src;

public class BlockRedstoneLight extends Block
{
    private final boolean field_48215_a;

    public BlockRedstoneLight(int par1, boolean par2)
    {
        super(par1, 211, Material.field_48468_r);
        this.field_48215_a = par2;

        if (par2)
        {
            this.setLightValue(1.0F);
            ++this.blockIndexInTexture;
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        if (!par1World.isRemote)
        {
            if (this.field_48215_a && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
            {
                par1World.setBlockWithNotify(par2, par3, par4, Block.field_48209_bL.blockID);
            }
            else if (!this.field_48215_a && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
            {
                par1World.setBlockWithNotify(par2, par3, par4, Block.field_48210_bM.blockID);
            }
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
            if (this.field_48215_a && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
            {
                par1World.setBlockWithNotify(par2, par3, par4, Block.field_48209_bL.blockID);
            }
            else if (!this.field_48215_a && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
            {
                par1World.setBlockWithNotify(par2, par3, par4, Block.field_48210_bM.blockID);
            }
        }
    }
}
