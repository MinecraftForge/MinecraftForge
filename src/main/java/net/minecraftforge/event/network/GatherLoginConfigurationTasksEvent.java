/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.network;

import java.util.function.Consumer;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Gathers tasks that need to be run during the initial login configuration.
 * @see net.minecraft.server.network.ServerConfigurationPacketListenerImpl#startConfiguration() startConfiguration
 */
@NullMarked
public record GatherLoginConfigurationTasksEvent(Connection getConnection, Consumer<ConfigurationTask> taskAdder) implements RecordEvent {
    public static final EventBus<GatherLoginConfigurationTasksEvent> BUS = EventBus.create(GatherLoginConfigurationTasksEvent.class);

    public void addTask(ConfigurationTask task) {
        taskAdder.accept(task);
    }
}
