/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import com.google.common.collect.ObjectArrays;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static net.minecraftforge.fml.Logging.CORE;

public abstract class FMLCommonLaunchHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<String> SKIPPACKAGES = Arrays.asList(
            // standard libs
            "joptsimple.", "org.lwjgl.", "com.mojang.guava.", "com.google.", "org.apache.commons.", "io.netty.",
            "org.apache.logging.log4j.", "org.apache.http.", "org.apache.maven.", "org.objectweb.asm.",
            "paulscode.sound.", "com.ibm.icu.", "sun.", "gnu.trove.", "com.electronwill.nightconfig.",
            "net.minecraftforge.fml.loading.", "net.minecraftforge.fml.language.", "net.minecraftforge.versions.",
            "net.minecraftforge.eventbus.", "net.minecraftforge.api."
    );

    private Path forgePath;

    protected Predicate<String> getPackagePredicate() {
        return cn -> SKIPPACKAGES.stream().noneMatch(cn::startsWith);
    }

    public void setup(final IEnvironment environment)
    {

    }

    Path findLibsPath() {
        final Path asm = findJarPathFor("org/objectweb/asm/Opcodes.class", "asm");
        // go up SIX parents to find the libs dir
        final Path libs = asm.getParent().getParent().getParent().getParent().getParent().getParent();
        LOGGER.debug(CORE, "Found probable library path {}", libs);
        return libs;
    }
    Path findJarPathFor(final String className, final String jarName) {
        final URL resource = getClass().getClassLoader().getResource(className);
        try {
            Path path;
            final URI uri = resource.toURI();
            if (uri.getSchemeSpecificPart().contains("!")) {
                path = Paths.get(new URI(uri.getSchemeSpecificPart().split("!")[0]));
            } else {
                path = Paths.get(new URI("file:///"+uri.getSchemeSpecificPart().substring(0, uri.getSchemeSpecificPart().length()-className.length())));
            }
            LOGGER.debug(CORE, "Found JAR {} at path {}", jarName, path.toString());
            return path;
        } catch (URISyntaxException e) {
            LOGGER.error(CORE, "Failed to find JAR for class {} - {}", className, jarName);
            throw new RuntimeException("Unable to locate "+className+" - "+jarName, e);
        }
    }
    Path[] commonLibPaths(Path[] extras) {
        final Path realms = findJarPathFor("com/mojang/realmsclient/RealmsVersion.class", "realms");
        return ObjectArrays.concat(extras, realms);
    }

    Path getForgePath() {
        if (forgePath == null) {
            forgePath = findJarPathFor("net/minecraftforge/versions/forge/ForgeVersion.class", "forge");
            LOGGER.debug(CORE, "Found forge path {}", forgePath);
        }
        return forgePath;
    }
    public abstract Dist getDist();

    protected void beforeStart(ITransformingClassLoader launchClassLoader)
    {
        FMLLoader.beforeStart(launchClassLoader, getForgePath());
    }
}
