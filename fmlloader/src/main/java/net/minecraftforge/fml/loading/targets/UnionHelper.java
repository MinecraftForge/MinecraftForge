/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiPredicate;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class UnionHelper {
    private static final FileSystemProvider UFSP = getUnionFileSystemProvider();
    private static FileSystemProvider getUnionFileSystemProvider() {
        for (var provider : FileSystemProvider.installedProviders()) {
            if ("union".equals(provider.getScheme()))
                return provider;
        }
        throw new IllegalStateException("Missing UnionFileSystemProvider");
    };

    static FileSystem newFileSystem(BiPredicate<String, String> filter, Path[] paths) {
        try {
            if (paths == null || paths.length == 0)
                throw new IllegalArgumentException("Must contain atleast one path");

            if (filter == null && paths.length == 1)
                return FileSystems.newFileSystem(paths[0]);

            var map = new HashMap<String, Object>();
            if (filter != null)
                map.put("filter", filter);

            var lst = new ArrayList<>(Arrays.asList(paths));
            var base = lst.removeFirst();
            map.put("additional", lst);
            return UFSP.newFileSystem(base, map);
        } catch (IOException e) {
            return sneak(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable, R> R sneak(Throwable e) throws E {
        throw (E)e;
    }
}
