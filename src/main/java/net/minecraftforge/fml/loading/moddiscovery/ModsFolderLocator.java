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

import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.common.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipError;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.uncheck;
import static net.minecraftforge.fml.Logging.SCAN;

/**
 * Support loading mods located in JAR files in the mods folder
 */
public class ModsFolderLocator implements IModLocator {
    private static final String SUFFIX = ".jar";
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path modFolder;
    private final Map<ModFile, FileSystem> modJars;
    public ModsFolderLocator() {
        this(FMLPaths.MODSDIR.get());
    }

    ModsFolderLocator(Path modFolder) {
        this.modFolder = modFolder;
        this.modJars = new HashMap<>();
    }

    @Override
    public List<ModFile> scanMods() {
        LOGGER.debug(SCAN,"Scanning mods dir {} for mods", this.modFolder);
        return uncheck(()-> Files.list(this.modFolder)).
                sorted(Comparator.comparing(path-> StringUtils.toLowerCase(path.getFileName().toString()))).
                filter(p->StringUtils.toLowerCase(p.getFileName().toString()).endsWith(SUFFIX)).
                map(p->new ModFile(p, this)).
                peek(f->modJars.compute(f, (mf, fs)->createFileSystem(mf))).
                collect(Collectors.toList());
    }

    @Override
    public String name() {
        return "mods folder";
    }

    private FileSystem createFileSystem(ModFile modFile) {
        try {
            return FileSystems.newFileSystem(modFile.getFilePath(), modFile.getClass().getClassLoader());
        } catch (ZipError | IOException e) {
            LOGGER.debug(SCAN,"Ignoring invalid JAR file {}", modFile.getFilePath());
            return null;
        }
    }

    @Override
    public Path findPath(final ModFile modFile, final String... path) {
        if (path.length < 1) {
            throw new IllegalArgumentException("Missing path");
        }
        return modJars.get(modFile).getPath(path[0], Arrays.copyOfRange(path, 1, path.length));
    }

    @Override
    public void scanFile(final ModFile file, final Consumer<Path> pathConsumer) {
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
    public String toString() {
        return "{ModJarsFolder locator at "+this.modFolder+"}";
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
}
