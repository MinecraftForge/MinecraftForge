/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.ModFileFactory;
import net.minecraftforge.forgespi.locating.ModFileLoadingException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

public class ModFileParser {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static IModFileInfo readModList(final ModFile modFile, final ModFileFactory.ModFileInfoParser parser) {
        return parser.build(modFile);
    }

    // Note: Although @Nullable, several other places in FML assume ModFileInfo is not null. Keep this in mind
    public static @Nullable IModFileInfo modsTomlParser(final IModFile imodFile) {
        ModFile modFile = (ModFile) imodFile;
        LOGGER.debug(LogMarkers.LOADING,"Considering mod file candidate {}", modFile.getFilePath());
        final Path modsjson = modFile.findResource("META-INF", "mods.toml");
        if (!Files.exists(modsjson)) {
            LOGGER.warn(LogMarkers.LOADING, "Mod file {} is missing mods.toml file", modFile.getFilePath());
            return null;
        }

        final FileConfig fileConfig = FileConfig.builder(modsjson).build();
        try {
            fileConfig.load();
            fileConfig.close();
            final NightConfigWrapper configWrapper = new NightConfigWrapper(fileConfig);
            return new ModFileInfo(modFile, configWrapper, configWrapper::setFile);
        } catch (ParsingException e) {
            LOGGER.error("Mod candidate {} contains a corrupt or misconfigured toml.", modFile.getFileName());
            throw new ModFileLoadingException("Mod candidate " + modFile.getFileName() + " contains a corrupt or misconfigured toml.");
        }

    }
}
