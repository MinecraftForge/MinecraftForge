/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.network;

import java.util.function.Consumer;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.eventbus.api.Event;

/**
 * Gathers tasks that need to be run during the initial login configuration.
 * @see net.minecraft.server.network.ServerConfigurationPacketListenerImpl#startConfiguration() startConfiguration
 */
public class GatherLoginConfigurationTasksEvent extends Event {
    private final Connection connection;
    private final Consumer<ConfigurationTask> add;

    public GatherLoginConfigurationTasksEvent(Connection connection, Consumer<ConfigurationTask> add) {
        this.connection = connection;
        this.add = add;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void addTask(ConfigurationTask task) {
        this.add.accept(task);
    }
}
