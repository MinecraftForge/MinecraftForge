/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.server.loading;

import net.minecraftsingularity.fml.*;
import net.minecraftsingularity.logging.CrashReportExtender;
import net.minecraftsingularity.common.util.LogicalSidedProvider;
import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.server.LanguageHook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public final class ServerModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean hasErrors = false;

    public static void load() {
        LogicalSidedProvider.setServer(()-> {
            throw new IllegalStateException("Unable to access server yet");
        });
        LanguageHook.loadForgeAndMCLangs();
        try {
            ModLoader.gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{});
            ModLoader.loadMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{});
            ModLoader.finishMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{});
        } catch (LoadingFailedException error) {
            ServerModLoader.hasErrors = true;
            // In case its not loaded properly
            LanguageHook.loadForgeAndMCLangs();
            CrashReportExtender.dumpModLoadingCrashReport(LOGGER, error, new File("."));
            throw error;
        }
        List<ModLoadingWarning> warnings = ModLoader.getWarnings();
        if (!warnings.isEmpty()) {
            LOGGER.warn(Logging.LOADING, "Mods loaded with {} warnings", warnings.size());
            warnings.forEach(warning -> LOGGER.warn(Logging.LOADING, warning.formatToString()));
        }
        BusGroup.DEFAULT.startup();
    }

    public static boolean hasErrors() {
        return ServerModLoader.hasErrors;
    }
}
