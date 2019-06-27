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

package net.minecraftforge.userdev;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

public class ClasspathLocator extends AbstractJarFileLocator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MODS_TOML = "META-INF/mods.toml";
    private List<Path> modCoords;

    @Override
    public List<ModFile> scanMods() {
        return modCoords.stream().
            map(mc -> new ModFile(mc, this)).
            peek(f->modJars.compute(f, (mf, fs)->createFileSystem(mf))).
            collect(Collectors.toList());
    }

    @Override
    public String name() {
        return "userdev classpath";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {
        try {
            modCoords = new ArrayList<>();
            final Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources(MODS_TOML);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                Path path = LibraryFinder.findJarPathFor(MODS_TOML, "classpath_mod", url);
                if (Files.isDirectory(path))
                    continue;

                LOGGER.debug(CORE, "Found classpath mod: {}", path);
                this.modCoords.add(path);
            }
        } catch (IOException e) {
            LOGGER.fatal(CORE,"Error trying to find resources", e);
            throw new RuntimeException("wha?", e);
        }
    }
}
