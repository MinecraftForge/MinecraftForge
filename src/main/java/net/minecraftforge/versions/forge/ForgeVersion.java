/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.versions.forge;

import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.JarVersionLookupHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ForgeVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    // This is Forge's Mod Id, used for the ForgeMod and resource locations
    public static final String MOD_ID = "forge";

    private static final String forgeVersion;
    private static final String forgeSpec;
    private static final String forgeGroup;

    static {
        LOGGER.debug(Logging.CORE, "Forge Version package {} from {}", ForgeVersion.class.getPackage(), ForgeVersion.class.getClassLoader());
        var info = JarVersionLookupHandler.getInfo(ForgeVersion.class);

        if (info.impl().version().isEmpty() || info.spec().version().isEmpty())
            throw new IllegalStateException("Failed to find version for package " + ForgeVersion.class.getPackageName() + " This is an invalid environment");

        forgeSpec = info.spec().version().get();
        forgeVersion = info.impl().version().get();
        forgeGroup = "net.minecraftforge";
        LOGGER.debug(Logging.CORE, "Found Forge version {}", forgeVersion);
        LOGGER.debug(Logging.CORE, "Found Forge spec {}", forgeSpec);
        LOGGER.debug(Logging.CORE, "Found Forge group {}", forgeGroup);
    }

    public static String getVersion() {
        return forgeVersion;
    }

    public static VersionChecker.Status getStatus() {
        return VersionChecker.getResult(ModList.get().getModFileById(MOD_ID).getMods().get(0)).status();
    }

    @Nullable
    public static String getTarget() {
        VersionChecker.CheckResult res = VersionChecker.getResult(ModList.get().getModFileById(MOD_ID).getMods().get(0));
        return res.target() == null ? "" : res.target().toString();
    }

    public static String getSpec() {
        return forgeSpec;
    }

    public static String getGroup() {
        return forgeGroup;
    }
}

