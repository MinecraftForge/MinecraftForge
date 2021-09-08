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

import cpw.mods.jarhandling.SecureJar;
import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class ClasspathLocator extends AbstractJarFileLocator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MODS_TOML = "META-INF/mods.toml";
    private static final String MANIFEST = "META-INF/MANIFEST.MF";
    private final List<String> ignoreList = Arrays.stream(System.getProperty("ignoreList", "").split(",")).toList();
    private boolean enabled = false;

    @Override
    public String name() {
        return "userdev classpath";
    }

    @Override
    public List<IModFile> scanMods() {
        if (!enabled)
            return List.of();
        try {
            var modCoords = Stream.<IModFile>builder();
            locateMods(modCoords, MODS_TOML, "classpath_mod", sj -> true);
            locateMods(modCoords, MANIFEST, "manifest_jar", sj -> isValidManifest(sj) && sj.getManifest().getMainAttributes().getValue(ModFile.TYPE) != null);
            return modCoords.build().toList();
        } catch (IOException e) {
            LOGGER.fatal(CORE, "Error trying to find resources", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<Path> scanCandidates() {
        return Stream.of();
    }

    private void locateMods(Stream.Builder<IModFile> modCoords, String resource, String name, Predicate<SecureJar> filter) throws IOException {
        final Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources(resource);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Path path = LibraryFinder.findJarPathFor(resource, name, url);
            String filename = path.getFileName().toString();
            if (ignoreList.stream().anyMatch(filename::startsWith) || Files.isDirectory(path))
                continue;

            ModJarMetadata.buildFile(this, filter, path).ifPresent(mf -> {
                LOGGER.debug(CORE, "Found classpath mod: {}", path);
                modCoords.add(mf);
            });
        }
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {
        var launchTarget = (String) arguments.get("launchTarget");
        enabled = launchTarget != null && launchTarget.contains("dev");
    }

    private static boolean isValidManifest(SecureJar sj) {
        try (var jis = new JarInputStream(Files.newInputStream(sj.getRootPath()))) {
            return jis.getManifest() != null;
        } catch (IOException e) {
            return false;
        }
    }
}
