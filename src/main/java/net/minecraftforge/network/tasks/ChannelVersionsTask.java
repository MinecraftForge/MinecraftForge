/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.tasks;

import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftsingularity.network.NetworkInitialization;
import net.minecraftsingularity.network.config.ConfigurationTaskContext;
import net.minecraftsingularity.network.packets.ChannelVersions;

/**
 * Sends the list of known channels to the client as well as their specific versions.
 * Allows the client to do compatibility checking.
 */
@ApiStatus.Internal
public class ChannelVersionsTask implements ConfigurationTask {
    public static final Type TYPE = new Type("singularity:channel_list");

    @Override
    public void start(ConfigurationTaskContext ctx) {
        NetworkInitialization.CONFIG.send(new ChannelVersions(), ctx.getConnection());
    }

    @Override
    public void start(Consumer<Packet<?>> send) {
        throw new IllegalStateException("This should never be called");
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
