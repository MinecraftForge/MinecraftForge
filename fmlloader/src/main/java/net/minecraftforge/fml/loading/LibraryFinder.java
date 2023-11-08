/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import java.nio.file.Files;
import java.nio.file.Path;

public class LibraryFinder {
    private static Path libsPath;
    static Path findLibsPath() {
        if (libsPath == null) {
            libsPath = Path.of(System.getProperty("libraryDirectory", "libraries"));
            if (!Files.isDirectory(libsPath))
                throw new IllegalStateException("Library directory: `" + libsPath + "` does not exist. Set it via the `libraryDirectory` system property.");
        }
        return libsPath;
    }

    public static Path findPathForMaven(final String group, final String artifact, final String extension, final String classifier, final String version) {
        return findLibsPath().resolve(MavenCoordinateResolver.get(group, artifact, extension, classifier, version));
    }
    public static Path findPathForMaven(final String maven) {
        return findLibsPath().resolve(MavenCoordinateResolver.get(maven));
    }
}
