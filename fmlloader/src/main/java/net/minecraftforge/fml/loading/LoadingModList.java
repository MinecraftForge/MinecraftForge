/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import net.minecraftforge.fml.loading.EarlyLoadingException.ExceptionData;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.nio.file.Files;
import java.nio.file.Path;
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
public class LoadingModList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static LoadingModList INSTANCE;
    private final List<ModFileInfo> modFiles;
    private final List<ModInfo> sortedList;
    private final Map<String, ModFileInfo> fileById;
    private final List<EarlyLoadingException> preLoadErrors;
    private List<IModFile> brokenFiles;

    private LoadingModList(final List<ModFile> modFiles, final List<ModInfo> sortedList) {
        this.modFiles = modFiles.stream()
                .map(ModFile::getModFileInfo)
                .map(ModFileInfo.class::cast)
                .toList();
        this.sortedList = sortedList.stream().toList();
        this.fileById = this.modFiles.stream()
                .map(ModFileInfo::getMods)
                .flatMap(Collection::stream)
                .map(ModInfo.class::cast)
                .collect(Collectors.toMap(ModInfo::getModId, ModInfo::getOwningFile));
        this.preLoadErrors = new ArrayList<>();
    }

    public static LoadingModList of(List<ModFile> modFiles, List<ModInfo> sortedList, final EarlyLoadingException earlyLoadingException) {
        INSTANCE = new LoadingModList(modFiles, sortedList);
        if (earlyLoadingException != null)
            INSTANCE.preLoadErrors.add(earlyLoadingException);
        return INSTANCE;
    }

    public static LoadingModList get() {
        return INSTANCE;
    }

    public void addAccessTransformers() {
        var errors = new ArrayList<ExceptionData>();

        for (ModFileInfo modFile : modFiles) {
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
            preLoadErrors.add(new EarlyLoadingException("Invalid Access Transformers", null, errors));
    }

    public void addForScanning(BackgroundScanHandler backgroundScanHandler) {
        backgroundScanHandler.setLoadingModList(this);
        modFiles.stream()
                .map(ModFileInfo::getFile)
                .forEach(backgroundScanHandler::submitForScanning);
    }

    public List<ModFileInfo> getModFiles() {
        return modFiles;
    }

    public Path findResource(final String className) {
        for (ModFileInfo mf : modFiles) {
            final Path resource = mf.getFile().findResource(className);
            if (Files.exists(resource)) return resource;
        }
        return null;
    }

    public ModFileInfo getModFileById(String modid) {
        return this.fileById.get(modid);
    }

    public List<ModInfo> getMods() {
        return this.sortedList;
    }

    public List<EarlyLoadingException> getErrors() {
        return preLoadErrors;
    }

    public void setBrokenFiles(final List<IModFile> brokenFiles) {
        this.brokenFiles = brokenFiles;
    }

    public List<IModFile> getBrokenFiles() {
        return this.brokenFiles;
    }
}
