/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.tasks;

import java.io.IOException;
import java.nio.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.network.chat.Component;
import net.minecraftsingularity.fml.config.ConfigTracker;
import net.minecraftsingularity.fml.config.ModConfig;
import net.minecraftsingularity.network.NetworkInitialization;
import net.minecraftsingularity.network.config.ConfigurationTaskContext;
import net.minecraftsingularity.network.config.SimpleConfigurationTask;
import net.minecraftsingularity.network.packets.ConfigData;

@ApiStatus.Internal
class SyncConfigTask extends SimpleConfigurationTask {
    static final Type TYPE = new Type("singularity:sync_configs");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("singularity_SYNC_CONFIG");

    SyncConfigTask() {
        super(TYPE, SyncConfigTask::run);
    }

    private static void run(ConfigurationTaskContext ctx) {
        for (var cfg : ConfigTracker.configSets().get(ModConfig.Type.SERVER)) {
            try {
                var pkt = new ConfigData(cfg.getFileName(), Files.readAllBytes(cfg.getFullPath()));
                NetworkInitialization.CONFIG.send(pkt, ctx.getConnection());
            } catch (IOException e) {
                LOGGER.error(MARKER, "Failed to read config file {} terminating connection", cfg.getFileName(), e);
                ctx.getConnection().disconnect(Component.literal("Connection closed - Failed to read config on server"));
            }
        }
    }
}
