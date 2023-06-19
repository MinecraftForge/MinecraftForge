/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import net.minecraftforge.fml.loading.*;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModValidator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<IModFile.Type, List<ModFile>> modFiles;
    private final List<ModFile> candidatePlugins;
    private final List<ModFile> candidateMods;
    private LoadingModList loadingModList;
    private List<IModFile> brokenFiles;
    private final List<EarlyLoadingException.ExceptionData> discoveryErrorData;

    public ModValidator(final Map<IModFile.Type, List<ModFile>> modFiles, final List<IModFileInfo> brokenFiles, final List<EarlyLoadingException.ExceptionData> discoveryErrorData) {
        this.modFiles = modFiles;
        this.candidateMods = lst(modFiles.get(IModFile.Type.MOD));
        this.candidateMods.addAll(lst(modFiles.get(IModFile.Type.GAMELIBRARY)));
        this.candidatePlugins = lst(modFiles.get(IModFile.Type.LANGPROVIDER));
        this.candidatePlugins.addAll(lst(modFiles.get(IModFile.Type.LIBRARY)));
        this.discoveryErrorData = discoveryErrorData;
        this.brokenFiles = brokenFiles.stream().map(IModFileInfo::getFile).collect(Collectors.toList()); // mutable list
    }

    private static List<ModFile> lst(List<ModFile> files) {
        return files == null ? new ArrayList<>() : new ArrayList<>(files);
    }

    public void stage1Validation() {
        brokenFiles.addAll(validateFiles(candidateMods));
        if (LOGGER.isDebugEnabled(LogMarkers.SCAN)) {
            LOGGER.debug(LogMarkers.SCAN, "Found {} mod files with {} mods", candidateMods.size(), candidateMods.stream().mapToInt(mf -> mf.getModInfos().size()).sum());
        }
        ImmediateWindowHandler.updateProgress("Found "+candidateMods.size()+" mod candidates");
    }

    @NotNull
    private List<ModFile> validateFiles(final List<ModFile> mods) {
        final List<ModFile> brokenFiles = new ArrayList<>();
        for (Iterator<ModFile> iterator = mods.iterator(); iterator.hasNext(); )
        {
            ModFile modFile = iterator.next();
            if (!modFile.getProvider().isValid(modFile) || !modFile.identifyMods()) {
                LOGGER.warn(LogMarkers.SCAN, "File {} has been ignored - it is invalid", modFile.getFilePath());
                iterator.remove();
                brokenFiles.add(modFile);
            }
        }
        return brokenFiles;
    }

    public ITransformationService.Resource getPluginResources() {
        return new ITransformationService.Resource(IModuleLayerManager.Layer.PLUGIN, this.candidatePlugins.stream().map(IModFile::getSecureJar).toList());
    }

    public ITransformationService.Resource getModResources() {
        return new ITransformationService.Resource(IModuleLayerManager.Layer.GAME, this.candidateMods.stream().map(IModFile::getSecureJar).toList());
    }

    private List<EarlyLoadingException.ExceptionData> validateLanguages() {
        List<EarlyLoadingException.ExceptionData> errorData = new ArrayList<>();
        for (Iterator<ModFile> iterator = this.candidateMods.iterator(); iterator.hasNext(); ) {
            final ModFile modFile = iterator.next();
            try {
                modFile.identifyLanguage();
            } catch (EarlyLoadingException e) {
                errorData.addAll(e.getAllData());
                iterator.remove();
            }
        }
        return errorData;
    }

    public BackgroundScanHandler stage2Validation() {
        var errors = validateLanguages();

        var allErrors = new ArrayList<>(errors);
        allErrors.addAll(this.discoveryErrorData);

        loadingModList = ModSorter.sort(candidateMods, allErrors);
        loadingModList.addCoreMods();
        loadingModList.addAccessTransformers();
        loadingModList.setBrokenFiles(brokenFiles);
        BackgroundScanHandler backgroundScanHandler = new BackgroundScanHandler(candidateMods);
        loadingModList.addForScanning(backgroundScanHandler);
        return backgroundScanHandler;
    }
}
