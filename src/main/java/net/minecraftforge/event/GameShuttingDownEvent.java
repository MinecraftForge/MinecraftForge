/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraftforge.eventbus.api.Event;

/**
 * A simple marker event that notifies when the game is about to close.
 * Fires once on the physical client and physical server.
 * Does not fire for the Integrated Server on a physical Client.
 *
 * On the client, the GL Context is still valid when the event is fired.
 * Fired on the FORGE event bus.
 *
 * @author Curle
 */
public class GameShuttingDownEvent extends Event
{
    public GameShuttingDownEvent() {}
}
