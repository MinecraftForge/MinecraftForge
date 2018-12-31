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

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.loading.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class ExplodedDirectoryLocator implements IModLocator {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Pair<Path,Path>> rootDirs;
    private final Map<ModFile, Pair<Path,Path>> mods;

    public ExplodedDirectoryLocator() {
        this.rootDirs = new ArrayList<>();
        this.mods = new HashMap<>();
    }

    @Override
    public List<ModFile> scanMods() {
        final Path modstoml = Paths.get("META-INF", "mods.toml");
        // Collect all the mods.toml files found
        rootDirs.forEach(pathPathPair -> {
            Path resources = pathPathPair.getRight();
            Path modtoml = resources.resolve(modstoml);
            if (Files.exists(modtoml)) {
                ModFile mf = new ModFile(pathPathPair.getLeft(), this);
                mods.put(mf, pathPathPair);
            } else {
                LOGGER.warn(LOADING, "Failed to find exploded resource mods.toml in directory {}", resources.toString());
            }
        });
        return new ArrayList<>(mods.keySet());
    }

    @Override
    public String name() {
        return "exploded directory";
    }

    @Override
    public Path findPath(final ModFile modFile, final String... path) {
        if (path.length < 1) {
            throw new IllegalArgumentException("Missing path");
        }
        final Path target = Paths.get(path[0], Arrays.copyOfRange(path, 1, path.length));
        // try right path first (resources)
        Path found = mods.get(modFile).getRight().resolve(target);
        if (Files.exists(found)) return found;
        // then try left path (classes)
        return mods.get(modFile).getLeft().resolve(target);
    }

    @Override
    public void scanFile(final ModFile modFile, final Consumer<Path> pathConsumer) {
        LOGGER.debug(SCAN,"Scanning directory {}", modFile.getFilePath().toString());
        final Pair<Path, Path> pathPathPair = mods.get(modFile);
        // classes are in the left branch of the pair
        try (Stream<Path> files = Files.find(pathPathPair.getLeft(), Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
            files.forEach(pathConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug(SCAN,"Directory scan complete {}", pathPathPair.getLeft());
    }

    @Override
    public String toString()
    {
        return "{ExplodedDir locator}";
    }

    @Override
    public Optional<Manifest> findManifest(Path file)
    {
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initArguments(final Map<String, ?> arguments) {
        final List<Pair<Path, Path>> explodedTargets = ((Map<String, List<Pair<Path, Path>>>) arguments).get("explodedTargets");
        rootDirs.addAll(explodedTargets);
    }
}
