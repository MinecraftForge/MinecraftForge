/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.uncheck;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

/**
 * Support loading mods located in JAR files in the mods folder
 */
public class ModsFolderLocator extends AbstractJarFileLocator {
    private static final String SUFFIX = ".jar";
    private static final Logger LOGGER = LogManager.getLogger();
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
    public List<IModFile> scanMods() {
        LOGGER.debug(SCAN,"Scanning mods dir {} for mods", this.modFolder);
        List<Path> excluded = ModDirTransformerDiscoverer.allExcluded();
        return uncheck(()-> Files.list(this.modFolder))
                .filter(p->!excluded.contains(p))
                .sorted(Comparator.comparing(path-> StringUtils.toLowerCase(path.getFileName().toString())))
                .filter(p->StringUtils.toLowerCase(p.getFileName().toString()).endsWith(SUFFIX))
                .map(p->ModFile.newFMLInstance(p, this))
                .peek(f->modJars.compute(f, (mf, fs)->createFileSystem(mf)))
                .collect(Collectors.toList());
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
