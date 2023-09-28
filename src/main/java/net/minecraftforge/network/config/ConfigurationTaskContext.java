/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.config;

import java.util.function.Consumer;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask.Type;

public class ConfigurationTaskContext {
    private final Connection connection;
    private final Consumer<Packet<?>> send;
    private final Consumer<Type> finish;

    public ConfigurationTaskContext(Connection connection, Consumer<Packet<?>> send, Consumer<Type> finish) {
        this.connection = connection;
        this.send = send;
        this.finish = finish;
    }

    public Connection getConnection() {
        return connection;
    }

    public void send(Packet<?> packet) {
        this.send.accept(packet);
    }

    public void finish(Type task) {
        this.finish.accept(task);
    }
}
