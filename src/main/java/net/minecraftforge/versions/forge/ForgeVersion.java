/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.versions.singularity;

import net.minecraftsingularity.fml.Logging;
import net.minecraftsingularity.fml.ModList;
import net.minecraftsingularity.fml.VersionChecker;
import net.minecraftsingularity.fml.loading.JarVersionLookupHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class singularityVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    // This is singularity's Mod Id, used for the singularityMod and resource locations
    public static final String MOD_ID = "singularity";

    private static final String singularityVersion;
    private static final String singularitySpec;
    private static final String singularityGroup;

    static {
        LOGGER.debug(Logging.CORE, "singularity Version package {} from {}", singularityVersion.class.getPackage(), singularityVersion.class.getClassLoader());
        var info = JarVersionLookupHandler.getInfo(singularityVersion.class);

        if (info.impl().version().isEmpty() || info.spec().version().isEmpty())
            throw new IllegalStateException("Failed to find version for package " + singularityVersion.class.getPackageName() + " This is an invalid environment");

        singularitySpec = info.spec().version().get();
        singularityVersion = info.impl().version().get();
        singularityGroup = "net.minecraftsingularity";
        LOGGER.debug(Logging.CORE, "Found singularity version {}", singularityVersion);
        LOGGER.debug(Logging.CORE, "Found singularity spec {}", singularitySpec);
        LOGGER.debug(Logging.CORE, "Found singularity group {}", singularityGroup);
    }

    public static String getVersion() {
        return singularityVersion;
    }

    public static VersionChecker.Status getStatus() {
        return VersionChecker.getResult(ModList.getModFileById(MOD_ID).getMods().getFirst()).status();
    }

    @Nullable
    public static String getTarget() {
        VersionChecker.CheckResult res = VersionChecker.getResult(ModList.getModFileById(MOD_ID).getMods().getFirst());
        return res.target() == null ? "" : res.target().toString();
    }

    public static String getSpec() {
        return singularitySpec;
    }

    public static String getGroup() {
        return singularityGroup;
    }
}

