/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;

/**
 * This event is fired whenever an {@link Entity} joins a {@link Level}.
 * This event is fired whenever an entity is added to a level in {@link Level#addFreshEntity(Entity)}
 * and {@code PersistentEntitySectionManager#addNewEntity(Entity, boolean)}.
 * <p>
 * <strong>Note:</strong> This event may be called before the underlying {@link LevelChunk} is promoted to {@link ChunkStatus#FULL}.
 * You will cause chunk loading deadlocks if you do not delay your world interactions.
 * <p>
 * This event is {@linkplain Cancellable cancellable}.
 * If the event is cancelled, the entity will not be added to the level.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus}
 * on both logical sides.
 **/
public record EntityJoinLevelEvent(Entity getEntity, Level getLevel, boolean loadedFromDisk)
        implements Cancellable, EntityEvent, RecordEvent {
    public static final CancellableEventBus<EntityJoinLevelEvent> BUS = CancellableEventBus.create(EntityJoinLevelEvent.class);

    public EntityJoinLevelEvent(Entity entity, Level level) {
        this(entity, level, false);
    }

    /**
     * {@return the level that the entity is set to join}
     */
    public Level getLevel() {
        return getLevel;
    }

    /**
     * @return {@code true} if the entity was loaded from disk, {@code false} otherwise.
     * On the {@linkplain LogicalSide#CLIENT logical client}, this will always return {@code false}.
     */
    public boolean loadedFromDisk() {
        return loadedFromDisk;
    }
}
