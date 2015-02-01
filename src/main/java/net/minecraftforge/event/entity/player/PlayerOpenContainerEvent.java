package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

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
     * DEFAULT is vanilla behavior
     *
     */

    public PlayerOpenContainerEvent(EntityPlayer player, Container openContainer)
    {
        super(player);
        this.canInteractWith = openContainer.canInteractWith(player);
    }
}
