/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/**
 * AttackEntityEvent is fired when a player attacks an Entity.<br>
 * This event is fired whenever a player attacks an Entity in
 * {@link Player#attack(Entity)}.<br>
 * <br>
 * {@link #getTarget()} contains the Entity that was damaged by the player. <br>
 * <br>
 * This event is {@linkplain Cancellable cancellable}. If this event is cancelled, the player does not attack the Entity.
 **/
public record AttackEntityEvent(Player getEntity, Entity getTarget) implements Cancellable, PlayerEvent, RecordEvent {
    public static final CancellableEventBus<AttackEntityEvent> BUS = CancellableEventBus.create(AttackEntityEvent.class);
}
