/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.tasks;

import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.network.ChannelListManager;
import net.minecraftforge.network.config.ConfigurationTaskContext;

/**
 * Sends the list of known channels to the client using the
 * <a href="https://web.archive.org/web/20220711204310/https://dinnerbone.com/blog/2012/01/13/minecraft-plugin-channels-messaging/">Plugin Channel</a>
 * register messages.
 */
@ApiStatus.Internal
class RegisterChannelsTask implements ConfigurationTask {
    private static final Type TYPE = new Type("forge:send_vanilla_channel_list");

    @Override
    public void start(ConfigurationTaskContext ctx) {
        ChannelListManager.addChannels(ctx.getConnection());
        ctx.finish(type());
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
