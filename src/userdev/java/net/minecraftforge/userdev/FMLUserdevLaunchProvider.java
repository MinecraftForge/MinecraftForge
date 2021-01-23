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

package net.minecraftforge.userdev;

import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.loading.LibraryFinder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public abstract class FMLUserdevLaunchProvider extends FMLCommonLaunchHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private Path forgeJar;
    private Path mcJars;
    private static final String FORGE_VERSION_CLASS = "net/minecraftforge/versions/forge/ForgeVersion.class";

    @Override
    public Path getForgePath(final String mcVersion, final String forgeVersion, final String forgeGroup) {
        final URL forgePath = getClass().getClassLoader().getResource(FORGE_VERSION_CLASS);
        if (forgePath == null) {
            LOGGER.fatal(CORE, "Unable to locate Forge on the classpath");
            throw new RuntimeException("Unable to locate forge on the classpath");
        }
        forgeJar = LibraryFinder.findJarPathFor(FORGE_VERSION_CLASS, "forge", forgePath);
        return forgeJar;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setup(final IEnvironment environment, final Map<String, ?> arguments) {
        if (!forgeJar.getFileName().toString().endsWith(".jar")) {
            LOGGER.fatal(CORE, "Userdev Launcher attempted to be used with non-jar version of Forge: {}", forgeJar);
            throw new RuntimeException("Userdev Launcher can only be used with dev-jar version of Forge");
        }

        final List<String> mavenRoots = new ArrayList<>((List<String>) arguments.get("mavenRoots"));
        final String forgeGroup = (String) arguments.get("forgeGroup");
        int dirs = forgeGroup.split("\\.").length + 2;

        Path fjroot = forgeJar;
        do {
            fjroot = fjroot.getParent();
        } while (dirs-- > 0);
        final String fjpath = fjroot.toString();
        mavenRoots.add(fjpath);
        LOGGER.debug(CORE, "Injecting maven path {}", fjpath);

        processModClassesEnvironmentVariable((Map<String, List<Pair<Path, List<Path>>>>) arguments);

        // generics are gross yea?
        ((Map)arguments).put("mavenRoots", mavenRoots);
    }

    @Override
    protected void validatePaths(final Path forgePath, final Path[] mcPaths, final String forgeVersion, final String mcVersion, final String mcpVersion) {

    }

    @Override
    public Path[] getMCPaths(final String mcVersion, final String mcpVersion, final String forgeVersion, final String forgeGroup) {
        final URL mcDataPath = getClass().getClassLoader().getResource("assets/minecraft/lang/en_us.json");
        if (mcDataPath == null) {
            LOGGER.fatal(CORE, "Unable to locate minecraft data on the classpath");
            throw new RuntimeException("Unable to locate minecraft data on the classpath");
        }
        mcJars = LibraryFinder.findJarPathFor("en_us.json","mcdata", mcDataPath);
        return new Path[] {mcJars};
    }

    @Override
    protected String getNaming() {
        return "mcp";
    }

}
