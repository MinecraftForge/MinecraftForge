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

import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public abstract class CommonLaunchHandler implements ILaunchHandlerService {
    public record LocatedPaths(List<Path> minecraftPaths, BiPredicate<String, String> minecraftFilter, List<List<Path>> otherModPaths, List<Path> otherArtifacts) {}

    protected static final Logger LOGGER = LogManager.getLogger();

    public abstract Dist getDist();

    public abstract String getNaming();

    public boolean isProduction() {
        return false;
    }

    public boolean isData() {
        return false;
    }

    public abstract LocatedPaths getMinecraftPaths();

    @Override
    public void configureTransformationClassLoader(final ITransformingClassLoaderBuilder builder) {

    }

    protected final Map<String, List<Path>> getModClasses() {
        final String modClasses = Optional.ofNullable(System.getenv("MOD_CLASSES")).orElse("");
        LOGGER.debug(CORE, "Got mod coordinates {} from env", modClasses);

        record ExplodedModPath(String modid, Path path) {}
        // "a/b/;c/d/;" -> "modid%%c:\fish\pepper;modid%%c:\fish2\pepper2\;modid2%%c:\fishy\bums;modid2%%c:\hmm"
        final var modClassPaths = Arrays.stream(modClasses.split(File.pathSeparator))
                .map(inp -> inp.split("%%", 2))
                .map(splitString -> new ExplodedModPath(splitString.length == 1 ? "defaultmodid" : splitString[0], Paths.get(splitString[splitString.length - 1])))
                .collect(Collectors.groupingBy(ExplodedModPath::modid, Collectors.mapping(ExplodedModPath::path, Collectors.toList())));

        // We need to REVERSE the passed in mod classes so that SJH takes it properly.
        // The last path passed into SecureJar#from is the "main" path used to grab the manifest.
        // ForgeGradle always passed in resources,classes with the expectation in 1.16 or lower that the resources would be checked first.
        // So in 1.17+ FML/SJH, we need to reverse it.
        modClassPaths.forEach((k, v) -> Collections.reverse(v));

        LOGGER.debug(CORE, "Found supplied mod coordinates [{}]", modClassPaths);

        //final var explodedTargets = ((Map<String, List<ExplodedDirectoryLocator.ExplodedMod>>)arguments).computeIfAbsent("explodedTargets", a -> new ArrayList<>());
        //modClassPaths.forEach((modlabel,paths) -> explodedTargets.add(new ExplodedDirectoryLocator.ExplodedMod(modlabel, paths)));
        return modClassPaths;
    }
}
