/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;

public sealed interface ServerLifecycleEvent extends InheritableEvent
        permits ServerAboutToStartEvent, ServerStartedEvent, ServerStartingEvent, ServerStoppedEvent, ServerStoppingEvent {
    EventBus<ServerLifecycleEvent> BUS = EventBus.create(ServerLifecycleEvent.class);

    MinecraftServer getServer();
}
