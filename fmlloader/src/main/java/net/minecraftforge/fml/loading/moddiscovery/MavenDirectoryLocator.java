/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.MavenCoordinateResolver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MavenDirectoryLocator extends AbstractJarFileModLocator
{
    private List<Path> modCoords;

    @Override
    public Stream<Path> scanCandidates() {
        return modCoords.stream();
    }

    @Override
    public String name() {
        return "maven libs";
    }

    public String toString() {
        return "{Maven Directory locator for mods "+this.modCoords+"}";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initArguments(final Map<String, ?> arguments) {
        final List<String> mavenRoots = (List<String>) arguments.get("mavenRoots");
        final List<Path> mavenRootPaths = mavenRoots.stream().map(n -> FMLPaths.GAMEDIR.get().resolve(n)).collect(Collectors.toList());
        final List<String> mods = (List<String>) arguments.get("mods");
        final List<String> listedMods = ModListHandler.processModLists((List<String>) arguments.get("modLists"), mavenRootPaths);

        List<Path> localModCoords = Stream.concat(mods.stream(),listedMods.stream()).map(MavenCoordinateResolver::get).collect(Collectors.toList());
        // find the modCoords path in each supplied maven path, and turn it into a mod file. (skips not found files)

        this.modCoords = localModCoords.stream().map(mc -> mavenRootPaths.stream().map(root -> root.resolve(mc)).filter(path -> Files.exists(path)).findFirst().orElseThrow(() -> new IllegalArgumentException("Failed to locate requested mod coordinate " + mc))).collect(Collectors.toList());
    }
}
