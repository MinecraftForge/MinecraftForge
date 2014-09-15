package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;

/**
 * This event is fired when the player is waking up.<br/>
 * This is merely for purposes of listening for this to happen.<br/>
 * There is nothing that can be manipulated with this event.
 */
public class PlayerWakeUpEvent extends PlayerEvent
{
    public PlayerWakeUpEvent(EntityPlayer player)
    {
        super(player);
    }
}
