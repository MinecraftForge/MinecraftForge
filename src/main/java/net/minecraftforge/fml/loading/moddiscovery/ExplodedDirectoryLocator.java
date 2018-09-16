/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.LOADING;
import static net.minecraftforge.fml.Logging.SCAN;

public class ExplodedDirectoryLocator implements IModLocator {
    private static final String DIR = System.getProperty("fml.explodedDir", "modclasses");
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path rootDir;

    public ExplodedDirectoryLocator() {
        this.rootDir = FileSystems.getDefault().getPath(DIR);
        if (!Files.exists(this.rootDir)) {
            LOGGER.debug(LOADING,"Creating directory {}" + this.rootDir);
            try
            {
                Files.createDirectory(this.rootDir);
            }
            catch (IOException e)
            {
                LOGGER.error(LOADING,"Error creating {}", this.rootDir, e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<ModFile> scanMods() {
        return Collections.singletonList(new ModFile(rootDir, this));
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
        return rootDir.resolve(FileSystems.getDefault().getPath(path[0], Arrays.copyOfRange(path, 1, path.length)));
    }

    @Override
    public void scanFile(final ModFile modFile, final Consumer<Path> pathConsumer) {
        LOGGER.debug(SCAN,"Scanning directory {}", rootDir);
        try (Stream<Path> files = Files.find(rootDir, Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
            files.forEach(pathConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug(SCAN,"Directory scan complete {}", rootDir);
    }

    @Override
    public String toString()
    {
        return "{ExplodedDir locator at "+this.rootDir+"}";
    }

    @Override
    public Optional<Manifest> findManifest(Path file)
    {
        return Optional.empty();
    }
}
