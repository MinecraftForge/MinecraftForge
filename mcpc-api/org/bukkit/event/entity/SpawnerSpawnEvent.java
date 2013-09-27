package org.bukkit.event.entity;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;

/**
 * Called when an entity is spawned into a world by a spawner.
 * <p>
 * If a Spawner Spawn event is cancelled, the entity will not spawn.
 */
public class SpawnerSpawnEvent extends EntitySpawnEvent {
    private final CreatureSpawner spawner;

    public SpawnerSpawnEvent(final Entity spawnee, final CreatureSpawner spawner) {
        super(spawnee);
        this.spawner = spawner;
    }

    public CreatureSpawner getSpawner() {
        return spawner;
    }
}
