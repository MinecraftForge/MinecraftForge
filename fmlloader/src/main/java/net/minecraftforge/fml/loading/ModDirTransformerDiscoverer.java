/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import cpw.mods.modlauncher.api.NamedPath;
import cpw.mods.modlauncher.serviceapi.ITransformerDiscoveryService;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.zip.ZipFile;
import java.util.Set;

public class ModDirTransformerDiscoverer implements ITransformerDiscoveryService {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> SERVICES = Set.of(
        "cpw.mods.modlauncher.api.ITransformationService",
        "net.minecraftforge.forgespi.locating.IModLocator",
        "net.minecraftforge.forgespi.locating.IDependencyLocator"
    );

    @Override
    public List<NamedPath> candidates(final Path gameDirectory, final String launchTarget) {
        FMLPaths.loadAbsolutePaths(gameDirectory);
        FMLConfig.load();
        return candidates(gameDirectory);
    }

    @Override
    public void earlyInitialization(final String launchTarget, final String[] arguments) {
        ImmediateWindowHandler.load(launchTarget, arguments);
    }

    @Override
    public List<NamedPath> candidates(final Path gameDirectory) {
        ModDirTransformerDiscoverer.scan(gameDirectory);
        return List.copyOf(found);
    }

    private final static List<NamedPath> found = new ArrayList<>();

    public static List<Path> allExcluded() {
        return found.stream().map(np->np.paths()[0]).toList();
    }

    private static void scan(final Path gameDirectory) {
        final Path modsDir = gameDirectory.resolve(FMLPaths.MODSDIR.relative()).toAbsolutePath().normalize();
        if (!Files.exists(modsDir)) {
            // Skip if the mods dir doesn't exist yet.
            return;
        }
        try (var walk = Files.walk(modsDir, 1)){
            walk.forEach(ModDirTransformerDiscoverer::visitFile);
        } catch (IOException | IllegalStateException ioe) {
            LOGGER.error("Error during early discovery", ioe);
        }
    }

    private static void visitFile(Path path) {
        if (!Files.isRegularFile(path)) return;
        if (!path.toString().endsWith(".jar")) return;
        if (LamdbaExceptionUtils.uncheck(() -> Files.size(path)) == 0) return;

        SecureJar jar = SecureJar.from(path);
        jar.moduleDataProvider().descriptor().provides().stream()
            .map(ModuleDescriptor.Provides::service)
            .filter(SERVICES::contains)
            .forEach(s -> found.add(new NamedPath(s, path)));
    }
}
