/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import net.minecraftforge.fml.loading.StringUtils;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.uncheck;

/**
 * Support loading mods located in JAR files in the mods folder
 */
public class ModsFolderLocator extends AbstractJarFileModLocator
{
    private static final String SUFFIX = ".jar";
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path modFolder;
    private final String customName;

    public ModsFolderLocator() {
        this(FMLPaths.MODSDIR.get());
    }

    ModsFolderLocator(Path modFolder) {
        this(modFolder, "mods folder");
    }

    ModsFolderLocator(Path modFolder, String name) {
        this.modFolder = modFolder;
        this.customName = name;
    }

    @Override
    public Stream<Path> scanCandidates() {
        LOGGER.debug(LogMarkers.SCAN,"Scanning mods dir {} for mods", this.modFolder);
        var excluded = ModDirTransformerDiscoverer.allExcluded();

        return uncheck(()-> Files.list(this.modFolder))
                .filter(p-> !excluded.contains(p) && StringUtils.toLowerCase(p.getFileName().toString()).endsWith(SUFFIX))
                .sorted(Comparator.comparing(path-> StringUtils.toLowerCase(path.getFileName().toString())));
    }

    @Override
    public String name() {
        return customName;
    }

    @Override
    public String toString() {
        return "{"+customName+" locator at "+this.modFolder+"}";
    }

    @Override
    public void initArguments(final Map<String, ?> arguments) {
    }
}
