/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.core.BlockPos;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;

import java.util.Optional;

/**
 * PlayerSleepInBedEvent is fired when a player sleeps in a bed.
 * <br>
 * This event is fired whenever a player sleeps in a bed in
 * {@link Player#startSleeping(BlockPos)}.<br>
 * <br>
 * {@link #result} contains whether the player is able to sleep. <br>
 */
public final class PlayerSleepInBedEvent extends MutableEvent implements PlayerEvent {
    public static final EventBus<PlayerSleepInBedEvent> BUS = EventBus.create(PlayerSleepInBedEvent.class);

    private final Player player;
    private BedSleepingProblem result = null;
    private final Optional<BlockPos> pos;

    public PlayerSleepInBedEvent(Player player, Optional<BlockPos> pos) {
        this.player = player;
        this.pos = pos;
    }

    @Override
    public Player getEntity() {
        return this.player;
    }

    public BedSleepingProblem getResultStatus() {
        return result;
    }

    public void setResult(BedSleepingProblem result) {
        this.result = result;
    }

    public BlockPos getPos() {
        return pos.orElse(null);
    }

    public Optional<BlockPos> getOptionalPos() {
        return pos;
    }
}
