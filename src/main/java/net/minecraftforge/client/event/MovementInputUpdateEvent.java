/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.event.entity.player.PlayerEvent;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired after the local player's movement inputs are updated during a tick.
 * May be used to modify the provided {@linkplain net.minecraft.client.player.ClientInput#moveVector} state for some movement overrides.
 *
 *  <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param getEntity the local player
 * @param getInput the player's movement inputs
 */
public record MovementInputUpdateEvent(Player getEntity, ClientInput getInput) implements RecordEvent, PlayerEvent {
    public static final EventBus<MovementInputUpdateEvent> BUS = EventBus.create(MovementInputUpdateEvent.class);

    @ApiStatus.Internal
    public MovementInputUpdateEvent {}
}
