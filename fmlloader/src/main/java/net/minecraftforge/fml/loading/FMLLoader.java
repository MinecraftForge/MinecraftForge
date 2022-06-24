/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.*;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import cpw.mods.modlauncher.util.ServiceLoaderUtils;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModValidator;
import net.minecraftforge.accesstransformer.service.AccessTransformerService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import net.minecraftforge.fml.loading.targets.CommonLaunchHandler;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.coremod.ICoreModProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class FMLLoader
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static AccessTransformerService accessTransformer;
    private static ModDiscoverer modDiscoverer;
    private static ICoreModProvider coreModProvider;
    private static ILaunchPluginService eventBus;
    private static LanguageLoadingProvider languageLoadingProvider;
    private static Dist dist;
    private static String naming;
    private static LoadingModList loadingModList;
    private static RuntimeDistCleaner runtimeDistCleaner;
    private static Path gamePath;
    private static VersionInfo versionInfo;
    private static String launchHandlerName;
    private static CommonLaunchHandler commonLaunchHandler;
    public static Runnable progressWindowTick;
    private static ModValidator modValidator;
    public static BackgroundScanHandler backgroundScanHandler;
    private static boolean production;
    private static IModuleLayerManager moduleLayerManager;

    static void onInitialLoad(IEnvironment environment, Set<String> otherServices) throws IncompatibleEnvironmentException
    {
        final String version = LauncherVersion.getVersion();
        LOGGER.debug(CORE,"FML {} loading", version);
        final Package modLauncherPackage = ITransformationService.class.getPackage();
        LOGGER.debug(CORE,"FML found ModLauncher version : {}", modLauncherPackage.getImplementationVersion());
        if (!modLauncherPackage.isCompatibleWith("4.0")) {
            LOGGER.error(CORE, "Found incompatible ModLauncher specification : {}, version {} from {}", modLauncherPackage.getSpecificationVersion(), modLauncherPackage.getImplementationVersion(), modLauncherPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible modlauncher found "+modLauncherPackage.getSpecificationVersion());
        }

        accessTransformer = (AccessTransformerService) environment.findLaunchPlugin("accesstransformer").orElseThrow(()-> {
            LOGGER.error(CORE, "Access Transformer library is missing, we need this to run");
            return new IncompatibleEnvironmentException("Missing AccessTransformer, cannot run");
        });

        final Package atPackage = accessTransformer.getClass().getPackage();
        LOGGER.debug(CORE,"FML found AccessTransformer version : {}", atPackage.getImplementationVersion());
        if (!atPackage.isCompatibleWith("1.0")) {
            LOGGER.error(CORE, "Found incompatible AccessTransformer specification : {}, version {} from {}", atPackage.getSpecificationVersion(), atPackage.getImplementationVersion(), atPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible accesstransformer found "+atPackage.getSpecificationVersion());
        }

        eventBus = environment.findLaunchPlugin("eventbus").orElseThrow(()-> {
            LOGGER.error(CORE, "Event Bus library is missing, we need this to run");
            return new IncompatibleEnvironmentException("Missing EventBus, cannot run");
        });

        final Package eventBusPackage = eventBus.getClass().getPackage();
        LOGGER.debug(CORE,"FML found EventBus version : {}", eventBusPackage.getImplementationVersion());
        if (!eventBusPackage.isCompatibleWith("1.0")) {
            LOGGER.error(CORE, "Found incompatible EventBus specification : {}, version {} from {}", eventBusPackage.getSpecificationVersion(), eventBusPackage.getImplementationVersion(), eventBusPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible eventbus found "+eventBusPackage.getSpecificationVersion());
        }

        runtimeDistCleaner = (RuntimeDistCleaner)environment.findLaunchPlugin("runtimedistcleaner").orElseThrow(()-> {
            LOGGER.error(CORE, "Dist Cleaner is missing, we need this to run");
            return new IncompatibleEnvironmentException("Missing DistCleaner, cannot run!");
        });
        LOGGER.debug(CORE, "Found Runtime Dist Cleaner");

        var coreModProviders = ServiceLoaderUtils.streamWithErrorHandling(ServiceLoader.load(FMLLoader.class.getModule().getLayer(), ICoreModProvider.class), sce -> LOGGER.error(CORE, "Failed to load a coremod library, expect problems", sce)).toList();

        if (coreModProviders.isEmpty()) {
            LOGGER.error(CORE, "Found no coremod provider. Cannot run");
            throw new IncompatibleEnvironmentException("No coremod library found");
        } else if (coreModProviders.size() > 1) {
            LOGGER.error(CORE, "Found multiple coremod providers : {}. Cannot run", coreModProviders.stream().map(p -> p.getClass().getName()).collect(Collectors.toList()));
            throw new IncompatibleEnvironmentException("Multiple coremod libraries found");
        }

        coreModProvider = coreModProviders.get(0);
        final Package coremodPackage = coreModProvider.getClass().getPackage();
        LOGGER.debug(CORE,"FML found CoreMod version : {}", coremodPackage.getImplementationVersion());


        LOGGER.debug(CORE, "Found ForgeSPI package implementation version {}", Environment.class.getPackage().getImplementationVersion());
        LOGGER.debug(CORE, "Found ForgeSPI package specification {}", Environment.class.getPackage().getSpecificationVersion());
        if (Integer.parseInt(Environment.class.getPackage().getSpecificationVersion()) < 2) {
            LOGGER.error(CORE, "Found an out of date ForgeSPI implementation: {}, loading cannot continue", Environment.class.getPackage().getSpecificationVersion());
            throw new IncompatibleEnvironmentException("ForgeSPI is out of date, we cannot continue");
        }

        try {
            Class.forName("com.electronwill.nightconfig.core.Config", false, environment.getClass().getClassLoader());
            Class.forName("com.electronwill.nightconfig.toml.TomlFormat", false, environment.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error(CORE, "Failed to load NightConfig");
            throw new IncompatibleEnvironmentException("Missing NightConfig");
        }
    }

    static void setupLaunchHandler(final IEnvironment environment, final Map<String, Object> arguments)
    {
        final String launchTarget = environment.getProperty(IEnvironment.Keys.LAUNCHTARGET.get()).orElse("MISSING");
        arguments.put("launchTarget", launchTarget);
        final Optional<ILaunchHandlerService> launchHandler = environment.findLaunchHandler(launchTarget);
        LOGGER.debug(CORE, "Using {} as launch service", launchTarget);
        if (launchHandler.isEmpty()) {
            LOGGER.error(CORE, "Missing LaunchHandler {}, cannot continue", launchTarget);
            throw new RuntimeException("Missing launch handler: " + launchTarget);
        }

        if (!(launchHandler.get() instanceof CommonLaunchHandler)) {
            LOGGER.error(CORE, "Incompatible Launch handler found - type {}, cannot continue", launchHandler.get().getClass().getName());
            throw new RuntimeException("Incompatible launch handler found");
        }
        commonLaunchHandler = (CommonLaunchHandler)launchHandler.get();
        launchHandlerName = launchHandler.get().name();
        gamePath = environment.getProperty(IEnvironment.Keys.GAMEDIR.get()).orElse(Paths.get(".").toAbsolutePath());

        naming = commonLaunchHandler.getNaming();
        dist = commonLaunchHandler.getDist();
        production = commonLaunchHandler.isProduction();

        versionInfo = new VersionInfo(arguments);

        accessTransformer.getExtension().accept(Pair.of(naming, "srg"));

        LOGGER.debug(CORE,"Received command line version data  : {}", versionInfo);

        runtimeDistCleaner.getExtension().accept(dist);
    }
    public static List<ITransformationService.Resource> beginModScan(final Map<String,?> arguments)
    {
        LOGGER.debug(SCAN,"Scanning for Mod Locators");
        modDiscoverer = new ModDiscoverer(arguments);
        modValidator = modDiscoverer.discoverMods();
        var pluginResources = modValidator.getPluginResources();
        return List.of(pluginResources);
    }

    public static List<ITransformationService.Resource> completeScan(IModuleLayerManager layerManager) {
        moduleLayerManager = layerManager;
        languageLoadingProvider = new LanguageLoadingProvider();
        backgroundScanHandler = modValidator.stage2Validation();
        loadingModList = backgroundScanHandler.getLoadingModList();
        return List.of(modValidator.getModResources());
    }

    public static ICoreModProvider getCoreModProvider() {
        return coreModProvider;
    }

    public static LanguageLoadingProvider getLanguageLoadingProvider()
    {
        return languageLoadingProvider;
    }

    static ModDiscoverer getModDiscoverer() {
        return modDiscoverer;
    }

    public static CommonLaunchHandler getLaunchHandler() {
        return commonLaunchHandler;
    }

    public static void addAccessTransformer(Path atPath, ModFile modName)
    {
        LOGGER.debug(SCAN, "Adding Access Transformer in {}", modName.getFilePath());
        accessTransformer.offerResource(atPath, modName.getFileName());
    }

    public static Dist getDist()
    {
        return dist;
    }

    public static void beforeStart(ModuleLayer gameLayer)
    {
        ImmediateWindowHandler.acceptGameLayer(gameLayer);
        ImmediateWindowHandler.updateProgress("Launching minecraft");
        progressWindowTick.run();
    }

    public static LoadingModList getLoadingModList()
    {
        return loadingModList;
    }

    public static Path getGamePath()
    {
        return gamePath;
    }

    public static String getNaming() {
        return naming;
    }

    public static Optional<BiFunction<INameMappingService.Domain, String, String>> getNameFunction(final String naming) {
        return Launcher.INSTANCE.environment().findNameMapping(naming);
    }

    public static String getLauncherInfo() {
        return Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.MLIMPL_VERSION.get()).orElse("MISSING");
    }

    public static List<Map<String, String>> modLauncherModList() {
        return Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.MODLIST.get()).orElseGet(Collections::emptyList);
    }

    public static String launcherHandlerName() {
        return launchHandlerName;
    }

    public static boolean isProduction() {
        return production;
    }

    public static boolean isSecureJarEnabled() {
        return true;
    }

    public static ModuleLayer getGameLayer() {
        return moduleLayerManager.getLayer(IModuleLayerManager.Layer.GAME).orElseThrow();
    }

    public static VersionInfo versionInfo() {
        return versionInfo;
    }
}
