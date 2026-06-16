/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.*;
import net.minecraftforge.fml.loading.moddiscovery.InvalidModIdentifier;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
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
public final class ModLoader {
    private ModLoader() {}

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ArrayList<ModLoadingException> LOADING_EXCEPTIONS = new ArrayList<>();
    private static final ArrayList<ModLoadingWarning> LOADING_WARNINGS = new ArrayList<>();
    private static final Consumer<String> STATUS_CONSUMER = StartupNotificationManager.modLoaderConsumer().orElse(_ -> {});
    private static final HashSet<IModLoadingState> COMPLETED_STATES = new HashSet<>();
    private static boolean loadingStateValid;

    private static String computeLanguageList() {
        return "\n" + LanguageLoadingProvider
                .applyForEach(lp -> lp.name() + '@' + lp.getClass().getPackage().getImplementationVersion())
                .collect(Collectors.joining("\n\t\t", "\t\t", ""));
    }

    private static String computeModLauncherServiceList() {
        final List<Map<String, String>> mods = FMLLoader.modLauncherModList();
        return "\n" + mods.stream().map(mod -> mod.getOrDefault("file","nofile") +
                ' ' + mod.getOrDefault("name", "missing") +
                ' ' + mod.getOrDefault("type","NOTYPE") +
                ' ' + mod.getOrDefault("description", "")).
                collect(Collectors.joining("\n\t\t","\t\t",""));
    }

    /**
     * Run on the primary starting thread by ClientModLoader and ServerModLoader
     * @param syncExecutor An executor to run tasks on the main thread
     * @param parallelExecutor An executor to run tasks on a parallel loading thread pool
     * @param periodicTask Optional periodic task to perform on the main thread while other activities run
     */
    public static void gatherAndInitializeMods(final ModWorkManager.DrivenExecutor syncExecutor, final Executor parallelExecutor, final Runnable periodicTask) {
        var loadingExceptions = LoadingModList.getErrors().stream()
                .flatMap(ModLoadingException::fromEarlyException)
                .toList();
        ModLoader.LOADING_EXCEPTIONS.addAll(loadingExceptions);
        LOADING_WARNINGS.addAll(LoadingModList.getBrokenFiles().stream()
                .map(file -> new ModLoadingWarning(null, ModLoadingStage.VALIDATE, InvalidModIdentifier.identifyJarProblem(file.getFilePath()).orElse("fml.modloading.brokenfile"), file.getFileName()))
                .toList());
        Set<IModInfo> erroredModInfos;
        if (loadingExceptions.isEmpty()) {
            erroredModInfos = Collections.emptySet();
        } else {
            erroredModInfos = Collections.newSetFromMap(new IdentityHashMap<>());
            erroredModInfos.addAll(loadingExceptions.stream().map(ModLoadingException::getModInfo).toList());
        }
        LoadingModList.getModFiles().stream()
                .filter(ModFileInfo::missingLicense)
                .filter(modFileInfo -> modFileInfo.getMods().stream().noneMatch(erroredModInfos::contains)) //Ignore files where any other mod already encountered an error
                .map(modFileInfo -> new ModLoadingException(null, ModLoadingStage.VALIDATE, "fml.modloading.missinglicense", null, modFileInfo.getFile()))
                .forEach(LOADING_EXCEPTIONS::add);
        CrashReportCallables.registerCrashCallable("ModLauncher", FMLLoader::getLauncherInfo);
        CrashReportCallables.registerCrashCallable("ModLauncher launch target", FMLLoader::launcherHandlerName);
        CrashReportCallables.registerCrashCallable("ModLauncher naming", FMLLoader::getNaming);
        CrashReportCallables.registerCrashCallable("ModLauncher services", ModLoader::computeModLauncherServiceList);
        CrashReportCallables.registerCrashCallable("FML Language Providers", ModLoader::computeLanguageList);

        ForgeFeature.registerFeature("javaVersion", ForgeFeature.VersionFeatureTest.forVersionString(IModInfo.DependencySide.BOTH, System.getProperty("java.version")));
        ForgeFeature.registerFeature("openGLVersion", ForgeFeature.VersionFeatureTest.forVersionString(IModInfo.DependencySide.CLIENT, ImmediateWindowHandler.getGLVersion()));
        loadingStateValid = true;
        FMLLoader.backgroundScanHandler.waitForScanToComplete(periodicTask);
        if (!LOADING_EXCEPTIONS.isEmpty()) {
            LOGGER.fatal(CORE, "Error during pre-loading phase", LOADING_EXCEPTIONS.getFirst());
            STATUS_CONSUMER.accept("ERROR DURING MOD LOADING");
            ModList.clearLoadedMods();
            loadingStateValid = false;
            throw new LoadingFailedException(LOADING_EXCEPTIONS);
        }
        var failedBounds = new ArrayList<ForgeFeature.Bound>();
        for (var mod : LoadingModList.getMods()) {
            for (var feature : mod.getForgeFeatures()) {
                if (!ForgeFeature.testFeature(FMLEnvironment.dist, feature))
                    failedBounds.add(feature);
            }
        }

        if (!failedBounds.isEmpty()) {
            LOGGER.fatal(CORE, "Failed to validate feature bounds for mods: {}", failedBounds);
            STATUS_CONSUMER.accept("ERROR DURING MOD LOADING");
            ModList.clearLoadedMods();
            loadingStateValid = false;
            throw new LoadingFailedException(failedBounds.stream()
                    .map(fb -> new ModLoadingException(fb.modInfo(), ModLoadingStage.CONSTRUCT, "fml.modloading.feature.missing", null, fb, ForgeFeature.featureValue(fb)))
                    .toList());
        }

        final var modContainers = new HashMap<String, ModContainer>();
        for (var file : LoadingModList.getModFiles()) {
            var containers = buildMods(file.getFile());
            for (var container : containers)
                modContainers.put(container.getModId(), container);
        }

        if (!LOADING_EXCEPTIONS.isEmpty()) {
            LOGGER.fatal(CORE, "Failed to initialize mod containers", LOADING_EXCEPTIONS.getFirst());
            STATUS_CONSUMER.accept("ERROR DURING MOD LOADING");
            ModList.clearLoadedMods();
            loadingStateValid = false;
            throw new LoadingFailedException(LOADING_EXCEPTIONS);
        }

        // Gather all dependencies, so we can make sure they have finished their transition events before we fire ours
        for (var mod : modContainers.values()) {
            for (var dep : mod.getModInfo().getDependencies()) {
                var target = modContainers.get(dep.getModId());
                if (target == null)
                    continue;

                switch (dep.getOrdering()) {
                    case AFTER:
                        mod.dependencies.add(target);
                        break;
                    case BEFORE:
                        target.dependencies.add(mod);
                        break;
                    default:
                        break;
                }
            }
        }

        ModList.setLoadedMods(modContainers.values().stream().toList());
        var stateList = ModStateManager.getStates(ModLoadingPhase.GATHER);
        var progress = StartupMessageManager.addProgressBar("Mod Gather", stateList.stream().mapToInt(mls -> mls.size().getAsInt()).sum());
        for (IModLoadingState mls : stateList) {
            dispatchAndHandleError(mls, syncExecutor, parallelExecutor, periodicTask, progress);
        }
        progress.complete();
    }

    public static void loadMods(final ModWorkManager.DrivenExecutor syncExecutor, final Executor parallelExecutor, final Runnable periodicTask) {
        var stateList = ModStateManager.getStates(ModLoadingPhase.LOAD);
        var progress = StartupMessageManager.addProgressBar("Mod Loading", stateList.stream().mapToInt(mls -> mls.size().getAsInt()).sum());
        for (IModLoadingState mls : stateList) {
            dispatchAndHandleError(mls, syncExecutor, parallelExecutor, periodicTask, progress);
        }
        progress.complete();
    }

    public static void finishMods(final ModWorkManager.DrivenExecutor syncExecutor, final Executor parallelExecutor, final Runnable periodicTask) {
        var stateList = ModStateManager.getStates(ModLoadingPhase.COMPLETE);
        var progress = StartupMessageManager.addProgressBar("Mod Complete", stateList.stream().mapToInt(mls -> mls.size().getAsInt()).sum());
        for (IModLoadingState mls : stateList) {
            dispatchAndHandleError(mls, syncExecutor, parallelExecutor, periodicTask, progress);
        }

        var modBusGroups = Set.of(
                ModList.getLoadedMods().stream()
                        .map(ModContainer::getModBusGroup)
                        .filter(Objects::nonNull)
                        .toArray(BusGroup[]::new)
        );
        for (var modBusGroup : modBusGroups) {
            parallelExecutor.execute(modBusGroup::trim);
        }
        parallelExecutor.execute(BusGroup.DEFAULT::trim);

        STATUS_CONSUMER.accept("Mod loading complete - %d mods loaded".formatted(ModList.size()));
        progress.complete();
    }

    private static void dispatchAndHandleError(IModLoadingState state, ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor, final Runnable ticker, final ProgressMeter progressBar) {
        if (!isLoadingStateValid()) {
            LOGGER.error("Cowardly refusing to process mod state change request from {}", state);
            return;
        }
        progressBar.label(progressBar.name() + " working");
        syncExecutor.drive(ticker);

        var inlineRunnable = state.inlineRunnable();
        if (inlineRunnable != null) handleInlineTransition(inlineRunnable, state, syncExecutor, ticker);

        var transition = state.buildTransition(syncExecutor, parallelExecutor, progressBar);
        if (transition != null) waitForTransition(state, syncExecutor, ticker, transition);

        COMPLETED_STATES.add(state);
    }

    private static void handleInlineTransition(final Runnable transition, final IModLoadingState state, final ModWorkManager.DrivenExecutor syncExecutor, final Runnable ticker) {
        var pb = StartupMessageManager.addProgressBar("State transition " + state.name() + " running", 0);
        syncExecutor.drive(ticker);
        transition.run();
        syncExecutor.drive(ticker);
        pb.complete();
        syncExecutor.drive(ticker);
    }

    private static void waitForTransition(final IModLoadingState state, final ModWorkManager.DrivenExecutor syncExecutor, final Runnable ticker, final CompletableFuture<Void> transition) {
        while (!transition.isDone()) {
            syncExecutor.drive(ticker);
        }

        try {
            transition.join();
        } catch (CompletionException e) {
            loadingStateValid = false;
            Throwable t = e.getCause();
            boolean hasNotModLoadingEx = Arrays.stream(t.getSuppressed())
                    .anyMatch(obj -> !(obj instanceof ModLoadingException));
            if (hasNotModLoadingEx) {
                LOGGER.fatal("Encountered non-modloading exceptions!", e);
                STATUS_CONSUMER.accept("ERROR DURING MOD LOADING");
                throw e;
            }

            final List<ModLoadingException> modLoadingExceptions = Arrays.stream(t.getSuppressed())
                    .filter(ModLoadingException.class::isInstance)
                    .map(ModLoadingException.class::cast)
                    .toList();
            LOGGER.fatal(LOADING,"Failed to complete lifecycle event {}, {} errors found", state.name(), modLoadingExceptions.size());
            STATUS_CONSUMER.accept("ERROR DURING MOD LOADING");
            throw new LoadingFailedException(modLoadingExceptions);
        }
    }

    private static List<ModContainer> buildMods(final IModFile modFile) {
        final Map<String, IModInfo> modInfoMap = new HashMap<>();
        for (var mod : modFile.getModFileInfo().getMods())
            modInfoMap.put(mod.getModId(), mod);

        LOGGER.trace(LOADING, "ModContainer is {}", ModContainer.class.getClassLoader());
        final List<ModContainer> containers = new ArrayList<>();
        for (var entry : modFile.getScanResult().getTargets().entrySet()) {
            var container = buildModContainerFromTOML(modFile, modInfoMap, entry.getKey(), entry.getValue());
            if (container != null)
                containers.add(container);
        }

        if (containers.size() != modInfoMap.size()) {
            var modIds = modInfoMap.values().stream().map(IModInfo::getModId).sorted().toList();
            var containerIds = containers.stream().map(c -> c != null ? c.getModId() : "(null)").sorted().toList();

            LOGGER.fatal(LOADING, "File {} constructed {} mods: {}, but had {} mods specified: {}",
                    modFile.getFilePath(),
                    containers.size(), containerIds,
                    modInfoMap.size(), modIds);

            var missingClasses = new ArrayList<>(modIds);
            missingClasses.removeAll(containerIds);
            LOGGER.fatal(LOADING, "The following classes are missing, but are reported in the mods.toml: {}", missingClasses);

            var missingMods = new ArrayList<>(containerIds);
            missingMods.removeAll(modIds);
            LOGGER.fatal(LOADING, "The following mods are missing, but have classes in the jar: {}", missingMods);

            LOADING_EXCEPTIONS.add(new ModLoadingException(null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingclasses", null, modFile.getFilePath()));
        }

        // remove errored mod containers
        return containers.stream().filter(mc -> mc.modLoadingStage != ModLoadingStage.ERROR).toList();
    }

    private static ModContainer buildModContainerFromTOML(final IModFile modFile, final Map<String, IModInfo> modInfoMap,
        final String modId, final IModLanguageProvider.IModLanguageLoader languageLoader
    ) {
        try {
            IModInfo info = modInfoMap.get(modId);
            // throw a missing metadata error if there is no matching modid in the modInfoMap from the mods.toml file
            if (info == null)
                throw new ModLoadingException(null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingmetadata", null, modId);

            return languageLoader.loadMod(info, modFile.getScanResult(), FMLLoader.getGameLayer());
        } catch (ModLoadingException mle) {
            // exceptions are caught and added to the error list for later handling
            LOADING_EXCEPTIONS.add(mle);
            // return an errored container instance here, because we tried and failed building a container.
            return new ErroredModContainer();
        }
    }

    /**
     * @return If the current mod loading state is valid. Use if you interact with vanilla systems directly during loading
     * and don't want to cause extraneous crashes due to trying to do things that aren't possible in a "broken load"
     */
    public static boolean isLoadingStateValid() {
        return loadingStateValid;
    }

    public static boolean hasCompletedState(IModLoadingState state) {
        return COMPLETED_STATES.contains(state);
    }

    public static <T extends IModBusEvent> void runEventGenerator(Function<ModContainer, T> generator) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event generator to a broken mod state");
            return;
        }
        for (var mod : ModList.getLoadedMods())
            mod.acceptEvent(generator.apply(mod));
    }

    public static <T extends IModBusEvent> void postEvent(T e) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event {} to a broken mod state", e.getClass().getName());
            return;
        }
        for (var mod : ModList.getLoadedMods())
            mod.acceptEvent(e);
    }

    public static <T extends IModBusEvent> T postEventWithReturn(T e) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event {} to a broken mod state", e.getClass().getName());
            return e;
        }
        for (var mod : ModList.getLoadedMods())
            mod.acceptEvent(e);
        return e;
    }

    @SuppressWarnings("removal")
    public static <T extends IModBusEvent> void postEventWrapContainerInModOrder(T event) {
        postEventWithWrapInModOrder(event,
            (mc, _) -> ModLoadingContext.get().setActiveContainer(mc),
            (_, _) -> ModLoadingContext.get().setActiveContainer(null)
        );
    }

    public static <T extends IModBusEvent> void postEventWithWrapInModOrder(T e, BiConsumer<ModContainer, T> pre, BiConsumer<ModContainer, T> post) {
        if (!loadingStateValid) {
            LOGGER.error("Cowardly refusing to send event {} to a broken mod state", e.getClass().getName());
            return;
        }
        for (var mod : ModList.getLoadedMods()) {
            pre.accept(mod, e);
            mod.acceptEvent(e);
            post.accept(mod, e);
        }
    }

    public static List<ModLoadingWarning> getWarnings() {
        return List.copyOf(LOADING_WARNINGS);
    }

    public static void addWarning(ModLoadingWarning warning) {
        LOADING_WARNINGS.add(warning);
    }

    private static boolean runningDataGen = false;

    public static boolean isDataGenRunning () {
        return runningDataGen;
    }

    private static final class ErroredModContainer extends ModContainer {
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
