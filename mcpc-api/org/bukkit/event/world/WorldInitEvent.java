package org.bukkit.event.world;

import org.bukkit.World;
import org.bukkit.event.HandlerList;

/**
 * Called when a World is initializing
 */
public class WorldInitEvent extends WorldEvent {
    private static final HandlerList handlers = new HandlerList();

    public WorldInitEvent(final World world) {
        super(world);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
