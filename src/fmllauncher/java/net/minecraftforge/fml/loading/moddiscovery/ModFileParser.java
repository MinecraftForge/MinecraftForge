/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.forgespi.language.IModFileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class ModFileParser {

    private static final Logger LOGGER = LogManager.getLogger();

    public static IModFileInfo readModList(final ModFile modFile) {
        LOGGER.debug(LOADING,"Considering mod file candidate {}", modFile.getFilePath());
        final Path modsjson = modFile.getLocator().findPath(modFile, "META-INF", "mods.toml");
        if (!Files.exists(modsjson)) {
            LOGGER.warn(LOADING, "Mod file {} is missing mods.toml file", modFile);
            return null;
        }
        return loadModFile(modFile, modsjson);
    }

    public static IModFileInfo loadModFile(final ModFile file, final Path modsjson)
    {
        final FileConfig fileConfig = FileConfig.builder(modsjson).build();
        fileConfig.load();
        fileConfig.close();
        return new ModFileInfo(file, fileConfig);
    }

    protected static List<CoreModFile> getCoreMods(final ModFile modFile) {
        Map<String,String> coreModPaths;
        try {
            final Path coremodsjson = modFile.getLocator().findPath(modFile, "META-INF", "coremods.json");
            if (!Files.exists(coremodsjson)) {
                return Collections.emptyList();
            }
            final Type type = new TypeToken<Map<String, String>>() {}.getType();
            final Gson gson = new Gson();
            coreModPaths = gson.fromJson(Files.newBufferedReader(coremodsjson), type);
        } catch (IOException e) {
            LOGGER.debug(LOADING,"Failed to read coremod list coremods.json", e);
            return Collections.emptyList();
        }

        return coreModPaths.entrySet().stream().
                peek(e-> LOGGER.debug(LOADING,"Found coremod {} with Javascript path {}", e.getKey(), e.getValue())).
                map(e -> new CoreModFile(e.getKey(), modFile.getLocator().findPath(modFile, e.getValue()),modFile)).
                collect(Collectors.toList());
    }
}
