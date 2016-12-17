package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This event is fired when the current item of a player is switched for another slot.<br>
 * slotIndex represents the future slot index.
 * 
 * @author SCAREX
 */
public class PlayerChangeCurrentItemEvent extends PlayerEvent
{
    public final int slotIndex;
    
    public PlayerChangeCurrentItemEvent(EntityPlayer player, int slotIndex)
    {
        super(player);
        this.slotIndex = slotIndex;
    }
}
