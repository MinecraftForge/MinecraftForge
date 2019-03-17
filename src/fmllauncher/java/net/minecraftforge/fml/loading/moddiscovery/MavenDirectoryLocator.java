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

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.MavenCoordinateResolver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MavenDirectoryLocator extends AbstractJarFileLocator {
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
