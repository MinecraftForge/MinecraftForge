/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.fml.loading.MavenCoordinateResolver;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class ModListHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Reads the modList paths specified, and searches each maven root for mods matching. Returns a list of mods
     * found.
     *
     * @param modListPaths Paths to search for mod file lists
     * @param mavenRootPaths Roots to look for mods listed
     * @return list of found mod coordinates
     */
    public static List<String> processModLists(final List<String> modListPaths, final List<Path> mavenRootPaths) {
        final List<String> modCoordinates = modListPaths.stream().map(ModListHandler::transformPathToList).
                flatMap(Collection::stream).
                collect(Collectors.toList());

        List<Pair<Path,String>> localModCoords = modCoordinates.stream().map(mc->Pair.of(MavenCoordinateResolver.get(mc), mc)).collect(Collectors.toList());
        final List<Pair<Path, String>> foundCoordinates = localModCoords.stream().
                map(mc -> mavenRootPaths.stream().
                        map(root -> Pair.of(root.resolve(mc.getLeft()), mc.getRight())).
                        filter(path -> Files.exists(path.getLeft())).
                        findFirst().
                        orElseGet(()->{
                            LOGGER.warn(CORE, "Failed to find coordinate {}", mc);
                            return null;
                })).
                filter(Objects::nonNull).
                collect(Collectors.toList());

        final List<String> found = foundCoordinates.stream().map(Pair::getRight).collect(Collectors.toList());
        LOGGER.debug(CORE, "Found mod coordinates from lists: {}", found);
        return found;
    }

    private static List<String> transformPathToList(final String path) {
        LOGGER.debug(CORE, "Reading mod list {}", path);
        Path filePath = FMLPaths.GAMEDIR.get().resolve(path);
        if (!Files.exists(filePath)) {
            LOGGER.warn(CORE, "Failed to find modlist file at {}", filePath);
            return Collections.emptyList();
        }

        String extension = FileUtils.fileExtension(filePath);
        if (Objects.equals("list",extension)) {
            return readListFile(filePath).stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        } else {
            LOGGER.warn(CORE, "Failed to read unknown file list type {} for file {}", extension, filePath);
        }
        return Collections.emptyList();
    }

    /**
     * Simple list file, ending in ".list" with one mod coordinate per line
     * @param filePath path
     * @return list
     */
    private static List<String> readListFile(final Path filePath) {
        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            LOGGER.warn(CORE, "Failed to read file list {}", filePath, e);
            return Collections.emptyList();
        }
    }
}
