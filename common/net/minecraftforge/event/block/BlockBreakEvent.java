package net.minecraftforge.event.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.BlockSnapshot;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * Called when a block is broken by a player.
 * <p>
 * If you wish to have the block drop experience, you must set the experience value above 0.
 * By default, experience will be set in the event if:
 * <ol>
 * <li />The player is not in creative or adventure mode
 * <li />The player can loot the block (ie: does not destroy it completely, by using the correct tool)
 * <li />The player does not have silk touch
 * <li />The block drops experience in vanilla MineCraft
 * </ol>
 * <p>
 * Note:
 * Mods wanting to simulate a traditional block drop should set the block to air and utilize their own methods for determining
 *   what the default drop for the block being broken is and what to do about it, if anything.
 * <p>
 * If a Block Break event is cancelled, the block will not break and experience will not drop.
 */
@Cancelable
public class BlockBreakEvent extends BlockExpEvent 
{
    /** Reference to the Player who broke the block. If no player is available, use a FakePlayer */
    public final EntityPlayer player;
    /** A snapshot of block taken before being broken. */
    public final BlockSnapshot blocksnapshot;
    public BlockBreakEvent(World world, int x, int y, int z, int meta, BlockSnapshot blocksnapshot, EntityPlayer player) {
        super(world, x, y, z, meta, 0, blocksnapshot.getBlock());
        this.player = player;
        this.blocksnapshot = blocksnapshot;
    }
}