/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * This event gets fired whenever a entity mounts/dismounts another entity.<br>
 * <b>entityBeingMounted can be null</b>, be sure to check for that.
 * <br>
 * <br>
 * This event is {@linkplain Cancellable cancellable}.<br>
 * If this event is cancelled, the entity does not mount/dismount the other entity.
 */
public record EntityMountEvent(
        Entity getEntityMounting,
        Entity getEntityBeingMounted,
        Level getLevel,
        boolean isMounting
) implements Cancellable, EntityEvent, RecordEvent {
    public static final CancellableEventBus<EntityMountEvent> BUS = CancellableEventBus.create(EntityMountEvent.class);

    @Override
    public Entity getEntity() {
        return getEntityMounting;
    }

    public boolean isDismounting() {
        return !isMounting;
    }
}
