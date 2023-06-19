/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.fml.loading.EarlyLoadingException;
import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.UniqueModListBuilder;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ModDiscoverer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ServiceLoader<IModLocator> modLocators;
    private final ServiceLoader<IDependencyLocator> dependencyLocators;
    private final List<IModLocator>          modLocatorList;
    private final List<IDependencyLocator>   dependencyLocatorList;

    public ModDiscoverer(Map<String, ?> arguments) {
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.MODDIRECTORYFACTORY.get(), v->ModsFolderLocator::new);
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.PROGRESSMESSAGE.get(), v-> StartupNotificationManager.locatorConsumer().orElseGet(()-> s->{}));
        final var moduleLayerManager = Launcher.INSTANCE.environment().findModuleLayerManager().orElseThrow();
        modLocators = ServiceLoader.load(moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE).orElseThrow(), IModLocator.class);
        dependencyLocators = ServiceLoader.load(moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE).orElseThrow(), IDependencyLocator.class);
        modLocatorList = ServiceLoaderUtils.streamServiceLoader(()-> modLocators, sce->LOGGER.error("Failed to load mod locator list", sce)).collect(Collectors.toList());
        modLocatorList.forEach(l->l.initArguments(arguments));
        dependencyLocatorList = ServiceLoaderUtils.streamServiceLoader(()-> dependencyLocators, sce->LOGGER.error("Failed to load dependency locator list", sce)).collect(Collectors.toList());
        dependencyLocatorList.forEach(l->l.initArguments(arguments));
        if (LOGGER.isDebugEnabled(LogMarkers.CORE))
        {
            LOGGER.debug(LogMarkers.CORE, "Found Mod Locators : {}", modLocatorList.stream()
                                                                       .map(modLocator -> "(%s:%s)".formatted(modLocator.name(),
                                                                         modLocator.getClass().getPackage().getImplementationVersion())).collect(Collectors.joining(",")));
        }
        if (LOGGER.isDebugEnabled(LogMarkers.CORE))
        {
            LOGGER.debug(LogMarkers.CORE, "Found Dependency Locators : {}", dependencyLocatorList.stream()
                                                                       .map(dependencyLocator -> "(%s:%s)".formatted(dependencyLocator.name(),
                                                                         dependencyLocator.getClass().getPackage().getImplementationVersion())).collect(Collectors.joining(",")));
        }
    }

    public ModValidator discoverMods() {
        LOGGER.debug(LogMarkers.SCAN,"Scanning for mods and other resources to load. We know {} ways to find mods", modLocatorList.size());
        List<ModFile> loadedFiles = new ArrayList<>();
        List<EarlyLoadingException.ExceptionData> discoveryErrorData = new ArrayList<>();
        boolean successfullyLoadedMods = true;
        List<IModFileInfo> brokenFiles = new ArrayList<>();
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
                    brokenFiles.addAll(exceptions.stream().map(e->e instanceof InvalidModFileException ime ? ime.getBrokenFile() : null).filter(Objects::nonNull).toList());
                }
                var locatedFiles = candidates.stream().map(IModLocator.ModFileOrException::file).filter(Objects::nonNull).collect(Collectors.toList());

                var badModFiles = locatedFiles.stream().filter(file -> !(file instanceof ModFile)).toList();
                if (!badModFiles.isEmpty()) {
                    LOGGER.error(LogMarkers.SCAN, "Locator {} returned {} files which is are not ModFile instances! They will be skipped!", locator, badModFiles.size());
                    brokenFiles.addAll(badModFiles.stream().map(IModFile::getModFileInfo).toList());
                }
                locatedFiles.removeAll(badModFiles);
                LOGGER.debug(LogMarkers.SCAN, "Locator {} found {} valid mod files", locator, locatedFiles.size());
                handleLocatedFiles(loadedFiles, locatedFiles);
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
        Map<IModFile.Type, List<ModFile>> modFilesMap = Maps.newHashMap();
        try {
            final UniqueModListBuilder modsUniqueListBuilder = new UniqueModListBuilder(loadedFiles);
            final UniqueModListBuilder.UniqueModListData uniqueModsData = modsUniqueListBuilder.buildUniqueList();

            //Grab the temporary results.
            //This allows loading to continue to a base state, in case dependency loading fails.
            modFilesMap = uniqueModsData.modFiles().stream()
                            .collect(Collectors.groupingBy(IModFile::getType));
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
                    final List<IModFile> locatedMods = ImmutableList.copyOf(loadedFiles);

                    var locatedFiles = locator.scanMods(locatedMods);
                    if (locatedFiles.stream().anyMatch(file -> !(file instanceof ModFile))) {
                        LOGGER.error(LogMarkers.SCAN, "A dependency locator returned a file which is not a ModFile instance!. They will be skipped!");
                    }

                    handleLocatedFiles(loadedFiles, locatedFiles);
                }
                catch (EarlyLoadingException exception) {
                    LOGGER.error(LogMarkers.SCAN, "Failed to load dependencies with locator {}", locator, exception);
                    discoveryErrorData.addAll(exception.getAllData());
                }
            }

            //Second processing run of the mod list. Any duplicates will cause resolution failure and only the mods list will be loaded.
            try {
                final UniqueModListBuilder modsAndDependenciesUniqueListBuilder = new UniqueModListBuilder(loadedFiles);
                final UniqueModListBuilder.UniqueModListData uniqueModsAndDependenciesData = modsAndDependenciesUniqueListBuilder.buildUniqueList();

                //We now only need the mod files map, not the list.
                modFilesMap = uniqueModsAndDependenciesData.modFiles().stream()
                                .collect(Collectors.groupingBy(IModFile::getType));
            } catch (EarlyLoadingException exception) {
                LOGGER.error(LogMarkers.SCAN, "Failed to build unique mod list after dependency discovery.", exception);
                discoveryErrorData.addAll(exception.getAllData());
                modFilesMap = loadedFiles.stream().collect(Collectors.groupingBy(IModFile::getType));
            }
        }
        else {
            //Failure notify the listeners.
            LOGGER.error(LogMarkers.SCAN, "Mod Discovery failed. Skipping dependency discovery.");
        }

        //Validate the loading. With a deduplicated list, we can now successfully process the artifacts and load
        //transformer plugins.
        var validator = new ModValidator(modFilesMap, brokenFiles, discoveryErrorData);
        validator.stage1Validation();
        return validator;
    }

    private void handleLocatedFiles(final List<ModFile> loadedFiles, final List<IModFile> locatedFiles)
    {
        var locatedModFiles = locatedFiles.stream().filter(ModFile.class::isInstance).map(ModFile.class::cast).toList();
        for (IModFile mf : locatedModFiles) {
            LOGGER.info(LogMarkers.SCAN, "Found mod file {} of type {} with provider {}", mf.getFileName(), mf.getType(), mf.getProvider());
        }
        loadedFiles.addAll(locatedModFiles);
    }
}
