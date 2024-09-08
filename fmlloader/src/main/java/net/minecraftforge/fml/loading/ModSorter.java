/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.mojang.logging.LogUtils;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.loading.EarlyLoadingException.ExceptionData;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.loading.toposort.CyclePresentException;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class ModSorter
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private final UniqueModListBuilder uniqueModListBuilder;
    private List<ModFile> modFiles;
    private List<ModInfo> sortedList;
    private Map<String, IModInfo> modIdNameLookup;
    private List<ModFile> systemMods;

    private ModSorter(final List<ModFile> modFiles)
    {
        this.uniqueModListBuilder = new UniqueModListBuilder(modFiles);
    }

    public static LoadingModList sort(List<ModFile> mods, final List<ExceptionData> errors)
    {
        final ModSorter ms = new ModSorter(mods);
        try {
            ms.buildUniqueList();
        } catch (EarlyLoadingException e) {
            // We cannot build any list with duped mods. We have to abort immediately and report it
            // Note this will never actually throw an error because the duplicate checks are done in ModDiscovererer before we get to this phase
            // So all this is really doing is wasting time.
            // But i'm leaving it here until I rewrite all of cpw's mod loading code because its such a clusterfuck.
            return LoadingModList.of(ms.systemMods, ms.systemMods.stream().map(mf->(ModInfo)mf.getModInfos().getFirst()).toList(), e);
        }
        // try and validate dependencies
        final List<ExceptionData> failedList = Stream.concat(ms.verifyDependencyVersions().stream(), errors.stream()).toList();
        // if we miss one or the other, we abort now
        if (!failedList.isEmpty()) {
            return LoadingModList.of(ms.systemMods, ms.systemMods.stream().map(mf->(ModInfo)mf.getModInfos().getFirst()).toList(), new EarlyLoadingException("failure to validate mod list", null, failedList));
        } else {
            // Otherwise, lets try and sort the modlist and proceed
            EarlyLoadingException earlyLoadingException = null;
            try {
                ms.sort();
            } catch (EarlyLoadingException e) {
                earlyLoadingException = e;
            }
            return LoadingModList.of(ms.modFiles, ms.sortedList, earlyLoadingException);
        }
    }

    private void sort()
    {
        // lambdas are identity based, so sorting them is impossible unless you hold reference to them
        final MutableGraph<ModFileInfo> graph = GraphBuilder.directed().build();
        AtomicInteger counter = new AtomicInteger();
        Map<ModFileInfo, Integer> infos = modFiles.stream()
                .map(ModFile::getModFileInfo)
                .filter(ModFileInfo.class::isInstance)
                .map(ModFileInfo.class::cast)
                .collect(toMap(Function.identity(), e -> counter.incrementAndGet()));
        infos.keySet().forEach(graph::addNode);
        modFiles.stream()
                .map(ModFile::getModInfos)
                .flatMap(List::stream)
                .map(IModInfo::getDependencies)
                .<IModInfo.ModVersion>flatMap(List::stream)
                .forEach(dep -> addDependency(graph, dep));

        final List<ModFileInfo> sorted;
        try
        {
            sorted = TopologicalSort.topologicalSort(graph, Comparator.comparing(infos::get));
        }
        catch (CyclePresentException e)
        {
            Set<Set<ModFileInfo>> cycles = e.getCycles();
            if (LOGGER.isErrorEnabled(LOADING))
            {
                LOGGER.error(LOADING, "Mod Sorting failed.\nDetected Cycles: {}\n", cycles);
            }
            var dataList = cycles.stream()
                    .flatMap(Set::stream)
                    .<IModInfo>mapMulti((mf,c)->mf.getMods().forEach(c))
                    .map(IModInfo::getModId)
                    .map(list -> new ExceptionData("fml.modloading.cycle", list))
                    .toList();
            throw new EarlyLoadingException("Sorting error", e, dataList);
        }
        this.sortedList = sorted.stream()
                .map(ModFileInfo::getMods)
                .flatMap(List::stream)
                .map(ModInfo.class::cast)
                .collect(toList());
        this.modFiles = sorted.stream()
                .map(ModFileInfo::getFile)
                .collect(toList());
    }

    private void addDependency(MutableGraph<ModFileInfo> topoGraph, IModInfo.ModVersion dep)
    {
        final IModInfo targetModInfo = modIdNameLookup.get(dep.getModId());
        // soft dep that doesn't exist. Just return. No edge required.
        if (targetModInfo == null || !(targetModInfo.getOwningFile() instanceof final ModFileInfo target)) return;

        final ModFileInfo self = (ModFileInfo)dep.getOwner().getOwningFile();
        if (self == target)
            return; // in case a jar has two mods that have dependencies between

        switch (dep.getOrdering()) {
            case BEFORE -> topoGraph.putEdge(self, target);
            case AFTER -> topoGraph.putEdge(target, self);
            default -> {}
        }
    }

    private void buildUniqueList()
    {
        final UniqueModListBuilder.UniqueModListData uniqueModListData = uniqueModListBuilder.buildUniqueList();

        this.modFiles = uniqueModListData.modFiles();

        detectSystemMods(uniqueModListData.modFilesByFirstId());

        modIdNameLookup = uniqueModListData.modFilesByFirstId().entrySet().stream()
                .filter(e -> !e.getValue().getFirst().getModInfos().isEmpty())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().getFirst().getModInfos().getFirst()
                  ));
    }

    private void detectSystemMods(final Map<String, List<ModFile>> modFilesByFirstId) {
        // Capture system mods (ex. MC, Forge) here, so we can keep them for later
        var systemMods = List.of("minecraft", "forge");
        LOGGER.debug("Configured system mods: {}", systemMods);

        this.systemMods = new ArrayList<>();
        for (var systemMod : systemMods) {
            var container = modFilesByFirstId.get(systemMod);
            if (container != null && !container.isEmpty()) {
                LOGGER.debug("Found system mod: {}", systemMod);
                this.systemMods.add(container.getFirst());
            } else {
                throw new IllegalStateException("Failed to find system mod: " + systemMod);
            }
        }
    }

    private List<EarlyLoadingException.ExceptionData> verifyDependencyVersions()
    {
        final var modVersions = modFiles.stream()
                .map(ModFile::getModInfos)
                .flatMap(List::stream)
                .collect(toMap(IModInfo::getModId, IModInfo::getVersion));

        final var modVersionDependencies = modFiles.stream()
                .map(ModFile::getModInfos)
                .flatMap(List::stream)
                .collect(groupingBy(Function.identity(), flatMapping(e -> e.getDependencies().stream(), toList())));

        final var modRequirements = modVersionDependencies.values().stream()
                .<IModInfo.ModVersion>flatMap(List::stream)
                .filter(mv -> mv.getSide().isCorrectSide())
                .collect(toSet());

        final long mandatoryRequired = modRequirements.stream().filter(IModInfo.ModVersion::isMandatory).count();
        LOGGER.debug(LOADING, "Found {} mod requirements ({} mandatory, {} optional)", modRequirements.size(), mandatoryRequired, modRequirements.size() - mandatoryRequired);
        final var missingVersions = modRequirements.stream()
                .filter(mv -> (mv.isMandatory() || modVersions.containsKey(mv.getModId())) && !modVersionContained(mv, modVersions))
                .collect(toSet());
        final long mandatoryMissing = missingVersions.stream().filter(IModInfo.ModVersion::isMandatory).count();
        LOGGER.debug(LOADING, "Found {} mod requirements missing ({} mandatory, {} optional)", missingVersions.size(), mandatoryMissing, missingVersions.size() - mandatoryMissing);

        if (!missingVersions.isEmpty()) {
            if (mandatoryMissing > 0) {
                LOGGER.error(
                        LOADING,
                        "Missing or unsupported mandatory dependencies:\n{}",
                        missingVersions.stream()
                                .filter(IModInfo.ModVersion::isMandatory)
                                .map(ver -> formatDependencyError(ver, modVersions))
                                .collect(Collectors.joining("\n"))
                );
            }
            if (missingVersions.size() - mandatoryMissing > 0) {
                LOGGER.error(
                        LOADING,
                        "Unsupported installed optional dependencies:\n{}",
                        missingVersions.stream()
                                .filter(ver -> !ver.isMandatory())
                                .map(ver -> formatDependencyError(ver, modVersions))
                                .collect(Collectors.joining("\n"))
                );
            }

            return missingVersions.stream()
                    .map(mv -> new ExceptionData(mv.isMandatory() ? "fml.modloading.missingdependency" : "fml.modloading.missingdependency.optional",
                            mv.getOwner(), mv.getModId(), mv.getOwner().getModId(), mv.getVersionRange(),
                            modVersions.getOrDefault(mv.getModId(), new DefaultArtifactVersion("null"))))
                    .toList();
        }
        return Collections.emptyList();
    }

    private static String formatDependencyError(IModInfo.ModVersion dependency, Map<String, ArtifactVersion> modVersions)
    {
        ArtifactVersion installed = modVersions.get(dependency.getModId());
        return String.format(
                "\tMod ID: '%s', Requested by: '%s', Expected range: '%s', Actual version: '%s'",
                dependency.getModId(),
                dependency.getOwner().getModId(),
                dependency.getVersionRange(),
                installed != null ? installed.toString() : "[MISSING]"
        );
    }

    private static boolean modVersionContained(IModInfo.ModVersion mv, Map<String, ArtifactVersion> modVersions) {
        var modId = mv.getModId();
        var range = mv.getVersionRange();
        if (modVersions.containsKey(modId)
                && (range.containsVersion(modVersions.get(modId)) || modVersions.get(modId).toString().equals("0.0NONE")))
            return true;

        return VersionSupportMatrix.testVersionSupportMatrix(mv.getVersionRange(), mv.getModId(), "mod");
    }
}
