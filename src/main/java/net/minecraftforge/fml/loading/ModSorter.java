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

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.Java9BackportUtils;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.common.DuplicateModsFoundException;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.toposort.TopologicalSort;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.logging.log4j.LogManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.LOADING;

public class ModSorter
{
    private final List<ModFile> modFiles;
    private List<ModInfo> sortedList;
    private Map<String, ModInfo> modIdNameLookup;

    public ModSorter(final List<ModFile> modFiles)
    {
        this.modFiles = modFiles;
    }

    public static ModList sort(List<ModFile> mods)
    {
        final ModSorter ms = new ModSorter(mods);
        ms.buildUniqueList();
        ms.verifyDependencyVersions();
        ms.sort();
        return new ModList(ms.modFiles, ms.sortedList);
    }

    private void sort() {
        final TopologicalSort.DirectedGraph<ModInfo> topoGraph = new TopologicalSort.DirectedGraph<>();
        modFiles.stream().map(ModFile::getModInfos).
                flatMap(Collection::stream).forEach(topoGraph::addNode);
        modFiles.stream().map(ModFile::getModInfos).
                flatMap(Collection::stream).map(ModInfo::getDependencies).flatMap(Collection::stream).forEach(dep->addDependency(topoGraph, dep));
    }

    private void addDependency(TopologicalSort.DirectedGraph<ModInfo> topoGraph, ModInfo.ModVersion dep)
    {
    }

    private void buildUniqueList()
    {
        final Stream<ModInfo> modInfos = modFiles.stream().map(ModFile::getModInfos).flatMap(Collection::stream);
        final Map<String, List<ModInfo>> modIds = modInfos.collect(Collectors.groupingBy(ModInfo::getModId));

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
                flatMap(Collection::stream), ForgeVersion.getModInfos().stream()).collect(Collectors.toMap(ModInfo::getModId, ModInfo::getVersion));
        final Map<ModInfo, List<ModInfo.ModVersion>> modVersionDependencies = modFiles.stream().
                map(ModFile::getModInfos).flatMap(Collection::stream).
                collect(Collectors.groupingBy(Function.identity(), Java9BackportUtils.flatMapping(e -> e.getDependencies().stream(), Collectors.toList())));
        final Set<ModInfo.ModVersion> mandatoryModVersions = modVersionDependencies.values().stream().flatMap(Collection::stream).
                filter(mv -> mv.isMandatory() && mv.getSide().isCorrectSide()).collect(Collectors.toSet());
        LogManager.getLogger("FML").debug(LOADING, "Found {} mandatory requirements", mandatoryModVersions.size());
        final Set<ModInfo.ModVersion> missingVersions = mandatoryModVersions.stream().filter(mv->this.modVersionMatches(mv, modVersions)).collect(Collectors.toSet());
        LogManager.getLogger("FML").debug(LOADING, "Found {} mandatory mod requirements missing", missingVersions.size());

        if (!missingVersions.isEmpty()) {
            throw new MissingModsException(null,null,null);
        }
    }

    private boolean modVersionMatches(final ModInfo.ModVersion mv, final Map<String, ArtifactVersion> modVersions)
    {
        return !modVersions.containsKey(mv.getModId()) || !mv.getVersionRange().containsVersion(modVersions.get(mv.getModId()));
    }
}
