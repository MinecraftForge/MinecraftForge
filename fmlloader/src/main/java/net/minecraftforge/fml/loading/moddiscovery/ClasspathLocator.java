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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class ClasspathLocator extends AbstractModLocator
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String USERDEV_CLASSPATH = "userdev classpath";
    private final List<Path> legacyClasspath = Arrays.stream(System.getProperty("legacyClassPath", "").split(File.pathSeparator)).map(Path::of).toList();
    private boolean enabled = false;

    public ClasspathLocator() {super(USERDEV_CLASSPATH);}

    @Override
    public Stream<Path> scanCandidates() {
        if (!enabled)
            return Stream.of();

        try {
            var claimed = new ArrayList<>(legacyClasspath);
            var paths = Stream.<Path>builder();

            findPaths(claimed, MODS_TOML).forEach(paths::add);
            findPaths(claimed, MANIFEST).forEach(paths::add);

            return paths.build();
        } catch (IOException e) {
            LOGGER.fatal(SCAN, "Error trying to find resources", e);
            throw new RuntimeException(e);
        }
    }

    private List<Path> findPaths(List<Path> claimed, String resource) throws IOException {
        var ret = new ArrayList<Path>();
        final Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources(resource);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Path path = findJarPathFor(resource, resource, url);
            if (claimed.stream().anyMatch(path::equals) || !Files.exists(path) || Files.isDirectory(path))
                continue;
            ret.add(path);
        }
        return ret;
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {
        var launchTarget = (String) arguments.get("launchTarget");
        enabled = launchTarget != null && launchTarget.contains("dev");
    }

    private Path findJarPathFor(final String resourceName, final String jarName, final URL resource) {
        try {
            Path path;
            final URI uri = resource.toURI();
            if (uri.getScheme().equals("jar") && uri.getRawSchemeSpecificPart().contains("!/")) {
                int lastExcl = uri.getRawSchemeSpecificPart().lastIndexOf("!/");
                path = Paths.get(new URI(uri.getRawSchemeSpecificPart().substring(0, lastExcl)));
            } else {
                path = Paths.get(new URI("file://"+uri.getRawSchemeSpecificPart().substring(0, uri.getRawSchemeSpecificPart().length()-resourceName.length())));
            }
            //LOGGER.debug(CORE, "Found JAR {} at path {}", jarName, path.toString());
            return path;
        } catch (NullPointerException | URISyntaxException e) {
            LOGGER.fatal(SCAN, "Failed to find JAR for class {} - {}", resourceName, jarName);
            throw new RuntimeException("Unable to locate "+resourceName+" - "+jarName, e);
        }
    }
}
