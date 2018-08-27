/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.loading.moddiscovery.BackgroundScanHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.ICoreModProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.Logging.SCAN;

public class FMLLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static ILaunchPluginService accessTransformer;
    private static ModDiscoverer modDiscoverer;
    private static ICoreModProvider coreModProvider;
    private static ILaunchPluginService eventBus;
    private static LanguageLoadingProvider languageLoadingProvider;
    private static Dist dist;
    private static LoadingModList loadingModList;
    private static ClassLoader launchClassLoader;
    private static RuntimeDistCleaner runtimeDistCleaner;
    private static Path gamePath;
    private static Path forgePath;

    static void onInitialLoad(IEnvironment environment, Set<String> otherServices) throws IncompatibleEnvironmentException
    {
        final String version = ForgeVersion.getVersion();
        LOGGER.debug(CORE,"FML {} loading", version);
        final Package modLauncherPackage = ITransformationService.class.getPackage();
        LOGGER.debug(CORE,"FML found ModLauncher version : {}", modLauncherPackage.getImplementationVersion());
        if (!modLauncherPackage.isCompatibleWith("1.0")) {
            LOGGER.error(CORE,"Found incompatible ModLauncher specification : {}, version {} from {}", modLauncherPackage.getSpecificationVersion(), modLauncherPackage.getImplementationVersion(), modLauncherPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible modlauncher found "+modLauncherPackage.getSpecificationVersion());
        }
        LOGGER.debug(CORE, "Initializing modjar URL handler");
        URL.setURLStreamHandlerFactory(p->p.equals("modjar") ? new ModJarURLHandler() : null);

        accessTransformer = environment.findLaunchPlugin("accesstransformer").orElseThrow(()-> new IncompatibleEnvironmentException("Missing AccessTransformer, cannot run"));

        final Package atPackage = accessTransformer.getClass().getPackage();
        LOGGER.debug(CORE,"FML found AccessTransformer version : {}", atPackage.getImplementationVersion());
        if (!atPackage.isCompatibleWith("1.0")) {
            LOGGER.error(CORE,"Found incompatible AccessTransformer specification : {}, version {} from {}", atPackage.getSpecificationVersion(), atPackage.getImplementationVersion(), atPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible accesstransformer found "+atPackage.getSpecificationVersion());
        }

        eventBus = environment.findLaunchPlugin("eventbus").orElseThrow(()-> new IncompatibleEnvironmentException("Missing EventBus, cannot run"));

        final Package eventBusPackage = eventBus.getClass().getPackage();
        LOGGER.debug(CORE,"FML found EventBus version : {}", eventBusPackage.getImplementationVersion());
        if (!eventBusPackage.isCompatibleWith("1.0")) {
            LOGGER.error(CORE,"Found incompatible EventBus specification : {}, version {} from {}", eventBusPackage.getSpecificationVersion(), eventBusPackage.getImplementationVersion(), eventBusPackage.getImplementationVendor());
            throw new IncompatibleEnvironmentException("Incompatible eventbus found "+eventBusPackage.getSpecificationVersion());
        }

        runtimeDistCleaner = (RuntimeDistCleaner)environment.findLaunchPlugin("runtimedistcleaner").orElseThrow(()->new IncompatibleEnvironmentException("Missing RuntimeDistCleaner, cannot run!"));
        LOGGER.debug(CORE, "Found Runtime Dist Cleaner");

        final ArrayList<ICoreModProvider> coreModProviders = new ArrayList<>();
        ServiceLoader.load(ICoreModProvider.class).forEach(coreModProviders::add);

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

        languageLoadingProvider = new LanguageLoadingProvider();
    }

    static void setupLaunchHandler(final IEnvironment environment)
    {
        final String launchTarget = environment.getProperty(IEnvironment.Keys.LAUNCHTARGET.get()).orElse("MISSING");
        final Optional<ILaunchHandlerService> launchHandler = environment.findLaunchHandler(launchTarget);
        LOGGER.debug(CORE, "Using {} as launch service", launchTarget);
        if (!launchHandler.isPresent()) {
            LOGGER.error(CORE,"Missing LaunchHandler {}, cannot continue", launchTarget);
            throw new RuntimeException("Missing launch handler");
        }

        if (!(launchHandler.get() instanceof FMLCommonLaunchHandler)) {
            LOGGER.error(CORE, "Incompatible Launch handler found - type {}, cannot continue", launchHandler.get().getClass().getName());
            throw new RuntimeException("Incompatible launch handler found");
        }
        gamePath = environment.getProperty(IEnvironment.Keys.GAMEDIR.get()).orElse(Paths.get(".").toAbsolutePath());

        FMLCommonLaunchHandler commonLaunchHandler = (FMLCommonLaunchHandler)launchHandler.get();
        commonLaunchHandler.setup(environment);
        dist = commonLaunchHandler.getDist();
        runtimeDistCleaner.getExtension().accept(dist);
    }
    public static void beginModScan()
    {
        LOGGER.debug(SCAN,"Scanning for Mod Locators");
        modDiscoverer = new ModDiscoverer();
        final BackgroundScanHandler backgroundScanHandler = modDiscoverer.discoverMods();
        loadingModList = backgroundScanHandler.getLoadingModList();
    }

    public static ICoreModProvider getCoreModProvider() {
        return coreModProvider;
    }

    public static LanguageLoadingProvider getLanguageLoadingProvider()
    {
        return languageLoadingProvider;
    }

    public static void loadAccessTransformer()
    {
        final URL resource = FMLLoader.class.getClassLoader().getResource("forge_at.cfg");
        if (resource == null) {
            throw new RuntimeException("Missing forge_at.cfg file");
        }
        try
        {
            LOGGER.debug(CORE, "Loading forge_at.cfg into access transformer");
            accessTransformer.addResource(Paths.get(resource.toURI()), "forge_at.cfg");
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Error loading forge_at.cfg file", e);
            throw new RuntimeException(e);
        }
    }

    public static void addAccessTransformer(Path atPath, ModFile modName)
    {
        LOGGER.debug(SCAN, "Adding Access Transformer in {}", modName.getFilePath());
        accessTransformer.addResource(atPath, modName.getFileName());
    }

    public static Dist getDist()
    {
        return dist;
    }

    public static void beforeStart(ITransformingClassLoader launchClassLoader, Path forgePath)
    {
        FMLLoader.launchClassLoader = launchClassLoader.getInstance();
        FMLLoader.forgePath = forgePath;
    }


    public static LoadingModList getLoadingModList()
    {
        return loadingModList;

    }

    public static ClassLoader getLaunchClassLoader()
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
}
