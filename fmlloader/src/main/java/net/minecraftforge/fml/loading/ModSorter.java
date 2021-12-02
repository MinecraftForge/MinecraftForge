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

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fml.loading.EarlyLoadingException.ExceptionData;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.loading.toposort.CyclePresentException;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class ModSorter
{
    private static final Logger LOGGER = LogManager.getLogger();
    private List<ModFile> modFiles;
    private List<ModInfo> sortedList;
    private Map<String, ModInfo> modIdNameLookup;
    private List<ModFile> coreGameMods;

    private ModSorter(final List<ModFile> modFiles)
    {
        this.modFiles = modFiles;
    }

    public static LoadingModList sort(List<ModFile> mods, final Set<String> coreGameMods, final List<ExceptionData> errors)
    {
        final ModSorter ms = new ModSorter(mods);
        try {
            ms.buildUniqueList(coreGameMods);
        } catch (EarlyLoadingException e) {
            // We cannot build any list with duped mods. We have to abort immediately and report it
            return LoadingModList.of(ms.coreGameMods, ms.coreGameMods.stream().map(mf->(ModInfo)mf.getModInfos().get(0)).collect(toList()), e);
        }
        // try and validate dependencies
        final List<ExceptionData> failedList = Stream.concat(ms.verifyDependencyVersions().stream(), errors.stream()).toList();
        // if we miss one or the other, we abort now
        if (!failedList.isEmpty()) {
            return LoadingModList.of(ms.coreGameMods, ms.coreGameMods.stream().map(mf->(ModInfo)mf.getModInfos().get(0)).collect(toList()), new EarlyLoadingException("failure to validate mod list", null, failedList));
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

    @SuppressWarnings("UnstableApiUsage")
    private void sort()
    {
        // lambdas are identity based, so sorting them is impossible unless you hold reference to them
        final MutableGraph<ModFileInfo> graph = GraphBuilder.directed().build();
        AtomicInteger counter = new AtomicInteger();
        Map<IModFileInfo, Integer> infos = modFiles.stream()
                .map(ModFile::getModFileInfo)
                .filter(ModFileInfo.class::isInstance)
                .collect(toMap(Function.identity(), e -> counter.incrementAndGet()));
        infos.keySet().forEach(i -> graph.addNode((ModFileInfo) i));
        modFiles.stream()
                .map(ModFile::getModInfos)
                .<IModInfo>mapMulti(Iterable::forEach)
                .map(IModInfo::getDependencies)
                .<IModInfo.ModVersion>mapMulti(Iterable::forEach)
                .forEach(dep -> addDependency(graph, dep));

        final List<ModFileInfo> sorted;
        try
        {
            sorted = TopologicalSort.topologicalSort(graph, Comparator.comparing(infos::get));
        }
        catch (CyclePresentException e)
        {
            Set<Set<ModFileInfo>> cycles = e.getCycles();
            LOGGER.error(LOADING, () -> new AdvancedLogMessageAdapter(buffer ->
                    buffer.append("Mod Sorting failed.\n")
                    .append("Detected Cycles: ")
                    .append(cycles)
                    .append('\n')));
            var dataList = cycles.stream()
                    .<ModFileInfo>mapMulti(Iterable::forEach)
                    .<IModInfo>mapMulti((mf,c)->mf.getMods().forEach(c))
                    .map(IModInfo::getModId)
                    .map(list -> new ExceptionData("fml.modloading.cycle", list))
                    .toList();
            throw new EarlyLoadingException("Sorting error", e, dataList);
        }
        this.sortedList = sorted.stream()
                .map(ModFileInfo::getMods)
                .<IModInfo>mapMulti(Iterable::forEach)
                .map(ModInfo.class::cast)
                .collect(toList());
        this.modFiles = sorted.stream()
                .map(ModFileInfo::getFile)
                .collect(toList());
    }

    @SuppressWarnings("UnstableApiUsage")
    private void addDependency(MutableGraph<ModFileInfo> topoGraph, IModInfo.ModVersion dep)
    {
        final ModFileInfo self = (ModFileInfo)dep.getOwner().getOwningFile();
        final ModInfo targetModInfo = modIdNameLookup.get(dep.getModId());
        // soft dep that doesn't exist. Just return. No edge required.
        if (targetModInfo == null) return;
        final ModFileInfo target = targetModInfo.getOwningFile();
        if (self == target)
            return; // in case a jar has two mods that have dependencies between
        switch (dep.getOrdering()) {
            case BEFORE -> topoGraph.putEdge(self, target);
            case AFTER -> topoGraph.putEdge(target, self);
        }
    }

    private void buildUniqueList(Set<String> coreGameMods)
    {
        // Collect mod files by module name. This will be used for deduping purposes
        final Map<String, List<IModFile>> modFilesByFirstId = modFiles.stream()
                .collect(groupingBy(mf -> mf.getModFileInfo().moduleName()));

        // Capture core game mods (ex. MC, Forge) here, so we can keep them for later
        this.coreGameMods = new ArrayList<>();
        for (String coreGameMod : coreGameMods) {
            var container = modFilesByFirstId.get(coreGameMod);
            if (container != null && !container.isEmpty()) {
                LOGGER.debug("Found core game mod: " + coreGameMod);
                this.coreGameMods.add((ModFile) container.get(0));
            } else {
                throw new IllegalStateException("Failed to find core game mod: " + coreGameMod);
            }
        }

        // Select the newest by artifact version sorting of non-unique files thus identified
        this.modFiles = modFilesByFirstId.entrySet().stream()
                .map(this::selectNewestModInfo)
                .map(Map.Entry::getValue)
                .map(ModFile.class::cast)
                .collect(toList());

        // Transform to the full mod id list
        final Map<String, List<ModInfo>> modIds = modFiles.stream()
                .map(ModFile::getModInfos)
                .flatMap(Collection::stream)
                .map(ModInfo.class::cast)
                .collect(groupingBy(IModInfo::getModId));

        // Its theoretically possible that some mod has somehow moved an id to a secondary place, thus causing a dupe.
        // We can't handle this
        final List<ModInfo> dupedMods = modIds.values().stream()
                .filter(modInfos -> modInfos.size() > 1)
                .map(modInfos -> modInfos.get(0))
                .toList();

        if (!dupedMods.isEmpty()) {
            final List<EarlyLoadingException.ExceptionData> duplicateModErrors = dupedMods.stream()
                    .map(dm -> new EarlyLoadingException.ExceptionData("fml.modloading.dupedmod",
                            dm, Objects.toString(dm))).toList();
            throw new EarlyLoadingException("Duplicate mods found", null,  duplicateModErrors);
        }

        modIdNameLookup = modIds.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
    }

    private Map.Entry<String, IModFile> selectNewestModInfo(Map.Entry<String, List<IModFile>> fullList) {
        List<IModFile> modInfoList = fullList.getValue();
        if (modInfoList.size() > 1) {
            LOGGER.debug("Found {} mods for first modid {}, selecting most recent based on version data", modInfoList.size(), fullList.getKey());
            modInfoList.sort(Comparator.<IModFile, ArtifactVersion>comparing(mf -> mf.getModInfos().get(0).getVersion()).reversed());
            LOGGER.debug("Selected file {} for modid {} with version {}", modInfoList.get(0).getFileName(), fullList.getKey(), modInfoList.get(0).getModInfos().get(0).getVersion());
        }
        return Map.entry(fullList.getKey(), modInfoList.get(0));
    }

    private List<EarlyLoadingException.ExceptionData> verifyDependencyVersions()
    {
        final var modVersions = modFiles.stream()
                .map(ModFile::getModInfos)
                .<IModInfo>mapMulti(Iterable::forEach)
                .collect(toMap(IModInfo::getModId, IModInfo::getVersion));

        final var modVersionDependencies = modFiles.stream()
                .map(ModFile::getModInfos)
                .<IModInfo>mapMulti(Iterable::forEach)
                .collect(groupingBy(Function.identity(), flatMapping(e -> e.getDependencies().stream(), toList())));

        final var modRequirements = modVersionDependencies.values().stream()
                .<IModInfo.ModVersion>mapMulti(Iterable::forEach)
                .filter(mv -> mv.getSide().isCorrectSide())
                .collect(toSet());

        final long mandatoryRequired = modRequirements.stream().filter(IModInfo.ModVersion::isMandatory).count();
        LOGGER.debug(LOADING, "Found {} mod requirements ({} mandatory, {} optional)", modRequirements.size(), mandatoryRequired, modRequirements.size() - mandatoryRequired);
        final var missingVersions = modRequirements.stream()
                .filter(mv -> (mv.isMandatory() || modVersions.containsKey(mv.getModId())) && this.modVersionNotContained(mv, modVersions))
                .collect(toSet());
        final long mandatoryMissing = missingVersions.stream().filter(IModInfo.ModVersion::isMandatory).count();
        LOGGER.debug(LOADING, "Found {} mod requirements missing ({} mandatory, {} optional)", missingVersions.size(), mandatoryMissing, missingVersions.size() - mandatoryMissing);

        if (!missingVersions.isEmpty()) {
            return missingVersions.stream()
                    .map(mv -> new ExceptionData(mv.isMandatory() ? "fml.modloading.missingdependency" : "fml.modloading.missingdependency.optional",
                            mv.getOwner(), mv.getModId(), mv.getOwner().getModId(), mv.getVersionRange(),
                            modVersions.getOrDefault(mv.getModId(), new DefaultArtifactVersion("null"))))
                    .toList();
        }
        return Collections.emptyList();
    }

    private boolean modVersionNotContained(final IModInfo.ModVersion mv, final Map<String, ArtifactVersion> modVersions)
    {
        return !(VersionSupportMatrix.testVersionSupportMatrix(mv.getVersionRange(), mv.getModId(), "mod", (modId, range) -> modVersions.containsKey(modId) &&
                (range.containsVersion(modVersions.get(modId)) || modVersions.get(modId).toString().equals("0.0NONE"))));
    }
}
