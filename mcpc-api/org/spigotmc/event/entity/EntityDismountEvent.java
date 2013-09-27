package org.spigotmc.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Called when an entity stops riding another entity.
 *
 */
public class EntityDismountEvent extends EntityEvent
{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Entity dismounted;

    public EntityDismountEvent(Entity what, Entity dismounted)
    {
        super( what );
        this.dismounted = dismounted;
    }

    public Entity getDismounted()
    {
        return dismounted;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
