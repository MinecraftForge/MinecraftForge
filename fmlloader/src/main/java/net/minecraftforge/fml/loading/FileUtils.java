/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class FileUtils
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Path getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath.getParent())) {
            getOrCreateDirectory(dirPath.getParent(), "parent of "+dirLabel);
        }
        if (!Files.isDirectory(dirPath))
        {
            LOGGER.debug(CORE, "Making {} directory : {}", dirLabel, dirPath);
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                if (e instanceof FileAlreadyExistsException) {
                    LOGGER.error(CORE, "Failed to create {} directory - there is a file in the way", dirLabel);
                } else {
                    LOGGER.error(CORE, "Problem with creating {} directory (Permissions?)", dirLabel, e);
                }
                throw new RuntimeException("Problem creating directory", e);
            }
            LOGGER.debug(CORE, "Created {} directory : {}", dirLabel, dirPath);
        } else {
            LOGGER.debug(CORE, "Found existing {} directory : {}", dirLabel, dirPath);
        }
        return dirPath;
    }


    public static String fileExtension(final Path path) {
        String fileName = path.getFileName().toString();
        int idx = fileName.lastIndexOf('.');
        if (idx > -1) {
            return fileName.substring(idx+1);
        } else {
            return "";
        }
    }
}
