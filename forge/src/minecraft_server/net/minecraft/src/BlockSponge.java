package net.minecraft.src;

public class BlockSponge extends Block
{
    protected BlockSponge(int par1)
    {
        super(par1, Material.sponge);
        this.blockIndexInTexture = 48;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {}

    /**
     * Called whenever the block is removed.
     */
    public void onBlockRemoval(World par1World, int par2, int par3, int par4) {}
}
