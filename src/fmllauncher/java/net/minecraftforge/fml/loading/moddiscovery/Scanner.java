/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class Scanner {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ModFile fileToScan;

    public Scanner(final ModFile fileToScan) {
        this.fileToScan = fileToScan;
    }

    public ModFileScanData scan() {
        ModFileScanData result = new ModFileScanData();
        result.addModFileInfo(fileToScan.getModFileInfo());
        fileToScan.scanFile(p -> fileVisitor(p, result));
        final IModLanguageProvider loader = fileToScan.getLoader();
        if (loader != null) {
            LOGGER.debug(SCAN, "Scanning {} with language loader {}", fileToScan.getFilePath(), loader.name());
            loader.getFileVisitor().accept(result);
        }
        return result;
    }

    private void fileVisitor(final Path path, final ModFileScanData result) {
        LOGGER.debug(SCAN,"Scanning {} path {}", fileToScan, path);
        try (InputStream in = Files.newInputStream(path)){
            ModClassVisitor mcv = new ModClassVisitor();
            ClassReader cr = new ClassReader(in);
            cr.accept(mcv, 0);
            mcv.buildData(result.getClasses(), result.getAnnotations());
        } catch (IOException | IllegalArgumentException e) {
            // mark path bad
        }
    }
}
