package net.minecraftforge.event.entity.player;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

public class CanHarvestBlockEvent extends PlayerEvent
{
    public boolean result;
    public final Block block;
    
    /**
     * Fired while a player is mining a block,
     * Used to determine if anything drops when the block is mined.
     * 
     * Returns the value of result
     * 
     * @param player -EntityPlayer who is mining the block
     * @param block  -Block being mined
     * @param result -Boolean value from EntityPlayer.inventory.canHarvestBlock
     */
    public CanHarvestBlockEvent(EntityPlayer player, Block block, boolean result)
    {
        super(player);
        this.block = block;
        this.result = result;
    }
}
