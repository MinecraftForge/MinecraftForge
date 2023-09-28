/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.tasks;

import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.network.NetworkInitialization;
import net.minecraftforge.network.config.ConfigurationTaskContext;
import net.minecraftforge.network.packets.ChannelVersions;

/**
 * Sends the list of known channels to the client as well as their specific versions.
 * Allows the client to do compatibility checking.
 */
@ApiStatus.Internal
public class ChannelVersionsTask implements ConfigurationTask {
    public static final Type TYPE = new Type("forge:channel_list");

    @Override
    public void start(ConfigurationTaskContext ctx) {
        NetworkInitialization.PLAY.send(new ChannelVersions(), ctx.getConnection());
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
