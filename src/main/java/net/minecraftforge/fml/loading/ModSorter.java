/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import net.minecraftforge.fml.Java9BackportUtils;
import net.minecraftforge.fml.common.DuplicateModsFoundException;
import net.minecraftforge.fml.common.toposort.TopologicalSort;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.LOADING;

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
        ms.buildUniqueList();
        ms.verifyDependencyVersions();
        ms.sort();
        return LoadingModList.of(ms.modFiles, ms.sortedList);
    }

    private void sort()
    {
        final TopologicalSort.DirectedGraph<Supplier<ModFileInfo>> topoGraph = new TopologicalSort.DirectedGraph<>();
        modFiles.stream().map(ModFile::getModFileInfo).map(ModFileInfo.class::cast).forEach(mi -> topoGraph.addNode(() -> mi));
        modFiles.stream().map(ModFile::getModInfos).flatMap(Collection::stream).map(IModInfo::getDependencies).flatMap(Collection::stream).
                forEach(dep -> addDependency(topoGraph, dep));
        final List<Supplier<ModFileInfo>> sorted;
        try
        {
            sorted = TopologicalSort.topologicalSort(topoGraph);
        }
        catch (TopologicalSort.TopoSortException e)
        {
            TopologicalSort.TopoSortException.TopoSortExceptionData<Supplier<ModInfo>> data = e.getData();
            LOGGER.error(LOADING, ()-> data);
            throw e;
        }
        this.sortedList = sorted.stream().map(Supplier::get).map(ModFileInfo::getMods).
                flatMap(Collection::stream).map(ModInfo.class::cast).collect(Collectors.toList());
        this.modFiles = sorted.stream().map(Supplier::get).map(ModFileInfo::getFile).collect(Collectors.toList());
    }

    private void addDependency(TopologicalSort.DirectedGraph<Supplier<ModFileInfo>> topoGraph,IModInfo.ModVersion dep)
    {
        switch (dep.getOrdering()) {
            case BEFORE:
                topoGraph.addEdge(()->(ModFileInfo)dep.getOwner().getOwningFile(), ()->modIdNameLookup.get(dep.getModId()).getOwningFile());
                break;
            case AFTER:
                topoGraph.addEdge(()->modIdNameLookup.get(dep.getModId()).getOwningFile(), ()->(ModFileInfo)dep.getOwner().getOwningFile());
                break;
            case NONE:
                break;
        }
    }

    private void buildUniqueList()
    {
        final Stream<ModInfo> modInfos = modFiles.stream().map(ModFile::getModInfos).flatMap(Collection::stream).map(ModInfo.class::cast);
        final Map<String, List<ModInfo>> modIds = modInfos.collect(Collectors.groupingBy(IModInfo::getModId));

        // TODO: make this figure out dupe handling better
        final List<Map.Entry<String, List<ModInfo>>> dupedMods = modIds.entrySet().stream().filter(e -> e.getValue().size() > 1).collect(Collectors.toList());

        if (!dupedMods.isEmpty()) {
            throw new DuplicateModsFoundException(null);
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
            throw new RuntimeException("Missing mods");
        }
    }

    private boolean modVersionMatches(final IModInfo.ModVersion mv, final Map<String, ArtifactVersion> modVersions)
    {
        return !modVersions.containsKey(mv.getModId()) || !mv.getVersionRange().containsVersion(modVersions.get(mv.getModId()));
    }
}
