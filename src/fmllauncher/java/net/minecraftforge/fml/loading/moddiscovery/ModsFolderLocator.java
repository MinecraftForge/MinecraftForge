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

import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cpw.mods.modlauncher.api.LamdbaExceptionUtils.uncheck;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

/**
 * Support loading mods located in JAR files in the mods folder
 */
public class ModsFolderLocator extends AbstractJarFileLocator {
    private static final String SUFFIX = ".jar";
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path modFolder;

    public ModsFolderLocator() {
        this(FMLPaths.MODSDIR.get());
    }

    ModsFolderLocator(Path modFolder) {
        super();
        this.modFolder = modFolder;
    }

    @Override
    public List<ModFile> scanMods() {
        LOGGER.debug(SCAN,"Scanning mods dir {} for mods", this.modFolder);
        List<Path> excluded = new ModDirTransformerDiscoverer().candidates(FMLPaths.GAMEDIR.get());
        return uncheck(()-> Files.list(this.modFolder)).
                filter(p->!excluded.contains(p)).
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

    @Override
    public String toString() {
        return "{ModJarsFolder locator at "+this.modFolder+"}";
    }

    @Override
    public void initArguments(final Map<String, ?> arguments) {

    }
}
