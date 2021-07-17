/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class ExplodedDirectoryLocator implements IModLocator {
    private static final Logger LOGGER = LogManager.getLogger();

    public record ExplodedMod(String modid, List<Path> paths) {}

    private final List<ExplodedMod> explodedMods = new ArrayList<>();
    private final Map<ExplodedMod, IModFile> mods = new HashMap<>();

    @Override
    public List<IModFile> scanMods() {
        explodedMods.forEach(explodedMod -> ModJarMetadata.buildFile(this, jar ->jar.findFile("/META-INF/mods.toml").isPresent(), explodedMod.paths().toArray(Path[]::new))
                .ifPresentOrElse(f->mods.put(explodedMod, f), () -> LOGGER.warn(LOADING, "Failed to find exploded resource mods.toml in directory {}", explodedMod.paths().get(0).toString())));
        return List.copyOf(mods.values());
    }

    @Override
    public String name() {
        return "exploded directory";
    }

    @Override
    public void scanFile(final IModFile file, final Consumer<Path> pathConsumer) {
        LOGGER.debug(SCAN,"Scanning exploded directory {}", file.getFilePath().toString());
        try (Stream<Path> files = Files.find(file.getSecureJar().getRootPath(), Integer.MAX_VALUE, (p, a) -> p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class"))) {
            files.forEach(pathConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.debug(SCAN,"Exploded directory scan complete {}", file.getFilePath().toString());
    }

    @Override
    public String toString()
    {
        return "{ExplodedDir locator}";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initArguments(final Map<String, ?> arguments) {
        final var explodedTargets = ((Map<String, List<ExplodedMod>>) arguments).get("explodedTargets");
        if (explodedTargets != null && !explodedTargets.isEmpty()) {
            explodedMods.addAll(explodedTargets);
        }
    }

    @Override
    public boolean isValid(final IModFile modFile) {
        return true;
    }
}
