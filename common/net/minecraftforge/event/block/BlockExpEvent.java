package net.minecraftforge.event.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


/**
 * An event that's called when a block yields experience.
 */
public class BlockExpEvent extends BlockEvent {
    /** The experience amount dropped by block. */
    private int exp;

    public BlockExpEvent(World world, int x, int y, int z, int meta, int exp, Block block) {
        super(world, x, y, z, meta, block);
        this.exp = exp;
    }

    /**
     * Get the experience dropped by the block after the event has processed
     *
     * @return The experience to drop
     */
    public int getExpToDrop() {
        return exp;
    }

    /**
     * Set the amount of experience dropped by the block after the event has processed
     *
     * @param exp 1 or higher to drop experience, else nothing will drop
     */
    public void setExpToDrop(int exp) {
        this.exp = exp;
    }
}