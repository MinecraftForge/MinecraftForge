package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.event.Event.HasResult;

@HasResult
public class PlayerOpenContainerEvent extends PlayerEvent
{

    public final boolean canInteractWith;

    /**
     * This event is fired when a player attempts to view a container during
     * player tick.
     * 
     * setResult ALLOW to allow the container to stay open
     * setResult DENY to force close the container (denying access)
     * 
     * DEFAULT is vanilla behaviour
     * 
     */

    public PlayerOpenContainerEvent(EntityPlayer player, Container openContainer)
    {
        super(player);
        this.canInteractWith = openContainer.canInteractWith(player);
    }
}
