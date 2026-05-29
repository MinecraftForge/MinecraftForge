/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading.moddiscovery;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftsingularity.fml.loading.EarlyLoadingException;
import net.minecraftsingularity.fml.loading.FMLLoader;
import net.minecraftsingularity.fml.loading.ImmediateWindowHandler;
import net.minecraftsingularity.fml.loading.LogMarkers;
import net.minecraftsingularity.fml.loading.UniqueModListBuilder;
import net.minecraftsingularity.fml.loading.progress.StartupNotificationManager;
import net.minecraftsingularity.singularityspi.Environment;
import net.minecraftsingularity.singularityspi.language.IModFileInfo;
import net.minecraftsingularity.singularityspi.locating.IDependencyLocator;
import net.minecraftsingularity.singularityspi.locating.IModFile;
import net.minecraftsingularity.singularityspi.locating.IModLocator;

import net.minecraftsingularity.singularityspi.locating.ModFileLoadingException;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class ModDiscoverer {
    private ModDiscoverer() {}

    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public static ModValidator discoverMods(Map<String, ?> arguments) {
        var env = Launcher.INSTANCE.environment();
        env.computePropertyIfAbsent(Environment.Keys.MODDIRECTORYFACTORY.get(), _ -> ModsFolderLocator::new);
        env.computePropertyIfAbsent(Environment.Keys.PROGRESSMESSAGE.get(), _ -> StartupNotificationManager.locatorConsumer().orElse(_ -> {}));
        final var moduleLayerManager = env.findModuleLayerManager().orElseThrow();
        ServiceLoader<IModLocator> modLocators = ServiceLoader.load(moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE).orElseThrow(), IModLocator.class);
        ServiceLoader<IDependencyLocator> dependencyLocators = ServiceLoader.load(moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE).orElseThrow(), IDependencyLocator.class);
        List<IModLocator> modLocatorList = ServiceLoaderUtils.streamWithErrorHandling(modLocators, sce -> LOGGER.error("Failed to load mod locator list", sce)).toList();
        for (IModLocator iModLocator : modLocatorList) {
            iModLocator.initArguments(arguments);
        }
        List<IDependencyLocator> dependencyLocatorList = ServiceLoaderUtils.streamWithErrorHandling(dependencyLocators, sce -> LOGGER.error("Failed to load dependency locator list", sce)).toList();
        for (IDependencyLocator l : dependencyLocatorList) {
            l.initArguments(arguments);
        }
        if (LOGGER.isDebugEnabled(LogMarkers.CORE)) {
            LOGGER.debug(LogMarkers.CORE, "Found Mod Locators : {}", modLocatorList.stream()
                    .map(modLocator -> "(%s:%s)".formatted(modLocator.name(),
                            modLocator.getClass().getPackage().getImplementationVersion())).collect(Collectors.joining(",")));

            LOGGER.debug(LogMarkers.CORE, "Found Dependency Locators : {}", dependencyLocatorList.stream()
                    .map(dependencyLocator -> "(%s:%s)".formatted(dependencyLocator.name(),
                            dependencyLocator.getClass().getPackage().getImplementationVersion())).collect(Collectors.joining(",")));
        }

        LOGGER.debug(LogMarkers.SCAN,"Scanning for mods and other resources to load. We know {} ways to find mods", modLocatorList.size());
        List<ModFile> loadedFiles = new ArrayList<>();
        List<EarlyLoadingException.ExceptionData> discoveryErrorData = new ArrayList<>();
        boolean successfullyLoadedMods = true;
        List<IModFileInfo> brokenFiles = new ArrayList<>();
        List<ModFileLoadingException> modFileLoadingExceptions = new ArrayList<>();
        boolean distIsDedicatedServer = FMLLoader.getDist().isDedicatedServer();
        ImmediateWindowHandler.updateProgress("Discovering mod files");
        //Loop all mod locators to get the prime mods to load from.
        for (IModLocator locator : modLocatorList) {
            try {
                LOGGER.debug(LogMarkers.SCAN, "Trying locator {}", locator);
                var candidates = locator.scanMods();
                LOGGER.debug(LogMarkers.SCAN, "Locator {} found {} candidates or errors", locator, candidates.size());
                var exceptions = candidates.stream().map(IModLocator.ModFileOrException::ex).filter(Objects::nonNull).toList();
                if (!exceptions.isEmpty()) {
                    LOGGER.debug(LogMarkers.SCAN, "Locator {} found {} invalid mod files", locator, exceptions.size());
                    for (ModFileLoadingException ex : exceptions) {
                        // pipe exception messages through the discoveryErrorData to avoid swallowing some exceptions and improve error messages
                        // (no longer a generic "Invalid mod file" for all InvalidModExceptions - it actually shows the exception message now)
                        if (ex instanceof InvalidModFileException invalidModFileEx) {
                            var modInfo = invalidModFileEx.getBrokenFile();
                            brokenFiles.add(modInfo);
                            discoveryErrorData.add(new EarlyLoadingException.ExceptionData(ex.getMessage(), modInfo));
                        } else {
                            discoveryErrorData.add(new EarlyLoadingException.ExceptionData(ex.getMessage()));
                        }
                    }
                }
                var locatedFiles = candidates.stream().map(IModLocator.ModFileOrException::file).filter(Objects::nonNull).collect(Collectors.toList());

                var badModFiles = locatedFiles.stream().filter(file -> !(file instanceof ModFile)).toList();
                if (!badModFiles.isEmpty()) {
                    LOGGER.error(LogMarkers.SCAN, "Locator {} returned {} files which is are not ModFile instances! They will be skipped!", locator, badModFiles.size());
                    brokenFiles.addAll(badModFiles.stream().map(IModFile::getModFileInfo).toList());
                    locatedFiles.removeAll(badModFiles);
                }

                if (distIsDedicatedServer) {
                    var clientOnlyModFiles = locatedFiles.stream().filter(file -> file.getModFileInfo().isClientSideOnly()).toList();
                    if (!clientOnlyModFiles.isEmpty()) {
                        LOGGER.warn(LogMarkers.SCAN, "Locator {} returned {} files which are client-side-only mods, but we're on a dedicated server. They will be skipped!", locator, clientOnlyModFiles.size());
                        locatedFiles.removeAll(clientOnlyModFiles);
                    }
                }

                LOGGER.debug(LogMarkers.SCAN, "Locator {} found {} valid mod files", locator, locatedFiles.size());
                handleLocatedFiles(loadedFiles, locatedFiles, locator);
            } catch (InvalidModFileException imfe) {
                // We don't generally expect this exception, since it should come from the candidates stream above and be handled in the Locator, but just in case.
                LOGGER.error(LogMarkers.SCAN, "Locator {} found an invalid mod file {}", locator, imfe.getBrokenFile(), imfe);
                brokenFiles.add(imfe.getBrokenFile());
            } catch (EarlyLoadingException exception) {
                LOGGER.error(LogMarkers.SCAN, "Failed to load mods with locator {}", locator, exception);
                discoveryErrorData.addAll(exception.getAllData());
            }
        }

        //First processing run of the mod list. Any duplicates will cause resolution failure and dependency loading will be skipped.
        Map<IModFile.Type, List<ModFile>> modFilesMap = new EnumMap<>(IModFile.Type.class);
        try {
            final UniqueModListBuilder.UniqueModListData uniqueModsData = UniqueModListBuilder.buildUniqueList(loadedFiles);

            //Grab the temporary results.
            //This allows loading to continue to a base state, in case dependency loading fails.
            modFilesMap = uniqueModsData.modFiles().stream()
                            .collect(Collectors.groupingBy(IModFile::getType, () -> new EnumMap<>(IModFile.Type.class), Collectors.toList()));
            loadedFiles = uniqueModsData.modFiles();
        }
        catch (EarlyLoadingException exception) {
            LOGGER.error(LogMarkers.SCAN, "Failed to build unique mod list after mod discovery.", exception);
            discoveryErrorData.addAll(exception.getAllData());
            successfullyLoadedMods = false;
        }

        //We can continue loading if prime mods loaded successfully.
        if (successfullyLoadedMods) {
            LOGGER.debug(LogMarkers.SCAN, "Successfully Loaded {} mods. Attempting to load dependencies...", loadedFiles.size());
            for (IDependencyLocator locator : dependencyLocatorList) {
                try {
                    LOGGER.debug(LogMarkers.SCAN,"Trying locator {}", locator);
                    final List<IModFile> locatedMods = List.copyOf(loadedFiles);

                    var locatedFiles = locator.scanMods(locatedMods);
                    handleLocatedFiles(loadedFiles, locatedFiles, locator);
                }
                catch (EarlyLoadingException exception) {
                    LOGGER.error(LogMarkers.SCAN, "Failed to load dependencies with locator {}", locator, exception);
                    discoveryErrorData.addAll(exception.getAllData());
                }
            }

            //Second processing run of the mod list. Any duplicates will cause resolution failure and only the mods list will be loaded.
            try {
                final UniqueModListBuilder.UniqueModListData uniqueModsAndDependenciesData = UniqueModListBuilder.buildUniqueList(loadedFiles);

                //We now only need the mod files map, not the list.
                modFilesMap = uniqueModsAndDependenciesData.modFiles().stream()
                                .collect(Collectors.groupingBy(IModFile::getType, () -> new EnumMap<>(IModFile.Type.class), Collectors.toList()));
            } catch (EarlyLoadingException exception) {
                LOGGER.error(LogMarkers.SCAN, "Failed to build unique mod list after dependency discovery.", exception);
                discoveryErrorData.addAll(exception.getAllData());
                modFilesMap = loadedFiles.stream().collect(Collectors.groupingBy(IModFile::getType, () -> new EnumMap<>(IModFile.Type.class), Collectors.toList()));
            }
        }
        else {
            //Failure notify the listeners.
            LOGGER.error(LogMarkers.SCAN, "Mod Discovery failed. Skipping dependency discovery.");
        }

        //Validate the loading. With a deduplicated list, we can now successfully process the artifacts and load
        //transformer plugins.
        var validator = new ModValidator(modFilesMap, brokenFiles, discoveryErrorData, modFileLoadingExceptions);
        validator.stage1Validation();
        return validator;
    }

    private static void handleLocatedFiles(final List<ModFile> loadedFiles, final List<IModFile> locatedFiles, final Object locator) {
        for (IModFile mf : locatedFiles) {
            if (mf instanceof ModFile modFile) {
                LOGGER.info(LogMarkers.SCAN, "Found mod file {} of type {} with provider {}", mf.getFileName(), mf.getType(), mf.getProvider());
                loadedFiles.add(modFile);
            } else {
                LOGGER.error(LogMarkers.SCAN, "Skipping mod file {} found by {}, as it was not a ModFile instance!", mf.getFileName(), locator);
            }
        }
    }
}
