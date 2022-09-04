/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.loading.ClasspathLocatorUtils;
import net.minecraftforge.fml.loading.LogMarkers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ClasspathLocator extends AbstractJarFileModLocator {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Path> legacyClasspath = Arrays.stream(System.getProperty("legacyClassPath", "").split(File.pathSeparator)).map(Path::of).toList();
    private boolean enabled = false;

    @Override
    public String name() {
        return "userdev classpath";
    }

    @Override
    public Stream<Path> scanCandidates() {
        if (!enabled)
            return Stream.of();

        try {
            var claimed = new ArrayList<>(legacyClasspath);
            var paths = Stream.<Path>builder();

            findPaths(claimed, MODS_TOML).forEach(paths::add);
            findPaths(claimed, MANIFEST).forEach(paths::add);

            return paths.build();
        } catch (IOException e) {
            LOGGER.fatal(LogMarkers.SCAN, "Error trying to find resources", e);
            throw new RuntimeException(e);
        }
    }

    private List<Path> findPaths(List<Path> claimed, String resource) throws IOException {
        var ret = new ArrayList<Path>();
        final Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources(resource);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Path path = ClasspathLocatorUtils.findJarPathFor(resource, resource, url);
            if (claimed.stream().anyMatch(path::equals) || !Files.exists(path) || Files.isDirectory(path))
                continue;
            ret.add(path);
        }
        return ret;
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {
        var launchTarget = (String) arguments.get("launchTarget");
        enabled = launchTarget != null && launchTarget.contains("dev");
    }
}
