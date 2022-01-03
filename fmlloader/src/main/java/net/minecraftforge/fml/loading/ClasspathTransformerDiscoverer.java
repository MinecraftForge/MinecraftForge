/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.NamedPath;
import cpw.mods.modlauncher.serviceapi.ITransformerDiscoveryService;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ClasspathTransformerDiscoverer implements ITransformerDiscoveryService {

    private final List<Path> legacyClasspath = Arrays.stream(System.getProperty("legacyClassPath", "").split(File.pathSeparator)).map(Path::of).toList();

    @Override
    public List<NamedPath> candidates(Path gameDirectory) {
        return Collections.emptyList();
    }

    @Override
    public List<NamedPath> candidates(final Path gameDirectory, final String launchTarget) {
        if (launchTarget != null && launchTarget.contains("dev")) {
            this.scan(gameDirectory);
        }
        return List.copyOf(found);
    }

    private final static List<NamedPath> found = new ArrayList<>();

    public static List<Path> allExcluded() {
        return found.stream().map(np->np.paths()[0]).toList();
    }
    
    private void scan(final Path gameDirectory) {
        try {
            locateTransformers("META-INF/services/cpw.mods.modlauncher.api.ITransformationService");
            locateTransformers("META-INF/services/net.minecraftforge.forgespi.locating.IModLocator");
        } catch (IOException e) {
            LogManager.getLogger().error("Error during discovery of transform services from the classpath", e);
        }
    }

    private void locateTransformers(String resource) throws IOException {
        final Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources(resource);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Path path = ClasspathLocatorUtils.findJarPathFor(resource, url.toString(), url);
            if (legacyClasspath.stream().anyMatch(path::equals) || !Files.exists(path) || Files.isDirectory(path))
                continue;
            found.add(new NamedPath(path.toUri().toString(), path));
        }
    }
}
