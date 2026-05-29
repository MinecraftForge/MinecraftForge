/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftsingularity.eventbus.api.bus.EventBus;

/**
 * Called after {@link ServerStoppingEvent} when the server has completely shut down.
 * Called immediately before shutting down, on the dedicated server, and before returning
 * to the main menu on the client.
 *
 * @author cpw
 */
public record ServerStoppedEvent(MinecraftServer getServer) implements ServerLifecycleEvent {
    public static final EventBus<ServerStoppedEvent> BUS = EventBus.create(ServerStoppedEvent.class);
}
