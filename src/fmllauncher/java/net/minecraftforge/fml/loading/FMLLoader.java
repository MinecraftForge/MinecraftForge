/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.INameMappingService;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.accesstransformer.service.AccessTransformerService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.progress.EarlyProgressVisualization;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.coremod.ICoreModProvider;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.SCAN;

public class FMLLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static AccessTransformerService accessTransformer;
    private static ModDiscoverer modDiscoverer;
    private static ICoreModProvider coreModProvider;
    private static ILaunchPluginService eventBus;
    private static LanguageLoadingProvider languageLoadingProvider;
    private static Dist dist;
    private static String naming;
    private static LoadingModList loadingModList;
    private static TransformingClassLoader launchClassLoader;
    private static RuntimeDistCleaner runtimeDistCleaner;
    private static Path gamePath;
    private static Path forgePath;
    private static Path[] mcPaths;
    static String mcVersion;
    private static String mcpVersion;
    static String forgeVersion;
    private static String forgeGroup;
    private static Predicate<String> classLoaderExclusions;
    private static String launchHandlerName;
    private static FMLCommonLaunchHandler commonLaunchHandler;
    public static Runnable progressWindowTick;
    public static BackgroundScanHandler backgroundScanHandler;
    private static boolean production;

    static void onInitialLoad(IEnvironment environment, Set<String> otherServices) throws IncompatibleEnvironmentException
    {
        final String version = LauncherVersion.getVersion();
        LOGGER.debug(CORE,"FML {} loading", version);
        final Package modLauncherPackage = ITransformationService.class.getPackage();
        LOGGER.debug(CORE,"FML found ModLauncher version: {}", modLauncherPackage.getImplementationVersion());
        if (!modLauncherPackage.isCompatibleWith("4.0")) {
            LOGGER.fatal(CORE,"Found incompatible ModLauncher specification: {}, version {} from {}", modLauncherPackage.getSpecificationVersion(), modLauncherPackage.getImplementationVersion(), modLauncherPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible modlauncher found "+modLauncherPackage.getSpecificationVersion());
        }
        LOGGER.debug(CORE, "Initializing modjar URL handler");
        URL.setURLStreamHandlerFactory(p->p.equals("modjar") ? new ModJarURLHandler() : null);

        accessTransformer = (AccessTransformerService) environment.findLaunchPlugin("accesstransformer").orElseThrow(()-> {
            LOGGER.fatal(CORE,"Access Transformer library is missing, we need this to run");
            return new IncompatibleEnvironmentException("Missing AccessTransformer, cannot run");
        });

        final Package atPackage = accessTransformer.getClass().getPackage();
        LOGGER.debug(CORE,"FML found AccessTransformer version: {}", atPackage.getImplementationVersion());
        if (!atPackage.isCompatibleWith("1.0")) {
            LOGGER.fatal(CORE,"Found incompatible AccessTransformer specification: {}, version {} from {}", atPackage.getSpecificationVersion(), atPackage.getImplementationVersion(), atPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible accesstransformer found "+atPackage.getSpecificationVersion());
        }

        eventBus = environment.findLaunchPlugin("eventbus").orElseThrow(()-> {
            LOGGER.fatal(CORE,"Event Bus library is missing, we need this to run");
            return new IncompatibleEnvironmentException("Missing EventBus, cannot run");
        });

        final Package eventBusPackage = eventBus.getClass().getPackage();
        LOGGER.debug(CORE,"FML found EventBus version: {}", eventBusPackage.getImplementationVersion());
        if (!eventBusPackage.isCompatibleWith("1.0")) {
            LOGGER.fatal(CORE,"Found incompatible EventBus specification: {}, version {} from {}", eventBusPackage.getSpecificationVersion(), eventBusPackage.getImplementationVersion(), eventBusPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible eventbus found "+eventBusPackage.getSpecificationVersion());
        }

        runtimeDistCleaner = (RuntimeDistCleaner)environment.findLaunchPlugin("runtimedistcleaner").orElseThrow(()-> {
            LOGGER.fatal(CORE,"Dist Cleaner is missing, we need this to run");
            return new IncompatibleEnvironmentException("Missing DistCleaner, cannot run!");
        });
        LOGGER.debug(CORE, "Found Runtime Dist Cleaner");

        final ArrayList<ICoreModProvider> coreModProviders = new ArrayList<>();
        ServiceLoaderStreamUtils.errorHandlingServiceLoader(ICoreModProvider.class, serviceConfigurationError -> LOGGER.fatal(CORE, "Failed to load a coremod library, expect problems", serviceConfigurationError)).forEach(coreModProviders::add);

        if (coreModProviders.isEmpty()) {
            LOGGER.fatal(CORE, "Found no coremod provider. Cannot run");
            throw new IncompatibleEnvironmentException("No coremod library found");
        } else if (coreModProviders.size() > 1) {
            LOGGER.fatal(CORE, "Found multiple coremod providers: {}. Cannot run", coreModProviders.stream().map(p -> p.getClass().getName()).collect(Collectors.toList()));
            throw new IncompatibleEnvironmentException("Multiple coremod libraries found");
        }

        coreModProvider = coreModProviders.get(0);
        final Package coremodPackage = coreModProvider.getClass().getPackage();
        LOGGER.debug(CORE,"FML found CoreMod version: {}", coremodPackage.getImplementationVersion());


        LOGGER.debug(CORE, "Found ForgeSPI package implementation version {}", Environment.class.getPackage().getImplementationVersion());
        LOGGER.debug(CORE, "Found ForgeSPI package specification {}", Environment.class.getPackage().getSpecificationVersion());
        if (Integer.parseInt(Environment.class.getPackage().getSpecificationVersion()) < 2) {
            LOGGER.fatal(CORE, "Found an out of date ForgeSPI implementation: {}, loading cannot continue", Environment.class.getPackage().getSpecificationVersion());
            throw new IncompatibleEnvironmentException("ForgeSPI is out of date, we cannot continue");
        }

        try {
            Class.forName("com.electronwill.nightconfig.core.Config", false, environment.getClass().getClassLoader());
            Class.forName("com.electronwill.nightconfig.toml.TomlFormat", false, environment.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.fatal(CORE, "Failed to load NightConfig");
            throw new IncompatibleEnvironmentException("Missing NightConfig");
        }
        FixSSL.fixup();
    }

    static void setupLaunchHandler(final IEnvironment environment, final Map<String, ?> arguments)
    {
        final String launchTarget = environment.getProperty(IEnvironment.Keys.LAUNCHTARGET.get()).orElse("MISSING");
        final Optional<ILaunchHandlerService> launchHandler = environment.findLaunchHandler(launchTarget);
        LOGGER.debug(CORE, "Using {} as launch service", launchTarget);
        if (!launchHandler.isPresent()) {
            LOGGER.fatal(CORE,"Missing LaunchHandler {}, cannot continue", launchTarget);
            throw new RuntimeException("Missing launch handler");
        }

        if (!(launchHandler.get() instanceof FMLCommonLaunchHandler)) {
            LOGGER.fatal(CORE, "Incompatible Launch handler found - type {}, cannot continue", launchHandler.get().getClass().getName());
            throw new RuntimeException("Incompatible launch handler found");
        }
        launchHandlerName = launchHandler.get().name();
        gamePath = environment.getProperty(IEnvironment.Keys.GAMEDIR.get()).orElse(Paths.get(".").toAbsolutePath());

        commonLaunchHandler = (FMLCommonLaunchHandler)launchHandler.get();
        naming = commonLaunchHandler.getNaming();
        dist = commonLaunchHandler.getDist();
        production = commonLaunchHandler.isProduction();

        mcVersion = (String) arguments.get("mcVersion");
        progressWindowTick = EarlyProgressVisualization.INSTANCE.accept(dist, commonLaunchHandler.isData(), mcVersion);
        StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Early Loading!"));
        accessTransformer.getExtension().accept(Pair.of(naming, "srg"));

        mcpVersion = (String) arguments.get("mcpVersion");
        forgeVersion = (String) arguments.get("forgeVersion");
        forgeGroup = (String) arguments.get("forgeGroup");

        LOGGER.debug(CORE,"Received command line version data: MC Version: '{}' MCP Version: '{}' Forge Version: '{}' Forge group: '{}'", mcVersion, mcpVersion, forgeVersion, forgeGroup);
        forgePath = commonLaunchHandler.getForgePath(mcVersion, forgeVersion, forgeGroup);
        mcPaths = commonLaunchHandler.getMCPaths(mcVersion, mcpVersion, forgeVersion, forgeGroup);

        commonLaunchHandler.validatePaths(forgePath, mcPaths, forgeVersion, mcVersion, mcpVersion);
        commonLaunchHandler.setup(environment, arguments);
        classLoaderExclusions = commonLaunchHandler.getPackagePredicate();

        languageLoadingProvider = new LanguageLoadingProvider();
        languageLoadingProvider.addForgeLanguage(forgePath);

        runtimeDistCleaner.getExtension().accept(dist);
        progressWindowTick.run();
    }
    public static Map<IModFile.Type, List<ModFile>> beginModScan(final Map<String,?> arguments)
    {
        LOGGER.debug(SCAN,"Scanning for Mod Locators");
        modDiscoverer = new ModDiscoverer(arguments);
        backgroundScanHandler = modDiscoverer.discoverMods();
        loadingModList = backgroundScanHandler.getLoadingModList();
        commonLaunchHandler.addLibraries(backgroundScanHandler.getModFiles().getOrDefault(IModFile.Type.LIBRARY, Collections.emptyList()));
        progressWindowTick.run();
        return loadingModList.getModFiles().stream().map(ModFileInfo::getFile).collect(Collectors.groupingBy(ModFile::getType));
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

    public static void addAccessTransformer(Path atPath, ModFile modName)
    {
        LOGGER.debug(SCAN, "Adding Access Transformer in {}", modName.getFilePath());
        accessTransformer.offerResource(atPath, modName.getFileName());
    }

    public static Dist getDist()
    {
        return dist;
    }

    public static void beforeStart(ITransformingClassLoader launchClassLoader)
    {
        FMLLoader.launchClassLoader = (TransformingClassLoader) launchClassLoader.getInstance();
        StartupMessageManager.modLoaderConsumer().ifPresent(c->c.accept("Launching minecraft"));
        progressWindowTick.run();
    }

    public static LoadingModList getLoadingModList()
    {
        return loadingModList;
    }

    public static TransformingClassLoader getLaunchClassLoader()
    {
        return launchClassLoader;
    }

    public static Path getGamePath()
    {
        return gamePath;
    }

    public static Path getForgePath() {
        return forgePath;
    }

    public static Path[] getMCPaths() {
        return mcPaths;
    }

    public static Predicate<String> getClassLoaderExclusions() {
        return classLoaderExclusions;
    }

    public static String getNaming() {
        return naming;
    }

    public static Optional<BiFunction<INameMappingService.Domain, String, String>> getNameFunction(final String naming) {
        return Launcher.INSTANCE.environment().findNameMapping(naming);
    }

    public static String getMcpVersion() {
        return mcpVersion;
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
        return Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.SECURED_JARS_ENABLED.get()).orElse(false);
    }
}
