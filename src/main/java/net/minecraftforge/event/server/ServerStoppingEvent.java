/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftsingularity.eventbus.api.bus.EventBus;

/**
 * Called when the server begins an orderly shutdown, before {@link ServerStoppedEvent}.
 *
 * @author cpw
 */
public record ServerStoppingEvent(MinecraftServer getServer) implements ServerLifecycleEvent {
    public static final EventBus<ServerStoppingEvent> BUS = EventBus.create(ServerStoppingEvent.class);
}
