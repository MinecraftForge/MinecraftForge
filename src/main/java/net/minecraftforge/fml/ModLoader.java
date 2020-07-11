/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import net.minecraft.util.registry.Bootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.InvalidModIdentifier;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
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

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
    private GatherDataEvent.DataGeneratorConfig dataGeneratorConfig;
    private ExistingFileHelper existingFileHelper;
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

    public void loadMods(Executor mainThreadExecutor, Consumer<Consumer<Supplier<Event>>> preSidedRunnable, Consumer<Consumer<Supplier<Event>>> postSidedRunnable) {
        DeferredWorkQueue.workExecutor = mainThreadExecutor;
        statusConsumer.ifPresent(c->c.accept("Loading mod config"));
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()-> ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, FMLPaths.CONFIGDIR.get()));
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.get());
        statusConsumer.ifPresent(c->c.accept("Mod setup: SETUP"));
        dispatchAndHandleError(LifecycleEventProvider.SETUP, mainThreadExecutor, null);
        statusConsumer.ifPresent(c->c.accept("Mod setup: SIDED SETUP"));
        mainThreadExecutor.execute(()->preSidedRunnable.accept(c->ModList.get().forEachModContainer((mi,mc)->mc.acceptEvent(c.get()))));
        dispatchAndHandleError(LifecycleEventProvider.SIDED_SETUP, mainThreadExecutor, null);
        mainThreadExecutor.execute(()->postSidedRunnable.accept(c->ModList.get().forEachModContainer((mi,mc)->mc.acceptEvent(c.get()))));
        statusConsumer.ifPresent(c->c.accept("Mod setup complete"));
    }

    private static class SpacedRunnable implements Executor {
        private static final long FIFTYMILLIS = TimeUnit.MILLISECONDS.toNanos(50);
        private long next = System.nanoTime() + FIFTYMILLIS;
        @Override
        public void execute(final Runnable command) {
            final long time = System.nanoTime();
            if (next < time) {
                command.run();
                next = time + FIFTYMILLIS;
            }
        }
    }
    public void gatherAndInitializeMods(final Runnable ticker) {
        statusConsumer.ifPresent(c->c.accept("Waiting for scan to complete"));
        FMLLoader.backgroundScanHandler.waitForScanToComplete(ticker);
        statusConsumer.ifPresent(c->c.accept("Loading mods"));
        final ModList modList = ModList.of(loadingModList.getModFiles().stream().map(ModFileInfo::getFile).collect(Collectors.toList()), loadingModList.getMods());
        if (!this.loadingExceptions.isEmpty()) {
            LOGGER.fatal(CORE, "Error during pre-loading phase", loadingExceptions.get(0));
            modList.setLoadedMods(Collections.emptyList());
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
            throw new LoadingFailedException(loadingExceptions);
        }
        modList.setLoadedMods(modContainers);
        SpacedRunnable sr = new SpacedRunnable();
        statusConsumer.ifPresent(c->c.accept(String.format("Constructing %d mods", modList.size())));
        dispatchAndHandleError(LifecycleEventProvider.CONSTRUCT, sr, ticker);
        statusConsumer.ifPresent(c->c.accept("Creating registries"));
        GameData.fireCreateRegistryEvents(LifecycleEventProvider.CREATE_REGISTRIES, event -> dispatchAndHandleError(event, sr, ticker));
        ObjectHolderRegistry.findObjectHolders();
        CapabilityManager.INSTANCE.injectCapabilities(modList.getAllScanData());
        statusConsumer.ifPresent(c->c.accept("Populating registries"));
        GameData.fireRegistryEvents(rl->true, LifecycleEventProvider.LOAD_REGISTRIES, event -> dispatchAndHandleError(event, sr, ticker));
        statusConsumer.ifPresent(c->c.accept("Early mod loading complete"));
    }

    private void dispatchAndHandleError(LifecycleEventProvider event, Executor executor, final Runnable ticker) {
        if (!loadingExceptions.isEmpty()) {
            LOGGER.error(LOADING,"Skipping lifecycle event {}, {} errors found.", event, loadingExceptions.size());
        } else {
            event.dispatch(this::accumulateErrors, executor, ticker);
        }
        if (!loadingExceptions.isEmpty()) {
            LOGGER.fatal(LOADING,"Failed to complete lifecycle event {}, {} errors found", event, loadingExceptions.size());
            throw new LoadingFailedException(loadingExceptions);
        }
    }
    private void accumulateErrors(List<ModLoadingException> errors) {
        loadingExceptions.addAll(errors);
    }

    private List<ModContainer> buildMods(final ModFile modFile, final TransformingClassLoader modClassLoader)
    {
        final Map<String, IModInfo> modInfoMap = modFile.getModFileInfo().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, Function.identity()));

        LOGGER.debug(LOADING, "ModContainer is {}", ModContainer.class.getClassLoader());
        final List<ModContainer> containers = modFile.getScanResult().getTargets().entrySet().stream().
                map(e -> buildModContainerFromTOML(modFile, modClassLoader, modInfoMap, e))
                .filter(e -> e != null)
                .collect(Collectors.toList());
        if (containers.size() != modInfoMap.size()) {
            LOGGER.fatal(LOADING,"File {} constructed {} mods: {}, but had {} mods specified: {}",
                    modFile.getFilePath(),
                    containers.size(), containers.stream().map(c -> c != null ? c.getModId() : "(null)").sorted().collect(Collectors.toList()),
                    modInfoMap.size(), modInfoMap.values().stream().map(IModInfo::getModId).sorted().collect(Collectors.toList()));
            loadingExceptions.add(new ModLoadingException(null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingclasses", null, modFile.getFilePath()));
        }
        return containers;
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
            // exceptions are caught and added to the error list for later handling. Null is returned here.
            loadingExceptions.add(mle);
            return null;
        }
    }

    public void postEvent(Event e) {
        ModList.get().forEachModContainer((id, mc) -> mc.acceptEvent(e));
    }

    public void finishMods(Executor mainThreadExecutor)
    {
        DeferredWorkQueue.workExecutor = mainThreadExecutor;
        statusConsumer.ifPresent(c->c.accept("Mod setup: ENQUEUE IMC"));
        dispatchAndHandleError(LifecycleEventProvider.ENQUEUE_IMC, mainThreadExecutor, null);
        statusConsumer.ifPresent(c->c.accept("Mod setup: PROCESS IMC"));
        dispatchAndHandleError(LifecycleEventProvider.PROCESS_IMC, mainThreadExecutor, null);
        statusConsumer.ifPresent(c->c.accept("Mod setup: Final completion"));
        dispatchAndHandleError(LifecycleEventProvider.COMPLETE, mainThreadExecutor, null);
        statusConsumer.ifPresent(c->c.accept("Freezing data"));
        GameData.freezeData();
        NetworkRegistry.lock();
        statusConsumer.ifPresent(c->c.accept(String.format("Mod loading complete - %d mods loaded", ModList.get().size())));
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

    public void runDataGenerator(final Set<String> mods, final Path path, final Collection<Path> inputs, Collection<Path> existingPacks, final boolean serverGenerators, final boolean clientGenerators, final boolean devToolGenerators, final boolean reportsGenerator, final boolean structureValidator, final boolean flat) {
        if (mods.contains("minecraft") && mods.size() == 1) return;
        LOGGER.info("Initializing Data Gatherer for mods {}", mods);
        runningDataGen = true;
        Bootstrap.register();
        dataGeneratorConfig = new GatherDataEvent.DataGeneratorConfig(mods, path, inputs, serverGenerators, clientGenerators, devToolGenerators, reportsGenerator, structureValidator, flat);
        existingFileHelper = new ExistingFileHelper(existingPacks, structureValidator);
        gatherAndInitializeMods(() -> {});
        dispatchAndHandleError(LifecycleEventProvider.GATHERDATA, Runnable::run, () -> {});
        dataGeneratorConfig.runAll();
    }

    public Function<ModContainer, ModLifecycleEvent> getDataGeneratorEvent() {
        return mc -> new GatherDataEvent(mc, dataGeneratorConfig.makeGenerator(p->dataGeneratorConfig.isFlat() ? p : p.resolve(mc.getModId()), dataGeneratorConfig.getMods().contains(mc.getModId())), dataGeneratorConfig, existingFileHelper);
    }
}
