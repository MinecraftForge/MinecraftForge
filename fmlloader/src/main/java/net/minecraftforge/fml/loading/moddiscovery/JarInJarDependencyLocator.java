/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.EarlyLoadingException;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.ModFileLoadingException;
import net.minecraftforge.jarjar.selection.JarSelector;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiStatus.Internal
public class JarInJarDependencyLocator extends AbstractModProvider implements IDependencyLocator {
    private static final String COLOR_CODE = "\u00a7";
    private static final String RESET  = COLOR_CODE + "r";
    private static final String YELLOW = COLOR_CODE + "e";
    private static final String RED    = COLOR_CODE + "4";
    private static final String GREEN  = COLOR_CODE + "2";

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public String name() {
        return "JarInJar";
    }

    @Override
    public List<IModFile> scanMods(Iterable<IModFile> loadedMods) {
        final List<IModFile> sources = Lists.newArrayList();
        loadedMods.forEach(sources::add);

        var dependenciesToLoad = JarSelector.detectAndSelect(
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
    protected String getDefaultJarModType() {
        return IModFile.Type.GAMELIBRARY.name();
    }

    protected Optional<IModFile> loadModFileFrom(IModFile file, Path path) {
        try {
            final Path pathInModFile = file.findResource(path.toString());
            final URI filePathUri = new URI("jij:" + (pathInModFile.toAbsolutePath().toUri().getRawSchemeSpecificPart())).normalize();
            final Map<String, ?> outerFsArgs = ImmutableMap.of("packagePath", pathInModFile);
            final FileSystem zipFS = FileSystems.newFileSystem(filePathUri, outerFsArgs);
            final Path pathInFS = zipFS.getPath("/");
            final IModFile.Type parentType = file.getType();
            final String modType;
            if (parentType == IModFile.Type.LIBRARY || parentType == IModFile.Type.LANGPROVIDER) {
                modType = IModFile.Type.LIBRARY.name();
            } else {
                modType = IModFile.Type.GAMELIBRARY.name();
            }
            return Optional.of(createMod(pathInFS, modType).file());
        } catch (Exception e) {
            LOGGER.error("Failed to load mod file {} from {}", path, file.getFileName());
            final RuntimeException exception = new ModFileLoadingException("Failed to load mod file " + file.getFileName());
            exception.initCause(e);
            throw exception;
        }
    }

    protected EarlyLoadingException exception(Collection<JarSelector.ResolutionFailureInformation<IModFile>> failedDependencies) {
        final List<EarlyLoadingException.ExceptionData> errors = failedDependencies.stream()
               .filter(entry -> !entry.sources().isEmpty()) //Should never be the case, but just to be sure
               .map(this::buildExceptionData)
               .toList();

        return new EarlyLoadingException(failedDependencies.size() + " Dependency restrictions were not met.", null, errors);
    }

    @NotNull
    private EarlyLoadingException.ExceptionData buildExceptionData(JarSelector.ResolutionFailureInformation<IModFile> entry) {
        return new EarlyLoadingException.ExceptionData(
                getErrorTranslationKey(entry),
                entry.identifier().group() + ":" + entry.identifier().artifact(),
                entry.sources()
                     .stream()
                     .flatMap(this::getModWithVersionRangeStream)
                     .map(this::formatError)
                     .collect(Collectors.joining(", "))
        );
    }

    @NotNull
    private String getErrorTranslationKey(JarSelector.ResolutionFailureInformation<IModFile> entry) {
        return entry.failureReason() == JarSelector.FailureReason.VERSION_RESOLUTION_FAILED ?
                       "fml.dependencyloading.conflictingdependencies" :
                       "fml.dependencyloading.mismatchedcontaineddependencies";
    }

    @NotNull
    private Stream<ModWithVersionRange> getModWithVersionRangeStream(JarSelector.SourceWithRequestedVersionRange<IModFile> file) {
        return file.sources()
                   .stream()
                   .map(IModFile::getModFileInfo)
                   .flatMap(modFileInfo -> modFileInfo.getMods().stream())
                   .map(modInfo -> new ModWithVersionRange(modInfo, file.requestedVersionRange(), file.includedVersion()));
    }

    protected Optional<InputStream> loadResourceFromModFile(IModFile modFile, Path path) {
        try {
            var pathInModFile = modFile.findResource(path.toString());
            if (!Files.exists(pathInModFile)) {
                LOGGER.debug("Failed to load resource {} from {}, it does not contain dependency information.", path, modFile.getFileName());
                return Optional.empty();
            }
            return Optional.of(Files.newInputStream(pathInModFile));
        } catch (Exception e) {
            LOGGER.error("Failed to load resource {} from mod {}, cause {}", path, modFile.getFileName(), e);
            return Optional.empty();
        }
    }

    @NotNull
    private String formatError(ModWithVersionRange modWithVersionRange){
        return YELLOW + modWithVersionRange.modInfo().getModId() + RESET + " - " +
               RED + modWithVersionRange.versionRange().toString() + RESET + " - " +
               GREEN + modWithVersionRange.artifactVersion().toString() + RESET;
    }

    protected String identifyMod(IModFile modFile) {
        if (modFile.getModFileInfo() != null && !modFile.getModInfos().isEmpty())
            return modFile.getModInfos().stream().map(IModInfo::getModId).collect(Collectors.joining());

        var module = modFile.getSecureJar().moduleDataProvider().name();
        if (module != null && !module.isEmpty())
            return module;

        return modFile.getFileName();
    }

    private record ModWithVersionRange(IModInfo modInfo, VersionRange versionRange, ArtifactVersion artifactVersion) {}
}
