/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import cpw.mods.modlauncher.api.NamedPath;
import cpw.mods.modlauncher.serviceapi.ITransformerDiscoveryService;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

public class ModDirTransformerDiscoverer implements ITransformerDiscoveryService {
    private static final Logger LOGGER = LogUtils.getLogger();

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
        try (ZipFile zf = new ZipFile(new File(path.toUri()))) {
            if (zf.getEntry("META-INF/services/cpw.mods.modlauncher.api.ITransformationService") != null) {
                found.add(new NamedPath(zf.getName(), path));
            } else if (zf.getEntry("META-INF/services/net.minecraftforge.forgespi.locating.IModLocator") != null) {
                found.add(new NamedPath(zf.getName(), path));
            }
        } catch (IOException ioe) {
            LOGGER.error("Zip Error when loading jar file {}", path, ioe);
        }
    }
}
