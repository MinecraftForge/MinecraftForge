
package net.minecraft.src.forge;

import net.minecraft.src.World;

public interface IBlockChangeHandler
{

    /**
     * Raised when a block or a block's metadata is being altered You can
     * prevent the block from changing by returning false
     *
     * @param w The world object where the block is being set
     * @param x The x coordination on which the block is being set
     * @param y The y coordination on which the block is being set
     * @param z The z coordination on which the block is being set
     * @param blockID The id of the new block (-1 if only the metadata was
     * changed)
     * @param metadata The new metadata for the block
     * @return True if processing should continue.
     */
    public boolean onBlockChange(World w, int x, int y, int z, int blockID, int metadata);
}
