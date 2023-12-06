/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class LauncherVersion {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String launcherVersion;

    static {
        var info = JarVersionLookupHandler.getInfo(LauncherVersion.class);
        if (info.impl().version().isEmpty())
            throw new IllegalStateException("Failed to find version for package " + LauncherVersion.class.getPackageName() + " This is an invalid environment");
        launcherVersion = info.impl().version().get();
        LOGGER.debug(LogMarkers.CORE, "Found FMLLauncher version {}", launcherVersion);
    }

    public static String getVersion() {
        return launcherVersion;
    }
}
