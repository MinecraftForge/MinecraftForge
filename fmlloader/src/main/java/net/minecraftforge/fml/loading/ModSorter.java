/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.mojang.logging.LogUtils;
import net.minecraftsingularity.singularityspi.language.IModInfo.ModVersion;
import net.minecraftsingularity.fml.loading.EarlyLoadingException.ExceptionData;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFile;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftsingularity.fml.loading.moddiscovery.ModInfo;
import net.minecraftsingularity.fml.loading.toposort.CyclePresentException;
import net.minecraftsingularity.fml.loading.toposort.TopologicalSort;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftsingularity.fml.loading.LogMarkers.LOADING;

public final class ModSorter {
    private static final Logger LOGGER = LogUtils.getLogger();

    record State(List<ModFile> files, List<ModInfo> mods) {}

    private ModSorter() {}

    @SuppressWarnings("removal")
    public static LoadingModList sort(List<ModFile> mods, final List<ExceptionData> errors) {
        State systemMods = detectSystemMods(mods);
        List<ModFile> modFiles;

        try {
            modFiles = UniqueModListBuilder.buildUniqueList(mods).modFiles();
        } catch (EarlyLoadingException e) {
            // We cannot build any list with duped mods. We have to abort immediately and report it
            // Note this will never actually throw an error because the duplicate checks are done in ModDiscovererer before we get to this phase
            // So all this is really doing is wasting time.
            // But i'm leaving it here until I rewrite all of cpw's mod loading code because its such a clusterfuck.
            return LoadingModListImpl.init(systemMods, e);
        }

        var named = new HashMap<String, ModInfo>();
        for (var file : modFiles) {
            for (var info : file.getModInfos())
                named.put(info.getModId(), (ModInfo)info);
        }

        // try and validate dependencies
        final List<ExceptionData> failedList = Stream.concat(verifyDependencyVersions(modFiles).stream(), errors.stream()).toList();

        // if we miss one or the other, we abort now
        if (!failedList.isEmpty()) {
            return LoadingModListImpl.init(systemMods, new EarlyLoadingException("failure to validate mod list", null, failedList));
        } else {
            // Otherwise, lets try and sort the modlist and proceed
            try {
                var sorted = sort(modFiles, named);
                return LoadingModListImpl.init(sorted, null);
            } catch (EarlyLoadingException e) {
                // The only exception that can happen here is a cyclic exception, but fall back to system mods so we can display the nice screen.
                return LoadingModListImpl.init(systemMods, e);
            }
        }
    }

    private static State sort(final List<ModFile> modFiles, final Map<String, ModInfo> named) {
        final MutableGraph<ModInfo> graph = GraphBuilder.directed().build();

        int counter = 0;
        var infos = new HashMap<ModInfo, Integer>();
        for (var file : modFiles) {
            if (file.getModFileInfo() instanceof ModFileInfo info) {
                for (var imod : info.getMods()) {
                    var mod = (ModInfo)imod;
                    infos.put(mod, counter++);
                    graph.addNode(mod);
                }
            }
        }

        for (var file : modFiles) {
            for (var info : file.getModInfos()) {
                for (var dep : info.getDependencies()) {
                    // Ordering isn't effected by sides, should it be?
                    //if (!dep.getSide().isCorrectSide())
                    //    continue;

                    var target = named.get(dep.getModId());

                    // soft dep that doesn't exist. No edge required.
                    if (target == null)
                        continue;

                    var self = (ModInfo)dep.getOwner();

                    switch (dep.getOrdering()) {
                        case BEFORE -> graph.putEdge(self, target);
                        case AFTER -> graph.putEdge(target, self);
                        default -> {}
                    }
                }
            }
        }

        final List<ModInfo> sorted;
        try {
            sorted = TopologicalSort.topologicalSort(graph, Comparator.comparing(infos::get));
        } catch (CyclePresentException e) {
            Set<Set<ModInfo>> cycles = e.getCycles();
            var buf = new StringBuilder();
            buf.append("Mod Sorting failed - Detected Cycles: \n");

            var dataList = new ArrayList<ExceptionData>();
            for (var cycle : cycles) {
                buf.append("\tCycle:\n");
                for (var mod : cycle) {
                    var modDeps = new StringBuilder()
                        .append(mod.getModId())
                        .append(' ')
                        .append(mod.getDependencies().stream()
                            .filter(v -> cycle.stream().anyMatch(m -> m.getModId().equals(v.getModId())))
                            .map(dep -> dep.getOrdering().name() + " " + dep.getModId())
                            .collect(Collectors.joining(", "))
                        );
                    dataList.add(new ExceptionData("fml.modloading.cycle", modDeps.toString()));
                    buf.append("\t\tMod: ").append(modDeps.toString()).append('\n');
                }
            }

            LOGGER.error(LOADING, buf.toString());

            throw new EarlyLoadingException("Sorting error", e, dataList);
        }

        var files = new LinkedHashSet<ModFile>();
        var list = new ArrayList<ModInfo>();

        for (var mod : sorted) {
            files.add(mod.getOwningFile().getFile());
            list.add(mod);
        }

        return new State(files.stream().toList(), list);
    }

    private static State detectSystemMods(final List<ModFile> modFiles) {
        // Capture system mods (ex. MC, singularity) here, so we can keep them for later
        var systemMods = List.of("minecraft", "singularity");
        LOGGER.debug("Configured system mods: {}", systemMods);

        var mods = new ArrayList<ModInfo>();
        var files = new ArrayList<ModFile>();
        for (var systemMod : systemMods) {
            var mod = findMod(modFiles, systemMod);
            if (mod == null)
                throw new IllegalStateException("Failed to find system mod: " + systemMod);

            LOGGER.debug("Found system mod: {}", systemMod);
            mods.add(mod.info());
            files.add(mod.file());
        }

        return new State(files, mods);
    }

    private record ModPair(ModFile file, ModInfo info) {}
    private static ModPair findMod(final List<ModFile> modFiles, String name) {
        for (var file : modFiles) {
            for (var mod : file.getModFileInfo().getMods()) {
                if (name.equals(mod.getModId()))
                    return new ModPair(file, (ModInfo)mod);
            }
        }
        return null;
    }

    private static List<ExceptionData> verifyDependencyVersions(final List<ModFile> files) {
        final var modVersions = new HashMap<String, ArtifactVersion>();
        final var modRequirements = new HashSet<ModVersion>();
        int mandatoryRequired = 0;

        for (var file : files) {
            for (var info : file.getModInfos()) {
                modVersions.put(info.getModId(), info.getVersion());
                for (var dep : info.getDependencies()) {
                    if (dep.getSide().isCorrectSide()) {
                        if (modRequirements.add(dep) && dep.isMandatory())
                            mandatoryRequired++;
                    }
                }
            }
        }

        LOGGER.debug(LOADING, "Found {} mod requirements ({} mandatory, {} optional)", modRequirements.size(), mandatoryRequired, modRequirements.size() - mandatoryRequired);

        final var missingMandatory = new HashSet<ModVersion>();
        final var missingOptional = new HashSet<ModVersion>();

        for (var dep : modRequirements) {
            var modId = dep.getModId();
            var existing = modVersions.get(modId);

            if (!dep.isMandatory() && existing == null)
                continue;

            var range = dep.getVersionRange();
            if (existing != null && (range.containsVersion(existing) || "0.0NONE".equals(existing.toString())))
                continue;

            if (!VersionSupportMatrix.testVersionSupportMatrix(range, modId, "mod"))
                (dep.isMandatory() ? missingMandatory : missingOptional).add(dep);
        }


        LOGGER.debug(LOADING, "Found {} mod requirements missing ({} mandatory, {} optional)", missingMandatory.size() + missingOptional.size(), missingMandatory.size(), missingOptional.size());

        var ret = new ArrayList<ExceptionData>();

        if (!missingMandatory.isEmpty()) {
            LOGGER.error(LOADING, "Missing or unsupported mandatory dependencies:\n{}", formatDependencyError(missingMandatory, modVersions));
            for (var mv : missingMandatory)
                ret.add(data(mv, modVersions, "fml.modloading.missingdependency"));
        }

        if (!missingOptional.isEmpty()) {
            LOGGER.error(LOADING, "Unsupported installed optional dependencies:\n{}", formatDependencyError(missingMandatory, modVersions));
            for (var mv : missingMandatory)
                ret.add(data(mv, modVersions, "fml.modloading.missingdependency.optional"));
        }

        return ret;
    }

    private static String formatDependencyError(Collection<ModVersion> missing, Map<String, ArtifactVersion> modVersions) {
        var ret = new ArrayList<String>();
        for (var dep : missing) {
            var installed = modVersions.get(dep.getModId());
            ret.add(String.format(
                "\tMod ID: '%s', Requested by: '%s', Expected range: '%s', Actual version: '%s'",
                dep.getModId(),
                dep.getOwner().getModId(),
                dep.getVersionRange(),
                installed != null ? installed.toString() : "[MISSING]"
            ));
        }
        return String.join("\n", ret);
    }

    private static final ArtifactVersion NULL_VERSION = new DefaultArtifactVersion("null");
    private static ExceptionData data(ModVersion mv, Map<String, ArtifactVersion> modVersions, String key) {
        return new ExceptionData(key, mv.getOwner(), mv.getModId(), mv.getOwner().getModId(), mv.getVersionRange(), modVersions.getOrDefault(mv.getModId(), NULL_VERSION));
    }
}
