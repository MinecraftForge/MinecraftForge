package net.minecraftforge.common;

import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for receiving the notification of landing from EntityFallingBlock.
 *
 *  Note: implementing this prevents classes extending BlockFalling from having BlockFalling#onEndFalling called.
 * */
public interface IFallableBlock {

    /**
     * Called when the block hits a solid surface when canBePlaced passes
     *  Note: This happens after the block is placed, and, if applicable, before the TileEntity data is set
     *
     * @param entity the instance of the falling block entity.
     * @param world the World instance
     * @param pos the BlockPos the final landing position of the entity
     */
    public void onEndFalling(EntityFallingBlock entity, World world, BlockPos pos);

}
