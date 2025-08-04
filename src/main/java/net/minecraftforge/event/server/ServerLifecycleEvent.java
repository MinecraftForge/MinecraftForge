/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;

public sealed class ServerLifecycleEvent extends MutableEvent implements InheritableEvent
        permits ServerAboutToStartEvent, ServerStartedEvent, ServerStartingEvent, ServerStoppedEvent, ServerStoppingEvent {
    public static final EventBus<ServerLifecycleEvent> BUS = EventBus.create(ServerLifecycleEvent.class);

    protected final MinecraftServer server;

    public ServerLifecycleEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }
}
