/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class UniqueModListBuilder
{
    private final static Logger LOGGER = LogManager.getLogger();

    private final List<ModFile> modFiles;

    public UniqueModListBuilder(final List<ModFile> modFiles) {this.modFiles = modFiles;}

    public UniqueModListData buildUniqueList()
    {
        List<ModFile> uniqueModList;
        List<ModFile> uniqueLibListWithVersion;

        // Collect mod files by module name. This will be used for deduping purposes
        final Map<String, List<ModFile>> modFilesByFirstId = modFiles.stream()
                .filter(mf -> mf.getModFileInfo() != null)
                .collect(groupingBy(UniqueModListBuilder::getModId));

        final Map<String, List<ModFile>> libFilesWithVersionByModuleName = modFiles.stream()
                .filter(mf -> mf.getModFileInfo() == null)
                .collect(groupingBy(UniqueModListBuilder::getModId));

        // Select the newest by artifact version sorting of non-unique files thus identified
        uniqueModList = modFilesByFirstId.entrySet().stream()
                .map(this::selectNewestModInfo)
                .toList();

        // Select the newest by artifact version sorting of non-unique files thus identified
        uniqueLibListWithVersion = libFilesWithVersionByModuleName.entrySet().stream()
                .map(this::selectNewestModInfo)
                .toList();

        // Transform to the full mod id list
        final Map<String, List<IModInfo>> modIds = uniqueModList.stream()
                .filter(mf -> mf.getModFileInfo() != null) //Filter out non-mod files, we don't care about those for now.....
                .map(ModFile::getModInfos)
                .flatMap(Collection::stream)
                .collect(groupingBy(IModInfo::getModId));

        // Transform to the full lib id list
        final Map<String, List<ModFile>> versionedLibIds = uniqueLibListWithVersion.stream()
                .map(UniqueModListBuilder::getModId)
                .collect(Collectors.toMap(
                    Function.identity(),
                    libFilesWithVersionByModuleName::get
                ));

        // Its theoretically possible that some mod has somehow moved an id to a secondary place, thus causing a dupe.
        // We can't handle this
        final List<IModInfo> dupedMods = modIds.values().stream()
                .filter(modInfos -> modInfos.size() > 1)
                .map(modInfos -> modInfos.get(0))
                .toList();

        if (!dupedMods.isEmpty()) {
            final List<EarlyLoadingException.ExceptionData> duplicateModErrors = dupedMods.stream()
                                                                                   .map(dm -> new EarlyLoadingException.ExceptionData("fml.modloading.dupedmod", dm, Objects.toString(dm)))
                                                                                   .toList();
            throw new EarlyLoadingException("Duplicate mods found", null,  duplicateModErrors);
        }

        final List<ModFile> dupedLibs = versionedLibIds.values().stream()
                .filter(modFiles -> modFiles.size() > 1)
                .map(modFiles -> modFiles.get(0))
                .toList();

        if (!dupedLibs.isEmpty()) {
            final List<EarlyLoadingException.ExceptionData> duplicateLibErrors = dupedLibs.stream()
                .map(dm -> new EarlyLoadingException.ExceptionData("fml.modloading.dupedlib.versioned", dm, Objects.toString(dm)))
                .toList();
            throw new EarlyLoadingException("Duplicate plugins or libraries found", null,  duplicateLibErrors);
        }

        // Collect unique mod files by module name. This will be used for deduping purposes
        final Map<String, List<ModFile>> uniqueModFilesByFirstId = uniqueModList.stream()
                .collect(groupingBy(UniqueModListBuilder::getModId));

        final List<ModFile> loadedList = new ArrayList<>();
        loadedList.addAll(uniqueModList);
        loadedList.addAll(uniqueLibListWithVersion);

        return new UniqueModListData(loadedList, uniqueModFilesByFirstId);
    }

    private ModFile selectNewestModInfo(Map.Entry<String, List<ModFile>> fullList) {
        List<ModFile> modInfoList = fullList.getValue();
        if (modInfoList.size() > 1) {
            LOGGER.debug("Found {} mods for first modid {}, selecting most recent based on version data", modInfoList.size(), fullList.getKey());
            modInfoList.sort(Comparator.comparing(this::getVersion).reversed());
            LOGGER.debug("Selected file {} for modid {} with version {}", modInfoList.get(0).getFileName(), fullList.getKey(), modInfoList.get(0).getModInfos().get(0).getVersion());
        }
        return modInfoList.get(0);
    }

    private ArtifactVersion getVersion(final ModFile mf)
    {
        if (mf.getModFileInfo() == null) {
            return mf.getJarVersion();
        }
        return mf.getModInfos().get(0).getVersion();
    }

    private static String getModId(ModFile modFile) {
        if (modFile.getModFileInfo() == null || modFile.getModFileInfo().getMods().isEmpty()) {
            return modFile.getSecureJar().name();
        }

        return modFile.getModFileInfo().moduleName();
    }

    public record UniqueModListData(List<ModFile> modFiles, Map<String, List<ModFile>> modFilesByFirstId) {}

}
