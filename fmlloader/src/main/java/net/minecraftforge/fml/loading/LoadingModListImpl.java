/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading;

import net.minecraftsingularity.fml.loading.EarlyLoadingException.ExceptionData;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFile;
import net.minecraftsingularity.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftsingularity.fml.loading.moddiscovery.ModInfo;
import net.minecraftsingularity.singularityspi.locating.IModFile;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

/**
 * Master list of all mods <em>in the loading context. This class cannot refer outside the
 * loading package</em>
 */
record LoadingModListImpl(
        List<ModFileInfo> getModFiles,
        List<ModInfo> getMods,
        Map<String, ModFileInfo> fileById,
        ArrayList<EarlyLoadingException> getErrors,
        ArrayList<IModFile> getBrokenFiles
) implements LoadingModList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ModSorter.State temp;

    LoadingModListImpl(List<ModFile> modFiles, List<ModInfo> sortedList) {
        var modFileInfos = modFiles.stream()
                .map(ModFile::getModFileInfo)
                .map(ModFileInfo.class::cast)
                .toList();
        sortedList = sortedList.stream().toList();
        var fileById = modFileInfos.stream()
                .map(ModFileInfo::getMods)
                .flatMap(Collection::stream)
                .map(ModInfo.class::cast)
                .collect(Collectors.toUnmodifiableMap(ModInfo::getModId, ModInfo::getOwningFile));
        this(modFileInfos, sortedList, fileById, new ArrayList<>(), new ArrayList<>());
    }

    static LoadingModListImpl init(ModSorter.State modSorterState, EarlyLoadingException earlyLoadingException) {
        temp = modSorterState;
        var instance = get();
        if (earlyLoadingException != null)
            instance.getErrors.add(earlyLoadingException);
        temp = null;
        instance.addAccessTransformers();
        return instance;
    }

    static LoadingModListImpl get() {
        final class LazyInit {
            private LazyInit() {}
            private static final LoadingModListImpl INSTANCE
                    = new LoadingModListImpl(LoadingModListImpl.temp.files(), LoadingModListImpl.temp.mods());
        }
        return LazyInit.INSTANCE;
    }

    private void addAccessTransformers() {
        var errors = new ArrayList<ExceptionData>();

        for (ModFileInfo modFile : getModFiles) {
            ModFile mod = modFile.getFile();
            for (var at : mod.getAccessTransformers()) {
                if (!Files.exists(at)) {
                    var message = "Invalid mod file: " + modFile.getFile().getFileName() + ". Missing Access Transformer: " + at;
                    errors.add(new ExceptionData(message));
                    LOGGER.error(message);
                } else
                    FMLLoader.addAccessTransformer(at, mod);
            }
        }

        if (!errors.isEmpty())
            getErrors.add(new EarlyLoadingException("Invalid Access Transformers", null, errors));
    }
}
