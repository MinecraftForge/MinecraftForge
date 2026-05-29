/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when a player's spawn point is set or reset.
 * <p>
 * The event can be canceled, which will prevent the spawn point from being changed.
 */
public final class PlayerSetSpawnEvent extends MutableEvent implements Cancellable, PlayerEvent {
    public static final CancellableEventBus<PlayerSetSpawnEvent> BUS = CancellableEventBus.create(PlayerSetSpawnEvent.class);

    private final Player player;
    private final @Nullable ServerPlayer.RespawnConfig config;

    public PlayerSetSpawnEvent(ServerPlayer player, @Nullable ServerPlayer.RespawnConfig config) {
        this.player = player;
        this.config = config;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    /** @return The config for the player respawn */
    public @Nullable ServerPlayer.RespawnConfig getConfig() {
        return this.config;
    }
}
