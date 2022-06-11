/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraftforge.fml.loading.EarlyLoadingException;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.jarjar.metadata.ContainedJarIdentifier;
import net.minecraftforge.jarjar.selection.JarSelector;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JarInJarDependencyLocator extends AbstractJarFileDependencyLocator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JarInJarDependencyLocator.class);

    @Override
    public String name()
    {
        return "JarInJar";
    }

    @Override
    public List<IModFile> scanMods(final Iterable<IModFile> loadedMods) {
        final List<IModFile> sources = Lists.newArrayList();
        loadedMods.forEach(sources::add);


        final List<IModFile> dependenciesToLoad = JarSelector.detectAndSelect(
          sources,
          this::loadResourceFromModFile,
          this::loadModFileFrom,
          this::identifyMod,
          this::exception
        );

        if (dependenciesToLoad.isEmpty()) {
            LOGGER.info("No dependencies to load found. Skipping!");
            return Collections.emptyList();
        }

        LOGGER.info("Found {} dependencies adding them to mods collection", dependenciesToLoad.size());
        return dependenciesToLoad;
    }

    @Override
    public void initArguments(final Map<String, ?> arguments)
    {
        // NO-OP, for now
    }

    @Override
    protected String getDefaultJarModType()
    {
        return IModFile.Type.GAMELIBRARY.name();
    }

    protected EarlyLoadingException exception(Collection<JarSelector.ResolutionFailureInformation<IModFile>> failedDependencies)
    {
        record ModWithVersionRange(IModInfo modInfo, VersionRange versionRange, ArtifactVersion artifactVersion) {}

        final List<EarlyLoadingException.ExceptionData> errors = failedDependencies.stream()
                                                                   .filter(entry -> !entry.sources().isEmpty()) //Should never be the case, but just to be sure
                                                                   .map(entry -> new EarlyLoadingException.ExceptionData(
                                                                     entry.failureReason() == JarSelector.FailureReason.VERSION_RESOLUTION_FAILED ? "fml.dependencyloading.conflictingdependencies" : "fml.dependencyloading.mismatchedcontaineddependencies",
                                                                     entry.identifier().group() + ":" + entry.identifier().artifact(),
                                                                     entry.sources()
                                                                       .stream()
                                                                       .flatMap(file -> file.source().getModFileInfo().getMods().stream().map(modInfo -> new ModWithVersionRange(modInfo, file.requestedVersionRange(), file.includedVersion())))
                                                                       .map(modWithVersionRange -> "\u00a7e" + modWithVersionRange.modInfo().getModId() + "\u00a7r - \u00a74" + modWithVersionRange.versionRange().toString() + "\u00a74 - \u00a72" + modWithVersionRange.artifactVersion().toString() + "\u00a72")
                                                                       .collect(Collectors.joining(", ")))).toList();

        return new EarlyLoadingException(failedDependencies.size() + " Dependency restrictions were not met.", null, errors);
    }
}
