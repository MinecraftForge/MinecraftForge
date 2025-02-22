/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MarkerEvent;

@MarkerEvent
public sealed interface ServerLifecycleEvent extends InheritableEvent
        permits ServerAboutToStartEvent, ServerStartingEvent, ServerStartedEvent, ServerStoppingEvent, ServerStoppedEvent {
    MinecraftServer server();
}
