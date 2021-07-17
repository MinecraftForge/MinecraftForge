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

package net.minecraftforge.fml;

import com.google.common.collect.ImmutableList;
import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.InvalidModIdentifier;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.Logging.LOADING;

/**
 * Loads mods.
 *
 * Dispatch cycle is seen in {@link #loadMods()} and {@link #finishMods()}
 *
 * Overall sequence for loadMods is:
 * <dl>
 *     <dt>CONSTRUCT</dt>
 *     <dd>Constructs the mod instance. Mods can typically setup basic environment such as Event listeners
 *     and Configuration specifications here.</dd>
 *     <dt>Automated dispatches</dt>
 *     <dd>Dispatches automated elements : {@link net.minecraftforge.fml.common.Mod.EventBusSubscriber},
 *     {@link net.minecraftforge.event.RegistryEvent}, {@link net.minecraftforge.common.capabilities.CapabilityInject}
 *     and others</dd>
 *     <dt>CONFIG_LOAD</dt>
 *     <dd>Dispatches ConfigLoadEvent to mods</dd>
 *     <dt>COMMON_SETUP</dt>
 *     <dd>Dispatches {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent} to mods</dd>
 *     <dt>SIDED_SETUP</dt>
 *     <dd>Dispatches {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent} or
 *     {@link net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent} to mods</dd>
 * </dl>
 *
 * Overall sequence for finishMods is:
 * <dl>
 *     <dt>ENQUEUE_IMC</dt>
 *     <dd>Dispatches {@link net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent} to mods,
 *     for enqueuing {@link InterModComms} messages for other mods to receive subsequently</dd>
 *     <dt>PROCESS_IMC</dt>
 *     <dd>Dispatches {@link net.minecraftforge.fml.event.lifecycle.InterModProcessEvent} to mods,
 *     for processing {@link InterModComms} messages received from other mods prior to this event</dd>
 *     <dt>COMPLETE</dt>
 *     <dd>Dispatches {@link net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent} to mods,
 *     and completes the mod loading sequence.</dd>
 * </dl>
 */
public class ModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static ModLoader INSTANCE;
    private final TransformingClassLoader launchClassLoader;
    private final LoadingModList loadingModList;

    private final List<ModLoadingException> loadingExceptions;
    private final List<ModLoadingWarning> loadingWarnings;
    private boolean loadingStateValid;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Consumer<String>> statusConsumer = StartupMessageManager.modLoaderConsumer();

    private ModLoader()
    {
        INSTANCE = this;
        this.launchClassLoader = FMLLoader.getLaunchClassLoader();
        this.loadingModList = FMLLoader.getLoadingModList();
        this.loadingExceptions = FMLLoader.getLoadingModList().
                getErrors().stream().flatMap(ModLoadingException::fromEarlyException).collect(Collectors.toList());
        this.loadingWarnings = FMLLoader.getLoadingModList().
                getBrokenFiles().stream().map(file -> new ModLoadingWarning(null, ModLoadingStage.VALIDATE,
                    InvalidModIdentifier.identifyJarProblem(file.getFilePath()).orElse("fml.modloading.brokenfile"), file.getFileName())).collect(Collectors.toList());
        FMLLoader.getLoadingModList()
                .getModFiles().stream().filter(ModFileInfo::missingLicense) //Search for files with missing licenses
                .filter(modFileInfo -> modFileInfo.getMods().stream().noneMatch(thisModInfo -> this.loadingExceptions.stream().map(ModLoadingException::getModInfo).anyMatch(otherInfo -> otherInfo == thisModInfo))) //Ignore files where any other mod already encountered an error
                .map(modFileInfo -> new ModLoadingException(null, ModLoadingStage.VALIDATE, "fml.modloading.missinglicense", null, modFileInfo.getFile()))
                .forEach(this.loadingExceptions::add);
        LOGGER.debug(CORE, "Loading Network data for FML net version: {}", FMLNetworkConstants.init());
        CrashReportExtender.registerCrashCallable("ModLauncher", FMLLoader::getLauncherInfo);
        CrashReportExtender.registerCrashCallable("ModLauncher launch target", FMLLoader::launcherHandlerName);
        CrashReportExtender.registerCrashCallable("ModLauncher naming", FMLLoader::getNaming);
        CrashReportExtender.registerCrashCallable("ModLauncher services", this::computeModLauncherServiceList);
        CrashReportExtender.registerCrashCallable("FML", ForgeVersion::getSpec);
        CrashReportExtender.registerCrashCallable("Forge", ()->ForgeVersion.getGroup()+":"+ForgeVersion.getVersion());
        CrashReportExtender.registerCrashCallable("FML Language Providers", this::computeLanguageList);
    }

    private String computeLanguageList() {
        return "\n"+FMLLoader.getLanguageLoadingProvider().applyForEach(lp->lp.name() +"@"+ lp.getClass().getPackage().getImplementationVersion()).collect(Collectors.joining("\n\t\t", "\t\t", ""));
    }

    private String computeModLauncherServiceList() {
        final List<Map<String, String>> mods = FMLLoader.modLauncherModList();
        return "\n"+mods.stream().map(mod->mod.getOrDefault("file","nofile")+
                " "+mod.getOrDefault("name", "missing")+
                " "+mod.getOrDefault("type","NOTYPE")+
                " "+mod.getOrDefault("description", "")).
                collect(Collectors.joining("\n\t\t","\t\t",""));
    }

    public static ModLoader get()
    {
        return INSTANCE == null ? INSTANCE = new ModLoader() : INSTANCE;
    }

    /**
     * Run on the primary starting thread by ClientModLoader and ServerModLoader
     * @param syncExecutor An executor to run tasks on the main thread
     * @param parallelExecutor An executor to run tasks on a parallel loading thread pool
     * @param periodicTask Optional periodic task to perform on the main thread while other activities run
     */
    public void gatherAndInitializeMods(final ModWorkManager.DrivenExecutor syncExecutor, final Executor parallelExecutor, final Runnable periodicTask) {
        loadingStateValid = true;
        statusConsumer.ifPresent(c->c.accept("Waiting for scan to complete"));
        FMLLoader.backgroundScanHandler.waitForScanToComplete(periodicTask);
        statusConsumer.ifPresent(c->c.accept("Loading mods"));
        final ModList modList = ModList.of(loadingModList
                .getModFiles()
                .stream()
                .map(ModFileInfo::getFile)
                .collect(Collectors.toList()),
                loadingModList.getMods());
        if (!this.loadingExceptions.isEmpty()) {
            LOGGER.fatal(CORE, "Error during pre-loading phase", loadingExceptions.get(0));
            modList.setLoadedMods(Collections.emptyList());
            loadingStateValid = false;
            throw new LoadingFailedException(loadingExceptions);
        }
        statusConsumer.ifPresent(c->c.accept("Building Mod List"));
        final List<ModContainer> modContainers = loadingModList.getModFiles().stream().
                map(ModFileInfo::getFile).
                map(mf -> buildMods(mf, launchClassLoader)).
                flatMap(Collection::stream).
                collect(Collectors.toList());
        if (!loadingExceptions.isEmpty()) {
            LOGGER.fatal(CORE, "Failed to initialize mod containers", loadingExceptions.get(0));
            modList.setLoadedMods(Collections.emptyList());
            loadingStateValid = false;
            throw new LoadingFailedException(loadingExceptions);
        }
        modList.setLoadedMods(modContainers);
        statusConsumer.ifPresent(c->c.accept(String.format("Constructing %d mods", modList.size())));
        dispatchAndHandleError(ModLoadingStage.CONSTRUCT, syncExecutor, parallelExecutor, periodicTask);
        statusConsumer.ifPresent(c->c.accept("Creating registries"));
        dispatchAndHandleError(ModLoadingStage.CREATE_REGISTRIES, syncExecutor, parallelExecutor, periodicTask);
        ObjectHolderRegistry.findObjectHolders();
        CapabilityManager.INSTANCE.injectCapabilities(modList.getAllScanData());
        statusConsumer.ifPresent(c->c.accept("Adding custom tag types"));
        GameData.setCustomTagTypesFromRegistries();
        statusConsumer.ifPresent(c->c.accept("Populating registries"));
        dispatchAndHandleError(ModLoadingStage.LOAD_REGISTRIES, syncExecutor, parallelExecutor, periodicTask);
        statusConsumer.ifPresent(c->c.accept("Early mod loading complete"));
    }

    public void loadMods(final ModWorkManager.DrivenExecutor syncExecutor, final Executor parallelExecutor, final Function<Executor, CompletableFuture<Void>> beforeSidedEvent, final Function<Executor, CompletableFuture<Void>> afterSidedEvent, final Runnable periodicTask) {
        statusConsumer.ifPresent(c->c.accept("Loading mod config"));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, FMLPaths.CONFIGDIR.get()));
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.get());
        statusConsumer.ifPresent(c->c.accept("Mod setup: SETUP"));
        dispatchAndHandleError(ModLoadingStage.COMMON_SETUP, syncExecutor, parallelExecutor, periodicTask);
        statusConsumer.ifPresent(c->c.accept("Mod setup: SIDED SETUP"));
        dispatchAndHandleError(ModLoadingStage.SIDED_SETUP, syncExecutor, parallelExecutor, periodicTask, beforeSidedEvent, afterSidedEvent);
        statusConsumer.ifPresent(c->c.accept("Mod setup complete"));
    }

    public void finishMods(final ModWorkManager.DrivenExecutor syncExecutor, final Executor parallelExecutor, final Runnable periodicTask)
    {
        statusConsumer.ifPresent(c->c.accept("Mod setup: ENQUEUE IMC"));
        dispatchAndHandleError(ModLoadingStage.ENQUEUE_IMC, syncExecutor, parallelExecutor, periodicTask);
        statusConsumer.ifPresent(c->c.accept("Mod setup: PROCESS IMC"));
        dispatchAndHandleError(ModLoadingStage.PROCESS_IMC, syncExecutor, parallelExecutor, periodicTask);
        statusConsumer.ifPresent(c->c.accept("Mod setup: Final completion"));
        dispatchAndHandleError(ModLoadingStage.COMPLETE, syncExecutor, parallelExecutor, periodicTask);
        statusConsumer.ifPresent(c->c.accept("Freezing data"));
        GameData.freezeData();
        NetworkRegistry.lock();
        statusConsumer.ifPresent(c->c.accept(String.format("Mod loading complete - %d mods loaded", ModList.get().size())));
    }

    private void dispatchAndHandleError(ModLoadingStage state, ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor, final Runnable ticker) {
        if (!isLoadingStateValid()) {
            LOGGER.error("Cowardly refusing to process mod state change request from {}", state);
            return;
        }
        waitForTransition(state, syncExecutor, ticker, state.buildTransition(syncExecutor, parallelExecutor));
    }

    private void dispatchAndHandleError(ModLoadingStage state, ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor, final Runnable ticker, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask) {
        if (!isLoadingStateValid()) {
            LOGGER.error("Cowardly refusing to process mod state change request from {}", state);
            return;
        }
        waitForTransition(state, syncExecutor, ticker, state.buildTransition(syncExecutor, parallelExecutor, preSyncTask, postSyncTask));
    }

    private void waitForTransition(final ModLoadingStage state, final ModWorkManager.DrivenExecutor syncExecutor, final Runnable ticker, final CompletableFuture<List<Throwable>> transition) {
        while (!transition.isDone()) {
            syncExecutor.drive(ticker);
        }
        try {
            transition.join();
        } catch (CompletionException e) {
            loadingStateValid = false;
            Throwable t = e.getCause();
            final List<Throwable> notModLoading = Arrays.stream(t.getSuppressed())
                    .filter(obj -> !(obj instanceof ModLoadingException))
                    .collect(Collectors.toList());
            if (!notModLoading.isEmpty()) {
                LOGGER.fatal("Encountered non-modloading exceptions!", e);
                throw e;
            }

            final List<ModLoadingException> modLoadingExceptions = Arrays.stream(t.getSuppressed())
                    .filter(ModLoadingException.class::isInstance)
                    .map(ModLoadingException.class::cast)
                    .collect(Collectors.toList());
            LOGGER.fatal(LOADING,"Failed to complete lifecycle event {}, {} errors found", state, modLoadingExceptions.size());
            throw new LoadingFailedException(modLoadingExceptions);
        }
    }

    private List<ModContainer> buildMods(final ModFile modFile, final TransformingClassLoader modClassLoader)
    {
        final Map<String, IModInfo> modInfoMap = modFile.getModFileInfo().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, Function.identity()));

        LOGGER.trace(LOADING, "ModContainer is {}", ModContainer.class.getClassLoader());
        final List<ModContainer> containers = modFile.getScanResult().getTargets()
                .entrySet()
                .stream()
                .map(e -> buildModContainerFromTOML(modFile, modClassLoader, modInfoMap, e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (containers.size() != modInfoMap.size()) {
            LOGGER.fatal(LOADING,"File {} constructed {} mods: {}, but had {} mods specified: {}",
                    modFile.getFilePath(),
                    containers.size(), containers.stream().map(c -> c != null ? c.getModId() : "(null)").sorted().collect(Collectors.toList()),
                    modInfoMap.size(), modInfoMap.values().stream().map(IModInfo::getModId).sorted().collect(Collectors.toList()));
            loadingExceptions.add(new ModLoadingException(null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingclasses", null, modFile.getFilePath()));
        }
        // remove errored mod containers
        return containers.stream().filter(mc -> mc.modLoadingStage != ModLoadingStage.ERROR).collect(Collectors.toList());
    }

    private ModContainer buildModContainerFromTOML(final ModFile modFile, final TransformingClassLoader modClassLoader, final Map<String, IModInfo> modInfoMap, final Map.Entry<String, ? extends IModLanguageProvider.IModLanguageLoader> idToProviderEntry) {
        try {
            final String modId = idToProviderEntry.getKey();
            final IModLanguageProvider.IModLanguageLoader languageLoader = idToProviderEntry.getValue();
            IModInfo info = Optional.ofNullable(modInfoMap.get(modId)).
                    // throw a missing metadata error if there is no matching modid in the modInfoMap from the mods.toml file
                    orElseThrow(()->new ModLoadingException(null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingmetadata", null, modId));
            return languageLoader.loadMod(info, modClassLoader, modFile.getScanResult());
        } catch (ModLoadingException mle) {
            // exceptions are caught and added to the error list for later handling
            loadingExceptions.add(mle);
            // return an errored container instance here, because we tried and failed building a container.
            return new ErroredModContainer();
        }
    }

    /**
     * @return If the current mod loading state is valid. Use if you interact with vanilla systems directly during loading
     * and don't want to cause extraneous crashes due to trying to do things that aren't possible in a "broken load"
     */
    public static boolean isLoadingStateValid() {
        return get().loadingStateValid;
    }

    public <T extends Event & IModBusEvent> void runEventGenerator(Function<ModContainer, T> generator) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event generator to a broken mod state");
            return;
        }
        ModList.get().forEachModContainer((id, mc) -> mc.acceptEvent(generator.apply(mc)));
    }

    public <T extends Event & IModBusEvent> void postEvent(T e) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event {} to a broken mod state", e.getClass().getName());
            return;
        }
        ModList.get().forEachModContainer((id, mc) -> mc.acceptEvent(e));
    }

    public List<ModLoadingWarning> getWarnings()
    {
        return ImmutableList.copyOf(this.loadingWarnings);
    }

    public void addWarning(ModLoadingWarning warning)
    {
        this.loadingWarnings.add(warning);
    }

    private static boolean runningDataGen = false;

    public static boolean isDataGenRunning () {
        return runningDataGen;
    }

    private static class ErroredModContainer extends ModContainer {
        public ErroredModContainer() {
            super();
        }

        @Override
        public boolean matches(final Object mod) {
            return false;
        }

        @Override
        public Object getMod() {
            return null;
        }
    }
}
