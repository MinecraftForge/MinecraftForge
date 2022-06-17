/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class LauncherVersion {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String launcherVersion;

    static {
        String vers = JarVersionLookupHandler.getImplementationVersion(LauncherVersion.class).orElse(System.getenv("LAUNCHER_VERSION"));
        if (vers == null) throw new RuntimeException("Missing FMLLauncher version, cannot continue");
        launcherVersion = vers;
        LOGGER.debug(CORE, "Found FMLLauncher version {}", launcherVersion);
    }

    public static String getVersion()
    {
        return launcherVersion;
    }
}
