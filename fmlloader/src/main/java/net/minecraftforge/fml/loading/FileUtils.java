/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils
{
    private static final Logger LOGGER = LogUtils.getLogger();

   /**
    * @deprecated Use normal Java NIO methods instead
    */
    @Deprecated(forRemoval = true, since = "1.19.4")
    public static Path getOrCreateDirectory(Path dirPath, String dirLabel) {
        if (!Files.isDirectory(dirPath))
        {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("Problem creating directory", e);
            }
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

    public static boolean matchFileName(String path, String... matches) {
        // Extract file name from path
        String name = path.substring(Math.min(path.lastIndexOf(File.separatorChar) + 1, path.length()));
        // Check if it contains any of the desired keywords
        for (String match : matches) {
            if (name.contains(match)) {
                return true;
            }
        }
        return false;
    }
}
