/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.VersionInfo;

import java.io.File;
import java.nio.file.Path;
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
}
