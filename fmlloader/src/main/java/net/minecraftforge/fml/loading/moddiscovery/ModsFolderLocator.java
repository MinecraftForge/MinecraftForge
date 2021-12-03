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

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import net.minecraftforge.fml.loading.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.uncheck;

/**
 * Support loading mods located in JAR files in the mods folder
 */
public class ModsFolderLocator extends AbstractJarFileLocator {
    private static final String SUFFIX = ".jar";
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path modFolder;
    private final String customName;

    public ModsFolderLocator() {
        this(FMLPaths.MODSDIR.get());
    }

    ModsFolderLocator(Path modFolder) {
        this(modFolder, "mods folder");
    }

    ModsFolderLocator(Path modFolder, String name) {
        this.modFolder = modFolder;
        this.customName = name;
    }

    @Override
    public Stream<Path> scanCandidates() {
        LOGGER.debug(LogMarkers.SCAN,"Scanning mods dir {} for mods", this.modFolder);
        var excluded = ModDirTransformerDiscoverer.allExcluded();

        return uncheck(()-> Files.list(this.modFolder))
                .filter(p-> !excluded.contains(p) && StringUtils.toLowerCase(p.getFileName().toString()).endsWith(SUFFIX))
                .sorted(Comparator.comparing(path-> StringUtils.toLowerCase(path.getFileName().toString())));
    }

    @Override
    public String name() {
        return customName;
    }

    @Override
    public String toString() {
        return "{"+customName+" locator at "+this.modFolder+"}";
    }

    @Override
    public void initArguments(final Map<String, ?> arguments) {
    }
}
