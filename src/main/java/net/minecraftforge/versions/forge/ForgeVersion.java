/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.versions.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.JarVersionLookupHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import static net.minecraftforge.fml.Logging.CORE;

public class ForgeVersion
{
    private static final Logger LOGGER = LogManager.getLogger();
    // This is Forge's Mod Id, used for the ForgeMod and resource locations
    public static final String MOD_ID = "forge";

    private static final String forgeVersion;
    private static final String forgeSpec;
    private static final String forgeGroup;

    static {
        LOGGER.debug(CORE, "Forge Version package {} from {}", ForgeVersion.class.getPackage(), ForgeVersion.class.getClassLoader());
        String vers = JarVersionLookupHandler.getImplementationVersion(ForgeVersion.class).orElse(FMLLoader.versionInfo().forgeVersion());
        if (vers == null) throw new RuntimeException("Missing forge version, cannot continue");
        String spec = JarVersionLookupHandler.getSpecificationVersion(ForgeVersion.class).orElse(System.getenv("FORGE_SPEC"));
        if (spec == null) throw new RuntimeException("Missing forge spec, cannot continue");
        String group = JarVersionLookupHandler.getImplementationTitle(ForgeVersion.class).orElse(FMLLoader.versionInfo().forgeGroup());
        if (group == null) {
            group = "net.minecraftforge"; // If all else fails, Our normal group
        }
        forgeVersion = vers;
        forgeSpec = spec;
        forgeGroup = group;
        LOGGER.debug(CORE, "Found Forge version {}", forgeVersion);
        LOGGER.debug(CORE, "Found Forge spec {}", forgeSpec);
        LOGGER.debug(CORE, "Found Forge group {}", forgeGroup);
    }

    public static String getVersion()
    {
        return forgeVersion;
    }

    public static VersionChecker.Status getStatus()
    {
        return VersionChecker.getResult(ModList.get().getModFileById(MOD_ID).getMods().get(0)).status();
    }

    @Nullable
    public static String getTarget()
    {
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

