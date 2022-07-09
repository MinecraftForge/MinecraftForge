/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class AbstractJarFileDependencyLocator extends AbstractJarFileLocator
{
    private static final Logger LOGGER = LogManager.getLogger();

    protected Optional<InputStream> loadResourceFromModFile(final IModFile modFile, final Path path) {
        try {
            return Optional.of(Files.newInputStream(modFile.findResource(path.toString())));
        }
        catch (final FileNotFoundException e) {
            LOGGER.debug("Failed to load resource {} from {}, it does not contain dependency information.", path, modFile.getFileName());
            return Optional.empty();
        }
        catch (final Exception e) {
            LOGGER.error("Failed to load resource {} from mod {}, cause {}", path, modFile.getFileName(), e);
            return Optional.empty();
        }
    }

    protected Optional<IModFile> loadModFileFrom(final IModFile file, final Path path) {
        try {
            final Path pathInModFile = file.findResource(path.toString());
            return createMod(pathInModFile);
        }
        catch (Exception e) {
            LOGGER.error("Failed to load mod file {} from {}", path, file.getFileName());
            return Optional.empty();
        }
    }

    protected String identifyMod(final IModFile modFile) {
        return modFile.getFileName();
    }
}
