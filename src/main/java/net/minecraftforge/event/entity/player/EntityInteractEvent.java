package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * EntityInteractEvent is fired when a player interacts with an Entity.<br>
 * This event is fired whenever a player interacts with an Entity in
 * EntityPlayer#interactWith(Entity).<br>
 * <br>
 * {@link #target} contains the Entity the player interacted with. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not interact with the Entity.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class EntityInteractEvent extends PlayerEvent
{
    public final Entity target;
    public EntityInteractEvent(EntityPlayer player, Entity target)
    {
        super(player);
        this.target = target;
    }
}
