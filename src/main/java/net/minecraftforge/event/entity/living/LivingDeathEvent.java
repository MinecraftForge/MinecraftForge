/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.event.singularityEventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * LivingDeathEvent is fired when an Entity dies. <br>
 * This event is fired whenever an Entity dies in
 * {@link LivingEntity#die(DamageSource)},
 * {@link Player#die(DamageSource)}, and
 * {@link ServerPlayer#die(DamageSource)}. <br>
 * <br>
 * This event is fired via the {@link singularityEventFactory#onLivingDeath(LivingEntity, DamageSource)}.<br>
 * <br>
 * This event is {@linkplain Cancellable cancellable}. If this event is cancelled, the Entity does not die.
 *
 * @param getSource the source of the damage that caused the entity to die
 */
public record LivingDeathEvent(LivingEntity getEntity, DamageSource getSource)
        implements Cancellable, LivingEvent, RecordEvent {
    public static final CancellableEventBus<LivingDeathEvent> BUS = CancellableEventBus.create(LivingDeathEvent.class);
}
