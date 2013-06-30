package net.minecraftforge.event.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.BlockSnapshot;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * Called when a block is placed by a player.
 *
 * If a Block Place event is cancelled, the block will not be placed.
 */
@Cancelable
public class BlockPlaceEvent extends BlockEvent {

    public final EntityPlayer player;
    public final ItemStack itemstack;
    public final int side;
    public final int clickedX;
    public final int clickedY;
    public final int clickedZ;
    public final int meta;
    public final BlockSnapshot blocksnapshot;

    public BlockPlaceEvent(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, int meta, int clickedX, int clickedY, int clickedZ, BlockSnapshot blocksnapshot) {
        super(world, x, y, z, meta, blocksnapshot.getBlock());
        this.side = side;
        this.clickedX = clickedX;
        this.clickedY = clickedY;
        this.clickedZ = clickedZ;
        this.meta = meta;
        this.player = player;
        this.itemstack = stack;
        this.blocksnapshot = blocksnapshot;
    }
}