/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * This event will fire when the player is opped or deopped.
 * <p>
 * This event is cancelable which will stop the op or deop from happening.
 * @param getNewLevel The new permission level.
 * @param getOldLevel The old permission level.
 */
@NullMarked
public record PermissionsChangedEvent(ServerPlayer getEntity, @Nullable LevelBasedPermissionSet getNewLevel, @Nullable LevelBasedPermissionSet getOldLevel)
        implements Cancellable, PlayerEvent, RecordEvent {
    public static final CancellableEventBus<PermissionsChangedEvent> BUS = CancellableEventBus.create(PermissionsChangedEvent.class);
}
