/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.fml.loading.EarlyLoadingException;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.UniqueModListBuilder;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ModDiscoverer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServiceLoader<IModLocator> modLocators;
    private final List<IModLocator>          modLocatorList;

    public ModDiscoverer(Map<String, ?> arguments) {
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.MODDIRECTORYFACTORY.get(), v->ModsFolderLocator::new);
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Environment.Keys.PROGRESSMESSAGE.get(), v-> StartupMessageManager.locatorConsumer().orElseGet(()-> s->{}));
        final var moduleLayerManager = Launcher.INSTANCE.environment().findModuleLayerManager().orElseThrow();
        modLocators = ServiceLoader.load(moduleLayerManager.getLayer(IModuleLayerManager.Layer.SERVICE).orElseThrow(), IModLocator.class);
        modLocatorList = ServiceLoaderUtils.streamServiceLoader(()-> modLocators, sce->LOGGER.error("Failed to load mod locator list", sce)).collect(Collectors.toList());
        modLocatorList.forEach(l->l.initArguments(arguments));
        if (LOGGER.isDebugEnabled(LogMarkers.CORE))
        {
            LOGGER.debug(LogMarkers.CORE, "Found Mod Locators : {}", modLocatorList.stream()
                                                                       .map(modLocator -> "(%s:%s)".formatted(modLocator.name(),
                                                                         modLocator.getClass().getPackage().getImplementationVersion())).collect(Collectors.joining(",")));
        }
    }

    public ModValidator discoverMods() {
        LOGGER.debug(LogMarkers.SCAN,"Scanning for mods and other resources to load. We know {} ways to find mods", modLocatorList.size());
        List<ModFile> loadedFiles = new ArrayList<>();
        List<EarlyLoadingException.ExceptionData> discoveryErrorData = new ArrayList<>();
        boolean successfullyLoadedMods = true;

        //Loop all mod locators to get the prime mods to load from.
        for (IModLocator locator : modLocatorList) {
            try {
                LOGGER.debug(LogMarkers.SCAN,"Trying locator {}", locator);
                var locatedFiles = locator.scanMods();

                if (locatedFiles.stream().anyMatch(file -> !(file instanceof ModFile))) {
                    LOGGER.error(LogMarkers.SCAN, "A mod locator returned a file which is not a ModFile instance!. They will be skipped!");
                }

                handleLocatedFiles(loadedFiles, locatedFiles);
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
            for (IModLocator locator : modLocatorList) {
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
        var validator = new ModValidator(modFilesMap, discoveryErrorData);
        validator.stage1Validation();
        return validator;
    }

    private void handleLocatedFiles(final List<ModFile> loadedFiles, final List<IModFile> locatedFiles)
    {
        var locatedModFiles = locatedFiles.stream().filter(ModFile.class::isInstance).map(ModFile.class::cast).toList();
        for (IModFile mf : locatedModFiles) {
            LOGGER.info(LogMarkers.SCAN, "Found mod file {} of type {} with provider {}", mf.getFileName(), mf.getType(), mf.getLocator());
            StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Found mod file "+mf.getFileName()+" of type "+mf.getType()));
        }
        loadedFiles.addAll(locatedModFiles);
    }
}
