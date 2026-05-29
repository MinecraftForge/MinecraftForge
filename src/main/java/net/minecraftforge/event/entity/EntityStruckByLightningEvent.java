/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraftsingularity.event.singularityEventFactory;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * EntityStruckByLightningEvent is fired when an Entity is about to be struck by lightning.<br>
 * This event is fired whenever an EntityLightningBolt is updated to strike an Entity in
 * {@link LightningBolt#tick()} via {@link singularityEventFactory#onEntityStruckByLightning(Entity, LightningBolt)}.<br>
 * <br>
 * This event is {@linkplain Cancellable cancellable}.<br>
 * If this event is cancelled, the Entity is not struck by the lightning.<br>
 *
 * @param getLightning the instance of EntityLightningBolt attempting to strike an entity.
 */
public record EntityStruckByLightningEvent(Entity getEntity, LightningBolt getLightning)
        implements Cancellable, EntityEvent, RecordEvent {
    public static final CancellableEventBus<EntityStruckByLightningEvent> BUS = CancellableEventBus.create(EntityStruckByLightningEvent.class);
}
