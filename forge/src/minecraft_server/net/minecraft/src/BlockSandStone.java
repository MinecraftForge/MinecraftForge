package net.minecraft.src;

public class BlockSandStone extends Block
{
    public BlockSandStone(int par1)
    {
        super(par1, 192, Material.rock);
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return par1 == 1 ? this.blockIndexInTexture - 16 : (par1 == 0 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture);
    }
}
