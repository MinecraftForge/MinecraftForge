/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import java.io.File;
import java.nio.file.Path;

public class FileUtils
{

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
