/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftsingularity.eventbus.api.bus.EventBus;

/**
 * Called after {@link ServerAboutToStartEvent} and before {@link ServerStartedEvent}.
 * This event allows for customizations of the server.
 *
 * If you need to add commands use {@link net.minecraftsingularity.event.RegisterCommandsEvent}.
 *
 * @author cpw
 */
public record ServerStartingEvent(MinecraftServer getServer) implements ServerLifecycleEvent {
    public static final EventBus<ServerStartingEvent> BUS = EventBus.create(ServerStartingEvent.class);
}
