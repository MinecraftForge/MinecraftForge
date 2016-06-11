package net.minecraftforge.fmp.multipart;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Interface used to allow conversion of normal blocks into multiparts.
 *
 * @see IMultipart
 * @see MultipartRegistry
 */
public interface IPartConverter
{
    
    /**
     * Gets the blocks that can be converted by this class.
     */
    public Collection<Block> getConvertableBlocks();

    /**
     * Converts a block into a collection of multiparts. Simulated determines whether we're getting the parts to perform
     * checks or to actually convert the block.
     */
    public Collection<? extends IMultipart> convertBlock(IBlockAccess world, BlockPos pos, boolean simulated);

}
