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

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.ModSorter;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipError;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;


public class ModDiscoverer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServiceLoader<IModLocator> locators;
    private final List<IModLocator> locatorList;

    public ModDiscoverer(Map<String, ?> arguments) {
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.MODFOLDERFACTORY.get(), v->ModsFolderLocator::new);
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.PROGRESSMESSAGE.get(), v->StartupMessageManager.locatorConsumer().orElseGet(()->s->{}));
        locators = ServiceLoader.load(IModLocator.class);
        locatorList = ServiceLoaderStreamUtils.toList(this.locators);
        locatorList.forEach(l->l.initArguments(arguments));
        locatorList.add(new MinecraftLocator());
        LOGGER.debug(CORE,"Found Mod Locators : {}", ()->locatorList.stream().map(iModLocator -> "("+iModLocator.name() + ":" + iModLocator.getClass().getPackage().getImplementationVersion()+")").collect(Collectors.joining(",")));
    }

    ModDiscoverer(List<IModLocator> locatorList) {
        this.locatorList = locatorList;
        this.locators = null;
    }

    public BackgroundScanHandler discoverMods() {
        LOGGER.debug(SCAN,"Scanning for mods and other resources to load. We know {} ways to find mods", locatorList.size());
        final Map<IModFile.Type, List<ModFile>> modFiles = locatorList.stream()
                .peek(loc -> LOGGER.debug(SCAN,"Trying locator {}", loc))
                .map(IModLocator::scanMods)
                .flatMap(Collection::stream)
                .peek(mf -> LOGGER.debug(SCAN,"Found mod file {} of type {} with locator {}", mf.getFileName(), mf.getType(), mf.getLocator()))
                .peek(mf -> StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found mod file "+mf.getFileName()+" of type "+mf.getType())))
                .map(ModFile.class::cast)
                .collect(Collectors.groupingBy(IModFile::getType));

        FMLLoader.getLanguageLoadingProvider().addAdditionalLanguages(modFiles.get(IModFile.Type.LANGPROVIDER));
        BackgroundScanHandler backgroundScanHandler = new BackgroundScanHandler(modFiles);
        final List<ModFile> mods = modFiles.getOrDefault(IModFile.Type.MOD, Collections.emptyList());
        final List<ModFile> brokenFiles = new ArrayList<>();
        for (Iterator<ModFile> iterator = mods.iterator(); iterator.hasNext(); )
        {
            ModFile mod = iterator.next();
            if (!mod.getLocator().isValid(mod) || !mod.identifyMods()) {
                LOGGER.warn(SCAN, "File {} has been ignored - it is invalid", mod.getFilePath());
                iterator.remove();
                brokenFiles.add(mod);
            }
        }
        LOGGER.debug(SCAN,"Found {} mod files with {} mods", mods::size, ()->mods.stream().mapToInt(mf -> mf.getModInfos().size()).sum());
        StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found "+mods.size()+" modfiles to load"));
        final LoadingModList loadingModList = ModSorter.sort(mods);
        loadingModList.addCoreMods();
        loadingModList.addAccessTransformers();
        loadingModList.addForScanning(backgroundScanHandler);
        loadingModList.setBrokenFiles(brokenFiles);
        return backgroundScanHandler;
    }

    private static class MinecraftLocator implements IModLocator {
        private final Path mcJar = FMLLoader.getMCPaths()[0];
        private final FileSystem fileSystem;

        MinecraftLocator() {
            if (!Files.isDirectory(mcJar)) {
                try {
                    fileSystem = FileSystems.newFileSystem(mcJar, getClass().getClassLoader());
                } catch (ZipError | IOException e) {
                    LOGGER.fatal(SCAN,"Invalid Minecraft JAR file - no filesystem created");
                    throw new RuntimeException(e);
                }
            } else {
                fileSystem = null;
            }
        }

        @Override
        public List<IModFile> scanMods() {
            return Collections.singletonList(new ModFile(mcJar, this));
        }

        @Override
        public String name() {
            return "minecraft";
        }

        @Override
        public Path findPath(final IModFile modFile, final String... path) {
            String[] newPath = Arrays.copyOf(path, path.length);
            if (path.length == 2 && Objects.equals(path[1], "mods.toml")) {
                final URI jarFileURI;
                try {
                    jarFileURI = getClass().getClassLoader().getResource("minecraftmod.toml").toURI();
                    if (Objects.equals(jarFileURI.getScheme(), "jar")) {
                        // Initialize the filesystem for the forge jar, because otherwise this barfs?
                        FileSystems.newFileSystem(jarFileURI, new HashMap<>());
                    }
                } catch (URISyntaxException | IOException e) {
                    LOGGER.fatal(SCAN, "Unable to read minecraft default mod");
                    throw new RuntimeException(e);
                }
                return Paths.get(jarFileURI);
            }
            if (Files.isDirectory(mcJar)) return findPathDirectory(modFile, path);
            return findPathJar(modFile, path);
        }

        private Path findPathDirectory(final IModFile modFile, final String... path) {
            if (path.length < 1) {
                throw new IllegalArgumentException("Missing path");
            }
            final Path target = Paths.get(path[0], Arrays.copyOfRange(path, 1, path.length));
            // try right path first (resources)
            return mcJar.resolve(target);
        }

        private Path findPathJar(final IModFile modFile, final String... path) {
            return fileSystem.getPath(path[0], Arrays.copyOfRange(path, 1, path.length));
        }
        @Override
        public void scanFile(final IModFile modFile, final Consumer<Path> pathConsumer) {
            LOGGER.debug(SCAN,"Scan started: {}", modFile);
            Path path;
            if (Files.isDirectory(mcJar))
                path = mcJar;
            else
                path = fileSystem.getPath("/");
            try (Stream<Path> files = Files.find(path, Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
                files.forEach(pathConsumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.debug(SCAN,"Scan finished: {}", modFile);
        }

        @Override
        public Optional<Manifest> findManifest(final Path file) {
            return Optional.empty();
        }

        @Override
        public void initArguments(final Map<String, ?> arguments) {
            // no op
        }

        @Override
        public boolean isValid(final IModFile modFile) {
            return true;
        }
    }
}
