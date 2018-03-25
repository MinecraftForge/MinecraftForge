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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.fmlLog;

public class ExplodedDirectoryLocator implements IModLocator {
    private static final String DIR = System.getProperty("fml.explodedDir", "modclasses");
    private final Path rootDir;

    ExplodedDirectoryLocator() {
        this.rootDir = FileSystems.getDefault().getPath(DIR);
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
        fmlLog.debug("Scanning directory {}", rootDir);
        try (Stream<Path> files = Files.find(rootDir, Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
            files.forEach(pathConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fmlLog.debug("Directory scan complete {}", rootDir);
    }
}
