/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.config;

import java.util.function.Consumer;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;

/**
 * A simple ConfigurationTask that will run a block of code and then mark itself as finished.
 */
public class SimpleConfigurationTask implements ConfigurationTask {
    private final Type type;
    private final Consumer<ConfigurationTaskContext> task;

    public SimpleConfigurationTask(Type type, Consumer<ConfigurationTaskContext> task) {
        this.type = type;
        this.task = task;
    }

    public SimpleConfigurationTask(Type type, Runnable task) {
        this(type, c -> task.run());
    }

    @Override
    public void start(ConfigurationTaskContext ctx) {
        this.task.accept(ctx);
        ctx.finish(type());
    }

    @Override
    public void start(Consumer<Packet<?>> send) {
        throw new IllegalStateException("This should never be called");
    }

    @Override
    public Type type() {
        return this.type;
    }
}
