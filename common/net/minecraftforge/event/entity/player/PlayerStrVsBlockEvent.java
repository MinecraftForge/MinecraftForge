package net.minecraftforge.event.entity.player;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlayerStrVsBlockEvent extends PlayerEvent
{
    public float result = 0F;
    public final Block block;
    public final int meta;
    private boolean handeled = false;
    
    /**
     * Fired while a player is mining a block,
     * Used to determine mining speed.
     * 
     * Canceling the event stops the normal check from being done and returns the value of result.
     * result defaults to 0F (unable to mine)
     * 
     * @param player -EntityPlayer who is mining the block
     * @param block  -Block being mined
     * @param meta   -Metadata of the block being mined
     */
    public PlayerStrVsBlockEvent(EntityPlayer player, Block block, int meta)
    {
        super(player);
        this.block = block;
        this.meta = meta;
    }
    
    public void setHandeled()
    {
        handeled = true;
    }
    
    public boolean isHandeled()
    {
        return handeled;
    }
}
