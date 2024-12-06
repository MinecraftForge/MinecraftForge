/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;

import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ApiStatus.Internal
record Scanner(ModFile fileToScan, ModFileScanData result) {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEBUG = LOGGER.isDebugEnabled(LogMarkers.SCAN);

    public Scanner(ModFile fileToScan) {
        this(fileToScan, new ModFileScanData());
    }

    public ModFileScanData scan() {
        result.addModFileInfo(fileToScan.getModFileInfo());
        fileToScan.scanFile(this::fileVisitor);
        final List<IModLanguageProvider> loaders = fileToScan.getLoaders();
        if (loaders != null) {
            for (IModLanguageProvider loader : loaders) {
                if (DEBUG) LOGGER.debug("Scanning {} with language loader {}", fileToScan.getFilePath(), loader.name());
                loader.getFileVisitor().accept(result);
            }
        }
        return result;
    }

    private void fileVisitor(final Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            ModClassVisitor mcv = new ModClassVisitor();
            ClassReader cr = new ClassReader(in);
            cr.accept(mcv, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            mcv.buildData(result.getClasses(), result.getAnnotations());
        } catch (IOException | IllegalArgumentException e) {
            // mark path bad
            if (DEBUG) LOGGER.warn("Failed scanning {} path {}", fileToScan, path);
        }
    }
}
