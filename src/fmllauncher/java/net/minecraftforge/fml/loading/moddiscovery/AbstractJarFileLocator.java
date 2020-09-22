/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.zip.ZipError;

import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public abstract class AbstractJarFileLocator implements IModLocator {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Map<IModFile, FileSystem> modJars;

    public AbstractJarFileLocator() {
        this.modJars = new HashMap<>();
    }

    protected FileSystem createFileSystem(IModFile modFile) {
        try {
            return FileSystems.newFileSystem(modFile.getFilePath(), modFile.getClass().getClassLoader());
        } catch (ZipError | IOException e) {
            LOGGER.debug(SCAN,"Invalid JAR file {} - no filesystem created", modFile.getFilePath());
            return null;
        }
    }

    @Override
    public Path findPath(final IModFile modFile, final String... path) {
        if (path.length < 1) {
            throw new IllegalArgumentException("Missing path");
        }
        return modJars.get(modFile).getPath("",path);
    }

    @Override
    public void scanFile(final IModFile file, final Consumer<Path> pathConsumer) {
        LOGGER.debug(SCAN,"Scan started: {}", file);
        FileSystem fs = modJars.get(file);
        fs.getRootDirectories().forEach(path -> {
            try (Stream<Path> files = Files.find(path, Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
                files.forEach(pathConsumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        LOGGER.debug(SCAN,"Scan finished: {}", file);
    }

    @Override
    public Optional<Manifest> findManifest(final Path file)
    {
        try (JarFile jf = new JarFile(file.toFile()))
        {
            return Optional.ofNullable(jf.getManifest());
        }
        catch (IOException e)
        {
            return Optional.empty();
        }
    }

    @Override
    public boolean isValid(final IModFile modFile) {
        return modJars.get(modFile) != null;
    }
}
