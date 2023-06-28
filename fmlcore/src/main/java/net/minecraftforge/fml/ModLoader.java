/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.InvalidModIdentifier;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.fml.loading.progress.StartupNotificationManager;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.locating.ForgeFeature;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.Logging.LOADING;

/**
 * Loads mods.
 *
 * Dispatch cycle is seen in {@code #loadMods()} and {@code #finishMods()}
 *
 * Overall sequence for loadMods is:
 * <dl>
 *     <dt>CONSTRUCT</dt>
 *     <dd>Constructs the mod instance. Mods can typically setup basic environment such as Event listeners
 *     and Configuration specifications here.</dd>
 *     <dt>Automated dispatches</dt>
 *     <dd>Dispatches automated elements : {@code net.minecraftforge.fml.common.Mod.EventBusSubscriber},
 *     {@code net.minecraftforge.event.RegistryEvent}, {@code net.minecraftforge.common.capabilities.CapabilityInject}
 *     and others</dd>
 *     <dt>CONFIG_LOAD</dt>
 *     <dd>Dispatches ConfigLoadEvent to mods</dd>
 *     <dt>COMMON_SETUP</dt>
 *     <dd>Dispatches {@code net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent} to mods</dd>
 *     <dt>SIDED_SETUP</dt>
 *     <dd>Dispatches {@code net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent} or
 *     {@code net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent} to mods</dd>
 * </dl>
 *
 * Overall sequence for finishMods is:
 * <dl>
 *     <dt>ENQUEUE_IMC</dt>
 *     <dd>Dispatches {@code net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent} to mods,
 *     for enqueuing {@link InterModComms} messages for other mods to receive subsequently</dd>
 *     <dt>PROCESS_IMC</dt>
 *     <dd>Dispatches {@code net.minecraftforge.fml.event.lifecycle.InterModProcessEvent} to mods,
 *     for processing {@link InterModComms} messages received from other mods prior to this event</dd>
 *     <dt>COMPLETE</dt>
 *     <dd>Dispatches {@code net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent} to mods,
 *     and completes the mod loading sequence.</dd>
 * </dl>
 */
public class ModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static ModLoader INSTANCE;
    private final LoadingModList loadingModList;

    private final List<ModLoadingException> loadingExceptions;
    private final List<ModLoadingWarning> loadingWarnings;
    private final ModStateManager stateManager;
    private boolean loadingStateValid;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Consumer<String>> statusConsumer = StartupNotificationManager.modLoaderConsumer();
    private final Set<IModLoadingState> completedStates = new HashSet<>();
    private ModList modList;

    private ModLoader()
    {
        INSTANCE = this;
        this.loadingModList = FMLLoader.getLoadingModList();
        this.loadingExceptions = FMLLoader.getLoadingModList().getErrors().stream()
                .flatMap(ModLoadingException::fromEarlyException)
                .collect(Collectors.toList());
        this.loadingWarnings = FMLLoader.getLoadingModList().getBrokenFiles().stream()
                .map(file -> new ModLoadingWarning(null, ModLoadingStage.VALIDATE, InvalidModIdentifier.identifyJarProblem(file.getFilePath()).orElse("fml.modloading.brokenfile"), file.getFileName()))
                .collect(Collectors.toList());
        FMLLoader.getLoadingModList().getModFiles().stream()
                .filter(ModFileInfo::missingLicense)
                .filter(modFileInfo -> modFileInfo.getMods().stream().noneMatch(thisModInfo -> this.loadingExceptions.stream().map(ModLoadingException::getModInfo).anyMatch(otherInfo -> otherInfo == thisModInfo))) //Ignore files where any other mod already encountered an error
                .map(modFileInfo -> new ModLoadingException(null, ModLoadingStage.VALIDATE, "fml.modloading.missinglicense", null, modFileInfo.getFile()))
                .forEach(this.loadingExceptions::add);
        this.stateManager = new ModStateManager();
        CrashReportCallables.registerCrashCallable("ModLauncher", FMLLoader::getLauncherInfo);
        CrashReportCallables.registerCrashCallable("ModLauncher launch target", FMLLoader::launcherHandlerName);
        CrashReportCallables.registerCrashCallable("ModLauncher naming", FMLLoader::getNaming);
        CrashReportCallables.registerCrashCallable("ModLauncher services", this::computeModLauncherServiceList);
        CrashReportCallables.registerCrashCallable("FML Language Providers", this::computeLanguageList);
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
        ForgeFeature.registerFeature("javaVersion", ForgeFeature.VersionFeatureTest.forVersionString(IModInfo.DependencySide.BOTH, System.getProperty("java.version")));
        ForgeFeature.registerFeature("openGLVersion", ForgeFeature.VersionFeatureTest.forVersionString(IModInfo.DependencySide.CLIENT, ImmediateWindowHandler.getGLVersion()));
        loadingStateValid = true;
        FMLLoader.backgroundScanHandler.waitForScanToComplete(periodicTask);
        final ModList modList = ModList.of(loadingModList.getModFiles().stream().map(ModFileInfo::getFile).toList(),
                loadingModList.getMods());
        if (!this.loadingExceptions.isEmpty()) {
            LOGGER.fatal(CORE, "Error during pre-loading phase", loadingExceptions.get(0));
            statusConsumer.ifPresent(c->c.accept("ERROR DURING MOD LOADING"));
            modList.setLoadedMods(Collections.emptyList());
            loadingStateValid = false;
            throw new LoadingFailedException(loadingExceptions);
        }
        List<? extends ForgeFeature.Bound> failedBounds = loadingModList.getMods().stream()
                .map(ModInfo::getForgeFeatures)
                .flatMap(Collection::stream)
                .filter(bound -> !ForgeFeature.testFeature(FMLEnvironment.dist, bound))
                .toList();

        if (!failedBounds.isEmpty()) {
            LOGGER.fatal(CORE, "Failed to validate feature bounds for mods: {}", failedBounds);
            statusConsumer.ifPresent(c->c.accept("ERROR DURING MOD LOADING"));
            modList.setLoadedMods(Collections.emptyList());
            loadingStateValid = false;
            throw new LoadingFailedException(failedBounds.stream()
                    .map(fb -> new ModLoadingException(fb.modInfo(), ModLoadingStage.CONSTRUCT, "fml.modloading.feature.missing", null, fb, ForgeFeature.featureValue(fb)))
                    .toList());
        }

        final List<ModContainer> modContainers = loadingModList.getModFiles().stream()
                .map(ModFileInfo::getFile)
                .map(this::buildMods)
                .<ModContainer>mapMulti(Iterable::forEach)
                .toList();
        if (!loadingExceptions.isEmpty()) {
            LOGGER.fatal(CORE, "Failed to initialize mod containers", loadingExceptions.get(0));
            statusConsumer.ifPresent(c->c.accept("ERROR DURING MOD LOADING"));
            modList.setLoadedMods(Collections.emptyList());
            loadingStateValid = false;
            throw new LoadingFailedException(loadingExceptions);
        }
        modList.setLoadedMods(modContainers);
        this.modList = modList;
        var stateList = stateManager.getStates(ModLoadingPhase.GATHER);
        var progress = StartupMessageManager.addProgressBar("Mod Gather", stateList.stream().mapToInt(mls -> mls.size().applyAsInt(modList)).sum());
        stateList.forEach(mls->dispatchAndHandleError(mls, syncExecutor, parallelExecutor, periodicTask, progress));
        progress.complete();
    }

    public void loadMods(final ModWorkManager.DrivenExecutor syncExecutor, final Executor parallelExecutor, final Runnable periodicTask) {
        var stateList = stateManager.getStates(ModLoadingPhase.LOAD);
        var progress = StartupMessageManager.addProgressBar("Mod Loading", stateList.stream().mapToInt(mls -> mls.size().applyAsInt(modList)).sum());
        stateList.forEach(mls->dispatchAndHandleError(mls, syncExecutor, parallelExecutor, periodicTask, progress));
        progress.complete();
    }

    public void finishMods(final ModWorkManager.DrivenExecutor syncExecutor, final Executor parallelExecutor, final Runnable periodicTask) {
        var stateList = stateManager.getStates(ModLoadingPhase.COMPLETE);
        var progress = StartupMessageManager.addProgressBar("Mod Complete", stateList.stream().mapToInt(mls -> mls.size().applyAsInt(modList)).sum());
        stateList.forEach(mls->dispatchAndHandleError(mls, syncExecutor, parallelExecutor, periodicTask, progress));
        statusConsumer.ifPresent(c->c.accept(String.format("Mod loading complete - %d mods loaded", this.modList.size())));
        progress.complete();
    }

    private void dispatchAndHandleError(IModLoadingState state, ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor, final Runnable ticker, final ProgressMeter progressBar) {
        if (!isLoadingStateValid()) {
            LOGGER.error("Cowardly refusing to process mod state change request from {}", state);
            return;
        }
        progressBar.label(progressBar.name()+ " working");
        syncExecutor.drive(ticker);
        state.inlineRunnable().ifPresent(a->this.handleInlineTransition(a, state, syncExecutor, ticker));
        state.buildTransition(syncExecutor, parallelExecutor, progressBar).ifPresent(t->waitForTransition(state, syncExecutor, ticker, t));
        completedStates.add(state);
    }

    private void handleInlineTransition(final Consumer<ModList> transition, final IModLoadingState state, final ModWorkManager.DrivenExecutor syncExecutor, final Runnable ticker) {
        var pb = StartupMessageManager.addProgressBar("State transition " +state.name()+" running", 0);
        syncExecutor.drive(ticker);
        transition.accept(this.modList);
        syncExecutor.drive(ticker);
        pb.complete();
        syncExecutor.drive(ticker);
    }
    private void waitForTransition(final IModLoadingState state, final ModWorkManager.DrivenExecutor syncExecutor, final Runnable ticker, final CompletableFuture<Void> transition) {
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
                statusConsumer.ifPresent(c->c.accept("ERROR DURING MOD LOADING"));
                throw e;
            }

            final List<ModLoadingException> modLoadingExceptions = Arrays.stream(t.getSuppressed())
                    .filter(ModLoadingException.class::isInstance)
                    .map(ModLoadingException.class::cast)
                    .collect(Collectors.toList());
            LOGGER.fatal(LOADING,"Failed to complete lifecycle event {}, {} errors found", state.name(), modLoadingExceptions.size());
            statusConsumer.ifPresent(c->c.accept("ERROR DURING MOD LOADING"));
            throw new LoadingFailedException(modLoadingExceptions);
        }
    }

    private List<ModContainer> buildMods(final IModFile modFile)
    {
        final Map<String, IModInfo> modInfoMap = modFile.getModFileInfo().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, Function.identity()));

        LOGGER.trace(LOADING, "ModContainer is {}", ModContainer.class.getClassLoader());
        final List<ModContainer> containers = modFile.getScanResult().getTargets()
                .entrySet()
                .stream()
                .map(e -> buildModContainerFromTOML(modFile, modInfoMap, e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (containers.size() != modInfoMap.size()) {
            var modIds = modInfoMap.values().stream().map(IModInfo::getModId).sorted().collect(Collectors.toList());
            var containerIds = containers.stream().map(c -> c != null ? c.getModId() : "(null)").sorted().collect(Collectors.toList());

            LOGGER.fatal(LOADING,"File {} constructed {} mods: {}, but had {} mods specified: {}",
                    modFile.getFilePath(),
                    containers.size(), containerIds,
                    modInfoMap.size(), modIds);

            var missingClasses = new ArrayList<>(modIds);
            missingClasses.removeAll(containerIds);
            LOGGER.fatal(LOADING, "The following classes are missing, but are reported in the mods.toml: {}", missingClasses);

            var missingMods = new ArrayList<>(containerIds);
            missingMods.removeAll(modIds);
            LOGGER.fatal(LOADING, "The following mods are missing, but have classes in the jar: {}", missingMods);

            loadingExceptions.add(new ModLoadingException(null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingclasses", null, modFile.getFilePath()));
        }
        // remove errored mod containers
        return containers.stream().filter(mc -> mc.modLoadingStage != ModLoadingStage.ERROR).collect(Collectors.toList());
    }

    private ModContainer buildModContainerFromTOML(final IModFile modFile, final Map<String, IModInfo> modInfoMap, final Map.Entry<String, ? extends IModLanguageProvider.IModLanguageLoader> idToProviderEntry) {
        try {
            final String modId = idToProviderEntry.getKey();
            final IModLanguageProvider.IModLanguageLoader languageLoader = idToProviderEntry.getValue();
            IModInfo info = Optional.ofNullable(modInfoMap.get(modId)).
                    // throw a missing metadata error if there is no matching modid in the modInfoMap from the mods.toml file
                    orElseThrow(()->new ModLoadingException(null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingmetadata", null, modId));
            return languageLoader.loadMod(info, modFile.getScanResult(), FMLLoader.getGameLayer());
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

    public boolean hasCompletedState(final String stateName) {
        IModLoadingState state = stateManager.findState(stateName);
        return completedStates.contains(state);
    }

    public <T extends Event & IModBusEvent> void runEventGenerator(Function<ModContainer, T> generator) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event generator to a broken mod state");
            return;
        }
        ModList.get().forEachModInOrder(mc -> mc.acceptEvent(generator.apply(mc)));
    }

    public <T extends Event & IModBusEvent> void postEvent(T e) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event {} to a broken mod state", e.getClass().getName());
            return;
        }
        ModList.get().forEachModInOrder(mc -> mc.acceptEvent(e));
    }
    public <T extends Event & IModBusEvent> T postEventWithReturn(T e) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event {} to a broken mod state", e.getClass().getName());
            return e;
        }
        ModList.get().forEachModInOrder(mc -> mc.acceptEvent(e));
        return e;
    }
    public <T extends Event & IModBusEvent> void postEventWrapContainerInModOrder(T event) {
        postEventWithWrapInModOrder(event, (mc, e) -> ModLoadingContext.get().setActiveContainer(mc), (mc, e) -> ModLoadingContext.get().setActiveContainer(null));
    }
    public <T extends Event & IModBusEvent> void postEventWithWrapInModOrder(T e, BiConsumer<ModContainer, T> pre, BiConsumer<ModContainer, T> post) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event {} to a broken mod state", e.getClass().getName());
            return;
        }
        ModList.get().forEachModInOrder(mc -> {
            pre.accept(mc, e);
            mc.acceptEvent(e);
            post.accept(mc, e);
        });
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
