package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * AttackEntityEvent is fired when a player attacks an Entity.<br>
 * This event is fired whenever a player attacks an Entity in
 * EntityPlayer#attackTargetEntityWithCurrentItem(Entity).<br>
 * <br>
 * {@link #target} contains the Entity that was damaged by the player. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not attack the Entity.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class AttackEntityEvent extends PlayerEvent
{
    public final Entity target;
    public AttackEntityEvent(EntityPlayer player, Entity target)
    {
        super(player);
        this.target = target;
    }
}
