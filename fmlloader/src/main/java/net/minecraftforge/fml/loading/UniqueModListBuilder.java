/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading;

import com.mojang.logging.LogUtils;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFile;
import net.minecraftsingularity.singularityspi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static net.minecraftsingularity.fml.loading.LogMarkers.LOADING;

@ApiStatus.Internal
@Deprecated(since = "1.21.3", forRemoval = true) // TODO: [FML][Loading] Convert to package private in 1.22
public final class UniqueModListBuilder {
    private UniqueModListBuilder() {}

    private static final Logger LOGGER = LogUtils.getLogger();

    public static UniqueModListData buildUniqueList(List<ModFile> modFiles) {
        // Collect mod files by module name. This will be used for deduping purposes
        final Map<String, List<ModFile>> modFilesByFirstId = modFiles.stream()
                .filter(mf -> mf.getModFileInfo() != null)
                .collect(groupingBy(UniqueModListBuilder::getModId));

        final Map<String, List<ModFile>> libFilesWithVersionByModuleName = modFiles.stream()
                .filter(mf -> mf.getModFileInfo() == null)
                .collect(groupingBy(UniqueModListBuilder::getModId));

        // Select the newest by artifact version sorting of non-unique files thus identified
        final List<ModFile> uniqueModList = modFilesByFirstId.entrySet().stream()
                .map(UniqueModListBuilder::selectNewestModInfo)
                .toList();

        // Select the newest by artifact version sorting of non-unique files thus identified
        final List<ModFile> uniqueLibListWithVersion = libFilesWithVersionByModuleName.entrySet().stream()
                .map(UniqueModListBuilder::selectNewestModInfo)
                .toList();

        // Transform to the full mod id list
        final Map<String, List<IModInfo>> modIds = uniqueModList.stream()
                .filter(mf -> mf.getModFileInfo() != null) //Filter out non-mod files, we don't care about those for now.....
                .map(ModFile::getModInfos)
                .flatMap(Collection::stream)
                .collect(groupingBy(IModInfo::getModId));

        // Transform to the full lib id list
        final List<List<ModFile>> versionedLibs = uniqueLibListWithVersion.stream()
                .map(UniqueModListBuilder::getModId)
                .map(libFilesWithVersionByModuleName::get)
                .toList();

        // Its theoretically possible that some mod has somehow moved an id to a secondary place, thus causing a dupe.
        // We can't handle this
        final List<String> dupedModErrors = modIds.values().stream()
                .filter(modInfos -> modInfos.size() > 1)
                .map(mods -> String.format("\tMod ID: '%s' from mod files: %s",
                        mods.getFirst().getModId(),
                        mods.stream()
                                .map(modInfo -> modInfo.getOwningFile().getFile().getFileName()).collect(joining(", "))
                )).toList();

        if (!dupedModErrors.isEmpty()) {
            LOGGER.error(LOADING, "Found duplicate mods:\n{}", String.join("\n", dupedModErrors));
            throw new EarlyLoadingException("Duplicate mods found", null, dupedModErrors.stream()
                    .map(EarlyLoadingException.ExceptionData::new)
                    .toList());
        }


        final List<String> dupedLibErrors = versionedLibs.stream()
                .filter(libModFiles -> libModFiles.size() > 1)
                .map(mods -> String.format("\tLibrary: '%s' from files: %s",
                        getModId(mods.getFirst()),
                        mods.stream().map(ModFile::getFileName).collect(joining(", "))
                )).toList();

        if (!dupedLibErrors.isEmpty()) {
            LOGGER.error(LOADING, "Found duplicate plugins or libraries:\n{}", String.join("\n", dupedLibErrors));
            throw new EarlyLoadingException("Duplicate plugins or libraries found", null, dupedLibErrors.stream()
                    .map(EarlyLoadingException.ExceptionData::new)
                    .toList());
        }

        // Collect unique mod files by module name. This will be used for deduping purposes
        final Map<String, List<ModFile>> uniqueModFilesByFirstId = uniqueModList.stream()
                .collect(groupingBy(UniqueModListBuilder::getModId));

        final List<ModFile> loadedList = new ArrayList<>(uniqueModList);
        loadedList.addAll(uniqueLibListWithVersion);

        return new UniqueModListData(loadedList, uniqueModFilesByFirstId);
    }

    private static ModFile selectNewestModInfo(Map.Entry<String, List<ModFile>> fullList) {
        List<ModFile> modInfoList = fullList.getValue();
        if (modInfoList.size() > 1) {
            LOGGER.debug("Found {} mods for first modid {}, selecting most recent based on version data", modInfoList.size(), fullList.getKey());
            modInfoList.sort(Comparator.comparing(UniqueModListBuilder::getVersion).reversed());
            LOGGER.debug("Selected file {} for modid {} with version {}", modInfoList.getFirst().getFileName(), fullList.getKey(), getVersion(modInfoList.getFirst()));
        }
        return modInfoList.getFirst();
    }

    private static ArtifactVersion getVersion(final ModFile mf) {
        List<IModInfo> modInfos;
        if (mf.getModFileInfo() == null || (modInfos = mf.getModInfos()) == null || modInfos.isEmpty()) {
            return mf.getJarVersion();
        }

        return modInfos.getFirst().getVersion();
    }

    private static String getModId(ModFile modFile) {
        var modFileInfo = modFile.getModFileInfo();
        List<IModInfo> mods;
        if (modFileInfo == null || (mods = modFileInfo.getMods()).isEmpty()) {
            return modFile.getSecureJar().name();
        }

        return mods.getFirst().getModId();
    }

    public record UniqueModListData(List<ModFile> modFiles, Map<String, List<ModFile>> modFilesByFirstId) {}
}
