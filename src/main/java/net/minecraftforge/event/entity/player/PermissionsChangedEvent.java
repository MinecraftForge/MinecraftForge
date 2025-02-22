/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * This event will fire when the player is opped or deopped.
 * <p>
 * This event is cancelable which will stop the op or deop from happening.
 *
 * @param newLevel The new permission level of the player
 * @param oldLevel The old permission level of the player
 */
public record PermissionsChangedEvent(Player entity, int newLevel, int oldLevel) implements Cancellable, PlayerEvent {
    public static final CancellableEventBus<PermissionsChangedEvent> BUS = CancellableEventBus.create(PermissionsChangedEvent.class);
}
