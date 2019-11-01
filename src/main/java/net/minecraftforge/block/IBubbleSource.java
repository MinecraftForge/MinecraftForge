package net.minecraftforge.block;

import net.minecraft.block.BubbleColumnBlock;

/**
 * Implement this interface on blocks, which should be able to maintain a {@link BubbleColumnBlock} above it.
 * Don't forget to override tick, tickRate and onBlockAdded,
 * if you want the block to create a bubble column block like vanilla Soul Sand or Magma Block.
 */
public interface IBubbleSource {
    /**
     * This should return TRUE, if block is maintaining a bubble column block with downward drag. (like Magma Block)
     * And it should return FALSE, if it is maintaining a bubble column block with upward pushing. (like Soul Sand)
     */
    boolean isDragging();
}
