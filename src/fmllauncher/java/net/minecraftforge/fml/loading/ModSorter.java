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

package net.minecraftforge.fml.loading;

import com.google.common.collect.Streams;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.loading.LogMarkers.LOADING;

public class ModSorter
{
    private static final Logger LOGGER = LogManager.getLogger();
    private List<ModFile> modFiles;
    private List<ModInfo> sortedList;
    private Map<String, ModInfo> modIdNameLookup;

    private ModSorter(final List<ModFile> modFiles)
    {
        this.modFiles = modFiles;
    }

    public static LoadingModList sort(List<ModFile> mods)
    {
        final ModSorter ms = new ModSorter(mods);
        EarlyLoadingException earlyLoadingException = null;
        try {
            ms.findLanguages();
            ms.buildUniqueList();
            ms.verifyDependencyVersions();
            ms.sort();
        } catch (EarlyLoadingException ele) {
            earlyLoadingException = ele;
            ms.sortedList = Collections.emptyList();
        }
        return LoadingModList.of(ms.modFiles, ms.sortedList, earlyLoadingException);
    }

    private void findLanguages() {
        modFiles.forEach(ModFile::identifyLanguage);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sort()
    {
        // lambdas are identity based, so sorting them is impossible unless you hold reference to them
        final MutableGraph<ModFileInfo> graph = GraphBuilder.directed().build();
        AtomicInteger counter = new AtomicInteger();
        Map<IModFileInfo, Integer> infos = modFiles.stream().map(ModFile::getModFileInfo).collect(Collectors.toMap(Function.identity(), (e) -> counter.incrementAndGet()));
        infos.keySet().forEach(i -> graph.addNode((ModFileInfo) i));
        modFiles.stream().map(ModFile::getModInfos).flatMap(Collection::stream).
                map(IModInfo::getDependencies).flatMap(Collection::stream).
                forEach(dep -> addDependency(graph, dep));
        final List<ModFileInfo> sorted;
        try
        {
            sorted = TopologicalSort.topologicalSort(graph, Comparator.comparing(infos::get));
        }
        catch (CyclePresentException e)
        {
            Set<Set<ModFileInfo>> cycles = e.getCycles();
            LOGGER.error(LOADING, () -> ((StringBuilderFormattable) (buffer -> {
                buffer.append("Mod Sorting failed.\n");
                buffer.append("Detected Cycles: ");
                buffer.append(cycles);
                buffer.append('\n');
            })));
            List<ExceptionData> dataList = cycles.stream()
                    .map(Set::stream)
                    .map(stream -> stream
                            .flatMap(modFileInfo -> modFileInfo.getMods().stream()
                                    .map(IModInfo::getModId)).collect(Collectors.toList()))
                    .map(list -> new ExceptionData("fml.modloading.cycle", list))
                    .collect(Collectors.toList());
            throw new EarlyLoadingException("Sorting error", e, dataList);
        }
        this.sortedList = sorted.stream().map(ModFileInfo::getMods).
                flatMap(Collection::stream).map(ModInfo.class::cast).collect(Collectors.toList());
        this.modFiles = sorted.stream().map(ModFileInfo::getFile).collect(Collectors.toList());
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
            case BEFORE:
                topoGraph.putEdge(self, target);
                break;
            case AFTER:
                topoGraph.putEdge(target, self);
                break;
            case NONE:
                break;
        }
    }

    private void buildUniqueList()
    {
        final Stream<ModInfo> modInfos = Stream.concat(DefaultModInfos.getModInfos().stream(), modFiles.stream().map(ModFile::getModInfos).flatMap(Collection::stream)).map(ModInfo.class::cast);
        final Map<String, List<ModInfo>> modIds = modInfos.collect(Collectors.groupingBy(IModInfo::getModId));

        // TODO: make this figure out dupe handling better
        final List<Map.Entry<String, List<ModInfo>>> dupedMods = modIds.entrySet().stream().filter(e -> e.getValue().size() > 1).collect(Collectors.toList());

        if (!dupedMods.isEmpty()) {
            final List<EarlyLoadingException.ExceptionData> duplicateModErrors = dupedMods.stream().
                    map(dm -> new EarlyLoadingException.ExceptionData("fml.modloading.dupedmod", dm.getValue().get(0))).
                    collect(Collectors.toList());
            throw new EarlyLoadingException("Duplicate mods found", null,  duplicateModErrors);
        }

        modIdNameLookup = modIds.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
    }

    private void verifyDependencyVersions()
    {
        final Map<String, ArtifactVersion> modVersions = Stream.concat(modFiles.stream().map(ModFile::getModInfos).
                flatMap(Collection::stream), DefaultModInfos.getModInfos().stream()).collect(Collectors.toMap(IModInfo::getModId, IModInfo::getVersion));
        final Map<IModInfo, List<IModInfo.ModVersion>> modVersionDependencies = modFiles.stream().
                map(ModFile::getModInfos).flatMap(Collection::stream).
                collect(Collectors.groupingBy(Function.identity(), Java9BackportUtils.flatMapping(e -> e.getDependencies().stream(), Collectors.toList())));
        final Set<IModInfo.ModVersion> mandatoryModVersions = modVersionDependencies.values().stream().flatMap(Collection::stream).
                filter(mv -> mv.isMandatory() && mv.getSide().isCorrectSide()).collect(Collectors.toSet());
        LOGGER.debug(LOADING, "Found {} mandatory requirements", mandatoryModVersions.size());
        final Set<IModInfo.ModVersion> missingVersions = mandatoryModVersions.stream().filter(mv->this.modVersionMatches(mv, modVersions)).collect(Collectors.toSet());
        LOGGER.debug(LOADING, "Found {} mandatory mod requirements missing", missingVersions.size());

        if (!missingVersions.isEmpty()) {
            final List<EarlyLoadingException.ExceptionData> exceptionData = missingVersions.stream().map(mv ->
                    new EarlyLoadingException.ExceptionData("fml.modloading.missingdependency", mv.getModId(),
                            mv.getOwner().getModId(), mv.getVersionRange(), modVersions.getOrDefault(mv.getModId(), new DefaultArtifactVersion("null")))).
                    collect(Collectors.toList());
            throw new EarlyLoadingException("Missing mods", null, exceptionData);
        }
    }

    private boolean modVersionMatches(final IModInfo.ModVersion mv, final Map<String, ArtifactVersion> modVersions)
    {
        return !modVersions.containsKey(mv.getModId()) || !mv.getVersionRange().containsVersion(modVersions.get(mv.getModId()));
    }
}
