package org.bukkit.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Item;

/**
 * Called when an item is spawned into a world
 */
public class ItemSpawnEvent extends EntitySpawnEvent {
    public ItemSpawnEvent(final Item spawnee) {
        super(spawnee);
    }

    @Deprecated
    public ItemSpawnEvent(final Item spawnee, final Location loc) {
        this(spawnee);
    }

    @Override
    public Item getEntity() {
        return (Item) entity;
    }
}
