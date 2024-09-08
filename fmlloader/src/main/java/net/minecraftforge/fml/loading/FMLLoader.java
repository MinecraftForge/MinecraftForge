/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.*;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModValidator;
import net.minecraftforge.accesstransformer.service.AccessTransformerService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.targets.CommonLaunchHandler;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.coremod.ICoreModProvider;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.BiFunction;
import static net.minecraftforge.fml.loading.LogMarkers.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class FMLLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static AccessTransformerService accessTransformer;
    private static ICoreModProvider coreModProvider;
    private static LanguageLoadingProvider languageLoadingProvider;
    private static Dist dist;
    private static String naming;
    private static LoadingModList loadingModList;
    private static RuntimeDistCleaner runtimeDistCleaner;
    private static Path gamePath;
    private static final VersionInfo versionInfo = VersionInfo.detect();
    private static String launchHandlerName;
    private static CommonLaunchHandler commonLaunchHandler;
    public static Runnable progressWindowTick;
    private static ModValidator modValidator;
    public static BackgroundScanHandler backgroundScanHandler;
    private static boolean production;
    private static IModuleLayerManager moduleLayerManager;

    static void onInitialLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
        LOGGER.debug(CORE, "Detected version data : {}", versionInfo);
        LOGGER.debug(CORE, "FML {} loading", LauncherVersion.getVersion());

        checkPackage(ITransformationService.class, "4.0", "ModLauncher");
        accessTransformer  = getPlugin(env, "accesstransformer",  "1.0", "AccessTransformer");
        /*eventBus       =*/ getPlugin(env, "eventbus",           "1.0", "EventBus");
        runtimeDistCleaner = getPlugin(env, "runtimedistcleaner", "1.0", "RuntimeDistCleaner");
        coreModProvider = getSingleService(ICoreModProvider.class, "CoreMod");
        LOGGER.debug(CORE, "FML found CoreMod version : {}", JarVersionLookupHandler.getInfo(coreModProvider.getClass()).impl().version().orElse("MISSING"));
        checkPackage(Environment.class, "2.0", "ForgeSPI");

        try {
            Class.forName("com.electronwill.nightconfig.core.Config", false, env.getClass().getClassLoader());
            Class.forName("com.electronwill.nightconfig.toml.TomlFormat", false, env.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error(CORE, "Failed to load NightConfig");
            throw new IncompatibleEnvironmentException("Missing NightConfig");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getPlugin(IEnvironment env, String id, String version, String name) throws IncompatibleEnvironmentException {
        var plugin = env.findLaunchPlugin(id).orElse(null);
        if (plugin == null) {
            LOGGER.error(CORE, "{} library is missing, we need this to run", name);
            throw new IncompatibleEnvironmentException("Missing " + name + ", cannot run");
        }
        checkPackage(plugin.getClass(), version, name);
        return (T) plugin;
    }

    private static void checkPackage(Class<?> cls, String version, String name) throws IncompatibleEnvironmentException {
        var pkg = cls.getPackage();
        var info = JarVersionLookupHandler.getInfo(pkg);
        LOGGER.debug(CORE, "Found {} version: {}", name, info.impl().version().orElse("MISSING"));

        if (!pkg.isCompatibleWith(version)) {
            LOGGER.error(CORE, "Found incompatible {} specification: {}, version {} from {}", name,
                 info.spec().version().orElse("MISSING"),
                 info.impl().version().orElse("MISSING"),
                 info.impl().vendor().orElse("MISSING")
             );
            throw new IncompatibleEnvironmentException("Incompatible " + name + " found " + info.spec().version().orElse("MISSING"));
        }
    }

    private static <T> T getSingleService(Class<T> clazz, String name) throws IncompatibleEnvironmentException {
          var providers = new ArrayList<T>();
          for (var itr = ServiceLoader.load(FMLLoader.class.getModule().getLayer(), clazz).iterator(); itr.hasNext(); ) {
              try {
                  providers.add(itr.next());
              } catch (ServiceConfigurationError e) {
                  LOGGER.error(CORE, "Failed to load a " + name + " library, expect problems", e);
              }
          }

          if (providers.isEmpty()) {
              LOGGER.error(CORE, "Found no {} provider. Cannot run", name);
              throw new IncompatibleEnvironmentException("No " + name + " library found");
          } else if (providers.size() > 1) {
              LOGGER.error(CORE, "Found multiple {} providers: {}. Cannot run", name, providers.stream().map(p -> p.getClass().getName()).toList());
              throw new IncompatibleEnvironmentException("Multiple " + name + " libraries found");
          }

          return providers.getFirst();
    }

    static void setupLaunchHandler(final IEnvironment environment, final Map<String, Object> arguments) {
        final String launchTarget = environment.getProperty(IEnvironment.Keys.LAUNCHTARGET.get()).orElse("MISSING");
        arguments.put("launchTarget", launchTarget);
        final Optional<ILaunchHandlerService> launchHandler = environment.findLaunchHandler(launchTarget);
        LOGGER.debug(CORE, "Using {} as launch service", launchTarget);
        if (launchHandler.isEmpty()) {
            LOGGER.error(CORE, "Missing LaunchHandler {}, cannot continue", launchTarget);
            throw new RuntimeException("Missing launch handler: " + launchTarget);
        }

        // TODO: [FML][Loader] What the fuck is the point of using a service if you require a specific concrete class
        if (!(launchHandler.get() instanceof CommonLaunchHandler)) {
            LOGGER.error(CORE, "Incompatible Launch handler found - type {}, cannot continue", launchHandler.get().getClass().getName());
            throw new RuntimeException("Incompatible launch handler found");
        }
        commonLaunchHandler = (CommonLaunchHandler)launchHandler.get();
        launchHandlerName = launchHandler.get().name();
        gamePath = environment.getProperty(IEnvironment.Keys.GAMEDIR.get()).orElse(Path.of(".").toAbsolutePath());

        naming = commonLaunchHandler.getNaming();
        dist = commonLaunchHandler.getDist();
        production = commonLaunchHandler.isProduction();

        accessTransformer.getExtension().accept(Map.entry(naming, "srg"));

        runtimeDistCleaner.getExtension().accept(dist);
    }

    public static List<ITransformationService.Resource> beginModScan(final Map<String,?> arguments) {
        LOGGER.debug(SCAN,"Scanning for Mod Locators");
        var modDiscoverer = new ModDiscoverer(arguments);
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

    public static LanguageLoadingProvider getLanguageLoadingProvider() {
        return languageLoadingProvider;
    }

    public static CommonLaunchHandler getLaunchHandler() {
        return commonLaunchHandler;
    }

    public static void addAccessTransformer(Path atPath, ModFile modName) {
        LOGGER.debug(SCAN, "Adding Access Transformer in {}", modName.getFilePath());
        accessTransformer.offerResource(atPath, modName.getFileName());
    }

    public static Dist getDist() {
        return dist;
    }

    public static void beforeStart(ModuleLayer gameLayer) {
        ImmediateWindowHandler.acceptGameLayer(gameLayer);
        ImmediateWindowHandler.updateProgress("Launching minecraft");
        progressWindowTick.run();
    }

    public static LoadingModList getLoadingModList() {
        return loadingModList;
    }

    public static Path getGamePath() {
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
        return Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.MODLIST.get()).orElse(List.of());
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
