/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.userdev;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.IModLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipError;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.uncheck;
import static net.minecraftforge.fml.Logging.SCAN;

public class ClasspathLocator implements IModLocator {

    private static final String JAR_SUFFIX = ".jar";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Predicate<Path> JAR_FILTER = path -> StringUtils.toLowerCase(path.getFileName().toString()).endsWith(JAR_SUFFIX);
    private static final Comparator<Path> PATH_COMPARATOR = Comparator.comparing(path -> StringUtils.toLowerCase(path.getFileName().toString()));

    private final Map<ModFile, FileSystem> modJars;
    private final Table<ModFile, String[], Path> pathCache;
    private final List<Path> classpath;

    public ClasspathLocator() {
        modJars = new HashMap<>();
        pathCache = HashBasedTable.create();
        classpath = Stream.of(System.getProperty("java.class.path", "").split(File.pathSeparator))
                .distinct()
                .map(Paths::get)
                .filter(Files::exists)
                .sorted(PATH_COMPARATOR)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModFile> scanMods() {
        List<ModFile> mods = classpath.stream()
                .filter(JAR_FILTER)
                .map(path -> new ModFile(path, this))
                .filter(ModFile::identifyMods)
                .peek(modFile -> modJars.compute(modFile, (key, value) -> createFileSystem(key)))
                .collect(Collectors.toList());

        Set<URL> modUrls = Sets.newHashSet();

        try {
            modUrls.addAll(Collections.list(ClassLoader.getSystemResources("META-INF/coremods.json")));
            modUrls.addAll(Collections.list(ClassLoader.getSystemResources("META-INF/mods.toml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        modUrls.stream()
                .filter(url -> url.getProtocol().equals("file"))
                .map((url) -> {
                    try {
                        // We got URLs including "META-INF/<something", so get two components up.
                        return new File(url.toURI()).toPath().getParent().getParent();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .sorted(PATH_COMPARATOR)
                .map(path -> new ModFile(path, this))
                .forEach(mods::add);

        return mods;
    }

    private FileSystem createFileSystem(ModFile modFile) {
        try {
            return FileSystems.newFileSystem(modFile.getFilePath(), modFile.getClass().getClassLoader());
        } catch (ZipError | IOException e) {
            LOGGER.debug(SCAN, "Ignoring invalid JAR file {}", modFile.getFilePath());
            return null;
        }
    }

    @Override
    public String name() {
        return "classpath mods";
    }

    @Override
    public Path findPath(final ModFile modFile, final String... path) {
        if (path.length < 1) {
            throw new IllegalArgumentException("Missing path");
        }

        // Check mod jars
        FileSystem modJarsResult = modJars.get(modFile);

        if (modJarsResult != null) {
            // Resolve via mod jar
            return modJarsResult
                    .getPath(path[0], Arrays.copyOfRange(path, 1, path.length));
        }

        // Check path cache
        Path pathCacheResult = pathCache.get(modFile, path);

        if (pathCacheResult != null) {
            // Resolve via path cache
            return pathCacheResult;
        }

        Path resolvedPath = null;

        // Loop classpath, excluding jars
        for (Path dirPath : classpath.stream().filter(JAR_FILTER.negate()).collect(Collectors.toList())) {
            if (resolvedPath == null || !Files.exists(resolvedPath)) {
                resolvedPath = dirPath.resolve(FileSystems.getDefault().getPath(path[0], Arrays.copyOfRange(path, 1, path.length)));
            }
        }

        if (resolvedPath == null) {
            throw new IllegalStateException("Path not resolved");
        }

        // Cache resolved path
        pathCache.put(modFile, path, resolvedPath);

        // Resolve via classpath
        return resolvedPath;
    }

    @Override
    public void scanFile(final ModFile modFile, final Consumer<Path> pathConsumer) {
        LOGGER.debug(SCAN, "Scanning classpath");

        // Check mod jars
        if (modJars.containsKey(modFile)) {
            // Scan mod jar
            modJars.get(modFile)
                    .getRootDirectories()
                    .forEach(path -> scanPath(path, pathConsumer));

            return;
        }

        // Loop classpath, excluding jars
        classpath.stream()
                .filter(JAR_FILTER.negate())
                .forEach(path -> scanPath(path, pathConsumer));

        LOGGER.debug(SCAN, "Classpath scan complete");
    }

    private void scanPath(Path path, final Consumer<Path> pathConsumer) {
        try (Stream<Path> files = Files.find(path, Integer.MAX_VALUE, (p, attributes) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
            files.forEach(pathConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "{Classpath locator}";
    }

    @Override
    public Optional<Manifest> findManifest(Path file) {
        if (JAR_FILTER.test(file)) {
            try (JarFile jarFile = new JarFile(file.toFile())) {
                return Optional.ofNullable(jarFile.getManifest());
            } catch (IOException ignored) { }
        }

        return Optional.empty();
    }

}