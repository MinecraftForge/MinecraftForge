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

package net.minecraftforge.fml.loading.targets;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.VersionInfo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CommonUserdevLaunchHandler extends CommonDevLaunchHandler {
    @Override
    public LocatedPaths getMinecraftPaths() {
        final var vers = FMLLoader.versionInfo();

        // Minecraft is extra jar {resources} + forge jar {patches}
        final var mcstream = Stream.<Path>builder();
        // Mod code is in exploded directories
        final var modstream = Stream.<List<Path>>builder();

        // The MC extra and forge jars are on the classpath, so try and pull them out
        var legacyCP = Objects.requireNonNull(System.getProperty("legacyClassPath"), "Missing legacyClassPath, cannot find userdev jars").split(File.pathSeparator);
        var extra = findJarOnClasspath(legacyCP, "client-extra");

        processStreams(legacyCP, vers, mcstream, modstream);
        getModClasses().forEach((modid, paths) -> modstream.add(paths));

        var minecraft = mcstream.build().collect(Collectors.toList());
        var mcFilter = getMcFilter(extra, minecraft, modstream);
        minecraft.add(extra); // Add extra late so the filter is made correctly
        return new LocatedPaths(minecraft, mcFilter, modstream.build().toList(), getFmlStuff(legacyCP));
    }

    protected abstract void processStreams(String[] classpath, VersionInfo versionInfo, Stream.Builder<Path> mc, Stream.Builder<List<Path>> mods);

    protected static Path findJarOnClasspath(String[] classpath, String match) {
        return Paths.get(Arrays.stream(classpath).filter(e -> e.contains(match)).findFirst().orElseThrow(() -> new IllegalStateException("Could not find " + match + " in classpath")));
    }
}
