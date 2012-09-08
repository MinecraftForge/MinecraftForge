package net.minecraftforge.event.entity.player;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

/**
 * Called when a player breaks a block (ItemInWorldManager.tryHarvestBlock).
 * blockX, blockY, blockZ are the coordinates of the broken block.
 * If the event is cancelled the block won't be broken.
 */
@Cancelable
public class BlockBreakEvent extends PlayerEvent 
{
        public final int blockX;
        public final int blockY;
        public final int blockZ;
        
        public BlockBreakEvent(EntityPlayer player, int xCoord, int yCoord, int zCoord) 
        {
                super(player);
                this.blockX = xCoord;
                this.blockY = yCoord;
                this.blockZ = zCoord;
        }
}