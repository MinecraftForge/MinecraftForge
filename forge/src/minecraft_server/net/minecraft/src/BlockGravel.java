package net.minecraft.src;

import java.util.Random;

public class BlockGravel extends BlockSand
{
    public BlockGravel(int par1, int par2)
    {
        super(par1, par2);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return par2Random.nextInt(10 - par3 * 3) == 0 ? Item.flint.shiftedIndex : this.blockID;
    }
}
