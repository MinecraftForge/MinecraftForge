package net.minecraftforge.event.block;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.world.ChunkWatchEvent;

/**
 * Represents a block related event.
 */
public abstract class BlockEvent extends Event 
{
    /** Reference to the World object which can never be null. */
    public final World world;
    /** Reference to the Block object. */
    public final Block block;
    /** Block metadata */
    public final int meta;
    /** x coord where the block event took place. */
    public final int x;
    /** y coord where the block event took place. */
    public final int y;
    /** z coord where the block event took place. */
    public final int z;
    public BlockEvent(World world, int x, int y, int z, int meta, Block block) {
        super();
        this.world = world;
        this.block = block;
        this.meta = meta;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}