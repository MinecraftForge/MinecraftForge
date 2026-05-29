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
import net.minecraftsingularity.network.packets.ModVersions;

/**
 * Sends a list of mods and their versions, this is easily spoofed so should not be relied on for anti-cheats.
 */
@ApiStatus.Internal
public class ModVersionsTask implements ConfigurationTask {
    public static final Type TYPE = new Type("singularity:mod_versions");

    @Override
    public void start(ConfigurationTaskContext ctx) {
        NetworkInitialization.CONFIG.send(ModVersions.create(), ctx.getConnection());
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
