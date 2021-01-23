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

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public abstract class FMLCommonLaunchHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<String> SKIPPACKAGES = Arrays.asList(
            // standard libs
            "joptsimple.", "org.lwjgl.", "com.mojang.guava.", "com.google.", "org.apache.commons.", "io.netty.",
            "org.apache.logging.log4j.", "org.apache.http.", "org.apache.maven.", "org.objectweb.asm.",
            "paulscode.sound.", "com.ibm.icu.", "sun.", "javax.", "gnu.trove.", "com.electronwill.nightconfig.",
            "net.minecraftforge.fml.loading.", "net.minecraftforge.fml.language.",
            "net.minecraftforge.eventbus.", "net.minecraftforge.api.", "com.mojang.util.QueueLogAppender"
    );

    private final List<Path> additionalLibraries = new ArrayList<>();

    protected Predicate<String> getPackagePredicate() {
        return cn -> SKIPPACKAGES.stream().noneMatch(cn::startsWith);
    }

    public Path getForgePath(final String mcVersion, final String forgeVersion, final String forgeGroup) {
        return LibraryFinder.getForgeLibraryPath(mcVersion, forgeVersion, forgeGroup);
    }

    public Path[] getMCPaths(final String mcVersion, final String mcpVersion, final String forgeVersion, final String forgeGroup) {
        return LibraryFinder.getMCPaths(mcVersion, mcpVersion, forgeVersion, forgeGroup, getDist().isClient() ? "client" : "server");
    }

    public void configureTransformationClassLoader(final ITransformingClassLoaderBuilder builder) {
        builder.addTransformationPath(FMLLoader.getForgePath());
        Arrays.stream(FMLLoader.getMCPaths()).forEach(builder::addTransformationPath);
        additionalLibraries.forEach(builder::addTransformationPath);
        FMLLoader.getLanguageLoadingProvider().getLibraries().forEach(builder::addTransformationPath);
        builder.setManifestLocator(getClassLoaderManifestLocatorFunction());
        builder.setResourceEnumeratorLocator(getClassLoaderResourceEnumerationFunction());
    }

    public void setup(final IEnvironment environment, final Map<String, ?> arguments)
    {

    }

    public abstract Dist getDist();

    protected void beforeStart(ITransformingClassLoader launchClassLoader)
    {
        FMLLoader.beforeStart(launchClassLoader);
    }

    protected void processModClassesEnvironmentVariable(final Map<String, List<Pair<Path, List<Path>>>> arguments) {
        final String modClasses = Optional.ofNullable(System.getenv("MOD_CLASSES")).orElse("");
        LOGGER.debug(CORE, "Got mod coordinates {} from env", modClasses);

        // "a/b/;c/d/;" -> "modid%%c:\fish\pepper;modid%%c:\fish2\pepper2\;modid2%%c:\fishy\bums;modid2%%c:\hmm"
        final Map<String, List<Path>> modClassPaths = Arrays.stream(modClasses.split(File.pathSeparator)).
                map(inp -> inp.split("%%", 2)).map(this::buildModPair).
                collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toList())));

        LOGGER.debug(CORE, "Found supplied mod coordinates [{}]", modClassPaths);

        final List<Pair<Path, List<Path>>> explodedTargets = arguments.computeIfAbsent("explodedTargets", a -> new ArrayList<>());
        modClassPaths.forEach((modlabel,paths) -> explodedTargets.add(Pair.of(paths.get(0), paths.subList(1, paths.size()))));
    }

    private Pair<String, Path> buildModPair(String[] splitString) {
        String modid = splitString.length == 1 ? "defaultmodid" : splitString[0];
        Path path = Paths.get(splitString[splitString.length - 1]);
        return Pair.of(modid, path);
    }

    protected void validatePaths(final Path forgePath, final Path[] mcPaths, String forgeVersion, String mcVersion, String mcpVersion) {
        if (!Files.exists(forgePath)) {
            LOGGER.fatal(CORE, "Failed to find Forge version {} for MC {} at {}", forgeVersion, mcVersion, forgePath);
            throw new RuntimeException("Missing Forge!");
        }

        Stream.of(mcPaths).forEach(p->{
            if (!Files.exists(p)) {
                LOGGER.fatal(CORE, "Failed to find Minecraft resource version {} at {}", mcVersion+"-"+mcpVersion, p);
                throw new RuntimeException("Missing minecraft resource!");
            }
        });

    }


    protected Function<String, Enumeration<URL>> getClassLoaderResourceEnumerationFunction() {
        return input->FMLLoader.getLoadingModList().findAllURLsForResource(input);
    }

    protected Function<URLConnection, Optional<Manifest>> getClassLoaderManifestLocatorFunction() {
        return urlConnection -> {
            if (urlConnection instanceof ModJarURLHandler.ModJarURLConnection) {
                return ((ModJarURLHandler.ModJarURLConnection) urlConnection).getManifest();
            } else {
                return Optional.empty();
            }
        };
    }

    protected abstract String getNaming();

    void addLibraries(final List<ModFile> libraries) {
        libraries
                .stream()
                .map(ModFile::getFilePath)
                .peek(p->LOGGER.debug(LOADING, "Adding {} as a library to the transforming classloader", p))
                .forEach(additionalLibraries::add);
    }

    public boolean isProduction() {
        return false;
    }

    public boolean isData() {
        return false;
    };
}
