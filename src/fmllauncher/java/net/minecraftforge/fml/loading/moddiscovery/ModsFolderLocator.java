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

import com.google.common.collect.Lists;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

/**
 * Support loading mods located in JAR files in the mods folder
 */
public class ModsFolderLocator extends AbstractJarFileLocator {
    private static final String SUFFIX = ".jar";
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Path> modFolders;

    public ModsFolderLocator() {
        this(FMLPaths.MODSDIR.get());
    }

    ModsFolderLocator(Path modFolder) {
        super();
        this.modFolders = Lists.newArrayList(new Path[]{modFolder}); //Path is iterable, so just putting modFolder there makes it iterate over all path entries
    }

    @Override
    public List<ModFile> scanMods() {
        LOGGER.debug(SCAN,"Scanning mods dirs {} for mods", this.modFolders.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator)));
        return  modFolders.stream().flatMap(x -> {
                    try {
                        return Files.list(x);
                    } catch (IOException e) {
                        LOGGER.warn(SCAN, "Potential mods dir {} does not exist, ignoring", x.toString());
                        return Stream.empty();
                    }
                }).
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
        return "{ModJarsFolder locator at "+this.modFolders.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator))+"}";
    }

    @Override
    public void initArguments(final Map<String, ?> arguments) {
        modFolders.addAll(modFolders.stream().map(p->p.resolve((String)arguments.get("mcVersion"))).collect(Collectors.toList()));
    }
}
