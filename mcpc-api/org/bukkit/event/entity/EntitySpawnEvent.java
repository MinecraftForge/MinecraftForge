package org.bukkit.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

/**
 * Called when an entity is spawned into a world.
 * <p>
 * If an Entity Spawn event is cancelled, the entity will not spawn.
 */
public class EntitySpawnEvent extends EntityEvent implements org.bukkit.event.Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean canceled;

    public EntitySpawnEvent(final Entity spawnee) {
        super(spawnee);
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    /**
     * Gets the location at which the entity is spawning.
     *
     * @return The location at which the entity is spawning
     */
    public Location getLocation() {
        return getEntity().getLocation();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
