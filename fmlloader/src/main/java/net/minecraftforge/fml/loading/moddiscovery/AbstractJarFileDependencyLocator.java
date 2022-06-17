/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.ModFileLoadingException;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class AbstractJarFileDependencyLocator extends AbstractJarFileModProvider implements IDependencyLocator
{
    private static final Logger LOGGER = LogUtils.getLogger();

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
            return Optional.of(createMod(pathInModFile).file());
        }
        catch (Exception e) {
            LOGGER.error("Failed to load mod file {} from {}", path, file.getFileName());
            throw new ModFileLoadingException("Failed to load mod file "+file.getFileName());
        }
    }

    protected String identifyMod(final IModFile modFile) {
        return modFile.getFileName();
    }
}
