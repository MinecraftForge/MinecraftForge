/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import org.jspecify.annotations.NullMarked;

/**
 * A simple marker event that notifies when the game is about to close.
 * Fires once on the physical client and physical server.
 * Does not fire for the Integrated Server on a physical Client.
 * <br>
 * On the client, the GL Context is still valid when the event is fired.
 * Fired on the singularity event bus.
 *
 * @author Curle
 */
@NullMarked
public record GameShuttingDownEvent() implements SelfDestructing, RecordEvent {
    public static final EventBus<GameShuttingDownEvent> BUS = EventBus.create(GameShuttingDownEvent.class);
}
