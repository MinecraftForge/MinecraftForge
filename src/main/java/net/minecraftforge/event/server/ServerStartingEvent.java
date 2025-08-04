/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.bus.EventBus;

/**
 * Called after {@link ServerAboutToStartEvent} and before {@link ServerStartedEvent}.
 * This event allows for customizations of the server.
 *
 * If you need to add commands use {@link net.minecraftforge.event.RegisterCommandsEvent}.
 *
 * @author cpw
 */
public final class ServerStartingEvent extends ServerLifecycleEvent {
    public static final EventBus<ServerStartingEvent> BUS = EventBus.create(ServerStartingEvent.class);

    public ServerStartingEvent(final MinecraftServer server) {
        super(server);
    }
}
