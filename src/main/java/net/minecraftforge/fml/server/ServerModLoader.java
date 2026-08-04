/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.server;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.network.FMLStatusPing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class ServerModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static DedicatedServer server;
    private static boolean hasErrors = false;

    public static void begin(DedicatedServer dedicatedServer) {
        ServerModLoader.server = dedicatedServer;
        SidedProvider.setServer(()->dedicatedServer);
        LogicalSidedProvider.setServer(()->dedicatedServer);
        LanguageHook.loadForgeAndMCLangs();
        try {
            ModLoader.get().gatherAndInitializeMods(null);
            ModLoader.get().loadMods(Runnable::run, (a)->{}, (a)->{});
        } catch (LoadingFailedException e) {
            ServerModLoader.hasErrors = true;
            throw e;
        }
    }


    public static void end() {
        try {
            ModLoader.get().finishMods(Runnable::run);
        } catch (LoadingFailedException e) {
            ServerModLoader.hasErrors = true;
            throw e;

        }
        List<ModLoadingWarning> warnings = ModLoader.get().getWarnings();
        if (!warnings.isEmpty()) {
            LOGGER.warn(LOADING, "Mods loaded with {} warnings", warnings.size());
            warnings.forEach(warning -> LOGGER.warn(LOADING, warning.formatToString()));
        }
        MinecraftForge.EVENT_BUS.start();
        server.getServerStatusResponse().setForgeData(new FMLStatusPing()); //gathers NetworkRegistry data
    }

    public static boolean hasErrors() {
        return ServerModLoader.hasErrors;
    }
}
