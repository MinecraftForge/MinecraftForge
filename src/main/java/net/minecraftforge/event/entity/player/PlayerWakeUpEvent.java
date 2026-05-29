/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NullMarked;

/**
 * This event is fired when the player is waking up.<br/>
 * This is merely for purposes of listening for this to happen.<br/>
 * There is nothing that can be manipulated with this event.
 */
@NullMarked
public record PlayerWakeUpEvent(Player getEntity, boolean wakeImmediately, boolean updateLevel)
        implements RecordEvent, PlayerEvent {
    public static final EventBus<PlayerWakeUpEvent> BUS = EventBus.create(PlayerWakeUpEvent.class);

    /**
     * Used for the 'wake up animation'.
     * This is false if the player is considered 'sleepy' and the overlay should slowly fade away.
     */
    public boolean wakeImmediately() { return wakeImmediately; }

    /**
     * Indicates if the server should be notified of sleeping changes.
     * This will only be false if the server is considered 'up to date' already, because, for example, it initiated the call.
     */
    public boolean updateLevel() { return updateLevel; }
}
