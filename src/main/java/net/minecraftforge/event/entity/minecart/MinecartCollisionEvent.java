package net.minecraftforge.event.entity.minecart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;

/**
 * MinecartCollisionEvent is fired when a minecart collides with an Entity.
 * This event is fired whenever a minecraft collides in
 * EntityMinecart#applyEntityCollision(Entity).
 * 
 * {@link #collider} contains the Entity the Minecart collided with.
 * 
 * This event is not {@link Cancelable}.
 * 
 * This event does not have a result. {@link HasResult}
 * 
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class MinecartCollisionEvent extends MinecartEvent
{
    public final Entity collider;

    public MinecartCollisionEvent(EntityMinecart minecart, Entity collider)
    {
        super(minecart);
        this.collider = collider;
    }
}
