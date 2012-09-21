package net.minecraftforge.event.world;

import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

public class BlockEvent extends WorldEvent
{

    /**
     * The coordinates of the block
     */
    public final int x, y, z;

    public BlockEvent(World world, int x, int y, int z)
    {
        super(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Raised when a block or a block's metadata is being altered. If canceled
     * the block wont be altered
     */
    @Cancelable
    public static class BlockChange extends BlockEvent
    {

        public final int blockID;
        public final int metadata;

        /**
         * Cancelable BlockChange event contructor
         *
         * @param world    The world object where the block is being set
         * @param x        The x coordination on which the block is being set
         * @param y        The y coordination on which the block is being set
         * @param z        The z coordination on which the block is being set
         * @param blockID  The id of the new block (-1 if only the metadata was
         *                 changed)
         * @param metadata The new metadata for the block
         */
        public BlockChange(World world, int x, int y, int z, int blockID, int metadata)
        {
            super(world, x, y, z);
            this.blockID = blockID;
            this.metadata = metadata;
        }
    }
}
