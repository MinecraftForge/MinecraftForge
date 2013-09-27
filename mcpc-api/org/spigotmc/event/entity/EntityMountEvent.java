package org.spigotmc.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Called when an entity attempts to ride another entity.
 *
 */
public class EntityMountEvent extends EntityEvent implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Entity mount;

    public EntityMountEvent(Entity what, Entity mount)
    {
        super( what );
        this.mount = mount;
    }

    public Entity getMount()
    {
        return mount;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancelled = cancel;
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
