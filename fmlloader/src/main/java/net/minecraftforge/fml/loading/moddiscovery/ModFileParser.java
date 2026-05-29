/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading.moddiscovery;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.mojang.logging.LogUtils;
import net.minecraftsingularity.fml.loading.LogMarkers;
import net.minecraftsingularity.singularityspi.language.IModFileInfo;
import net.minecraftsingularity.singularityspi.locating.IModFile;
import net.minecraftsingularity.singularityspi.locating.ModFileFactory;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

public class ModFileParser {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static IModFileInfo readModList(final ModFile modFile, final ModFileFactory.ModFileInfoParser parser) {
        return parser.build(modFile);
    }

    public static @Nullable IModFileInfo modsTomlParser(final IModFile imodFile) {
        ModFile modFile = (ModFile) imodFile;
        LOGGER.debug(LogMarkers.LOADING,"Considering mod file candidate {}", modFile.getFilePath());
        final Path modsjson = modFile.findResource("META-INF", "mods.toml");
        if (!Files.exists(modsjson)) {
            LOGGER.warn(LogMarkers.LOADING, "Mod file {} is missing mods.toml file", modFile.getFilePath());
            return null;
        }

        final FileConfig fileConfig = FileConfig.builder(modsjson).build();
        fileConfig.load();
        fileConfig.close();
        final NightConfigWrapper configWrapper = new NightConfigWrapper(fileConfig);
        return new ModFileInfo(modFile, configWrapper, configWrapper::setFile);
    }
}
