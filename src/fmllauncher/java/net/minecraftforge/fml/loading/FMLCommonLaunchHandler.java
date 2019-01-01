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

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

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

    protected Predicate<String> getPackagePredicate() {
        return cn -> SKIPPACKAGES.stream().noneMatch(cn::startsWith);
    }

    public Path getForgePath(final String mcVersion, final String forgeVersion, final String forgeGroup) {
        return LibraryFinder.getForgeLibraryPath(mcVersion, forgeVersion, forgeGroup);
    }

    public Path[] getMCPaths(final String mcVersion, final String mcpVersion, final String forgeVersion, final String forgeGroup) {
        return LibraryFinder.getMCPaths(mcVersion, mcpVersion, forgeVersion, forgeGroup, getDist().isClient() ? "client" : "server");
    }

    public void setup(final IEnvironment environment, final Map<String, ?> arguments)
    {

    }

    public abstract Dist getDist();

    protected void beforeStart(ITransformingClassLoader launchClassLoader)
    {
        FMLLoader.beforeStart(launchClassLoader);
    }

    protected void validatePaths(final Path forgePath, final Path[] mcPaths, String forgeVersion, String mcVersion, String mcpVersion) {
        if (!Files.exists(forgePath)) {
            LOGGER.fatal(CORE, "Failed to find forge version {} for MC {} at {}", forgeVersion, mcVersion, forgePath);
            throw new RuntimeException("Missing forge!");
        }

        Stream.of(mcPaths).forEach(p->{
            if (!Files.exists(p)) {
                LOGGER.fatal(CORE, "Failed to find Minecraft resource version {} at {}", mcVersion+"-"+mcpVersion, p);
                throw new RuntimeException("Missing minecraft resource!");
            }
        });

    }
}
