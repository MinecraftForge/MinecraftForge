/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Occurs when a player falls, but is able to fly.  Doesn't need to be cancelable, this is mainly for notification purposes.
 */
@NullMarked
public final class PlayerFlyableFallEvent extends MutableEvent implements PlayerEvent {
    public static final EventBus<PlayerFlyableFallEvent> BUS = EventBus.create(PlayerFlyableFallEvent.class);

    private final Player player;
    private double distance;
    private float multiplier;

    public PlayerFlyableFallEvent(Player player, double distance, float multiplier) {
        this.player = player;
        this.distance = distance;
        this.multiplier = multiplier;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public double getDistance() { return distance;}
    public void setDistance(float distance) { this.distance = distance; }
    public float getMultiplier() { return multiplier; }
    public void setMultiplier(float multiplier) { this.multiplier = multiplier; }
}
