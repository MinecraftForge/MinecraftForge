package net.minecraftforge.event.entity.player;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

/**
 * This event represents an activation of a block.  Canceling the event aborts
 * Minecraft's native block activation.
 * 
 * @author MALfunction84
 */
@Cancelable
public class BlockActivateEvent extends PlayerEvent {

    /**
     * The world in which the activated block resides.
     */
    public final World world;

    /**
     * The currently selected item stack, in case the item interacts with the block.
     */
    public final ItemStack item;

    /**
     * The x coordinate of the activated block.
     */
    public final int x;

    /**
     * The y coordinate of the activated block.
     */
    public final int y;

    /**
     * The z coordinate of the activated block.
     */
    public final int z;

    /**
     * The side of the block that was activated.  Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5, -1 = no side detected by ray trace.
     */
    public final int side;

    /**
     * The x coordinate of the hit vector.
     */
    public final float xHitVector;

    /**
     * The y coordinate of the hit vector.
     */
    public final float yHitVector;

    /**
     * The z coordinate of the hit vector.
     */
    public final float zHitVector;

    /**
     * The activated block type.
     */
    public final Block block;

    /**
     * Creates a BlockActivateEvent.
     * 
     * @param entityPlayer the player activating the block
     * @param world the world in which the activated block resides
     * @param itemStack the currently selected item stack, in case the item interacts with the block
     * @param x the x coordinate of the activated block
     * @param y the y coordinate of the activated block
     * @param z the z coordinate of the activated block
     * @param side the side of the block that was activated.  Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5, -1 = no side detected by ray trace.
     * @param xHitVector the x coordinate of the hit vector
     * @param yHitVector the y coordinate of the hit vector
     * @param zHitVector the z coordinate of the hit vector
     */
    public BlockActivateEvent(EntityPlayer entityPlayer, World world, ItemStack itemStack, int x, int y, int z, int side, float xHitVector, float yHitVector, float zHitVector)
    {
        super(entityPlayer);
        this.world = world;
        this.item = itemStack;
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.xHitVector = xHitVector;
        this.yHitVector = yHitVector;
        this.zHitVector = zHitVector;
        int blockId = world.getBlockId(x, y, z);
        this.block = Block.blocksList[blockId];
    }

}
