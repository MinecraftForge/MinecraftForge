package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This event is called when a player collides with a EntityXPOrb on the ground.
 * The event can be canceled, and no further processing will be done.  
 */
@Cancelable
public class PlayerPickupXpEvent extends PlayerEvent
{
    public final EntityXPOrb orb;

    public PlayerPickupXpEvent(EntityPlayer player, EntityXPOrb orb)
    {
        super(player);
        this.orb = orb;
    }
}
