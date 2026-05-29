/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.fml.event.lifecycle.InterModProcessEvent;

/**
 * Called before the server begins loading anything. Called after {@link InterModProcessEvent} on the dedicated
 * server, and after the player has hit "Play Selected World" in the client. Called before {@link ServerStartingEvent}.
 * <br>
 * You can obtain a reference to the server with this event.
 * @author cpw
 */
public record ServerAboutToStartEvent(MinecraftServer getServer) implements ServerLifecycleEvent {
    public static final EventBus<ServerAboutToStartEvent> BUS = EventBus.create(ServerAboutToStartEvent.class);
}
