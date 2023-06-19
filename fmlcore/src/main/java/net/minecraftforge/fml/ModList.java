/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Master list of all mods - game-side version. This is classloaded in the game scope and
 * can dispatch game level events as a result.
 */
public class ModList
{
    private static Logger LOGGER = LogManager.getLogger();
    private static ModList INSTANCE;
    private final List<IModFileInfo> modFiles;
    private final List<IModInfo> sortedList;
    private final Map<String, ModFileInfo> fileById;
    private List<ModContainer> mods;
    private Map<String, ModContainer> indexedMods;
    private List<ModFileScanData> modFileScanData;
    private List<ModContainer> sortedContainers;

    private ModList(final List<ModFile> modFiles, final List<ModInfo> sortedList)
    {
        this.modFiles = modFiles.stream().map(ModFile::getModFileInfo).map(ModFileInfo.class::cast).collect(Collectors.toList());
        this.sortedList = sortedList.stream().
                map(ModInfo.class::cast).
                collect(Collectors.toList());
        this.fileById = this.modFiles.stream().map(IModFileInfo::getMods).flatMap(Collection::stream).
                map(ModInfo.class::cast).
                collect(Collectors.toMap(ModInfo::getModId, ModInfo::getOwningFile));
        CrashReportCallables.registerCrashCallable("Mod List", this::crashReport);
    }

    private String getModContainerState(String modId) {
        return getModContainerById(modId).map(ModContainer::getCurrentState).map(Object::toString).orElse("NONE");
    }

    private String fileToLine(IModFile mf) {
        return String.format(Locale.ENGLISH, "%-50.50s|%-30.30s|%-30.30s|%-20.20s|%-10.10s|Manifest: %s", mf.getFileName(),
                mf.getModInfos().get(0).getDisplayName(),
                mf.getModInfos().get(0).getModId(),
                mf.getModInfos().get(0).getVersion(),
                getModContainerState(mf.getModInfos().get(0).getModId()),
                ((ModFileInfo)mf.getModFileInfo()).getCodeSigningFingerprint().orElse("NOSIGNATURE"));
    }
    private String crashReport() {
        return "\n"+applyForEachModFile(this::fileToLine).collect(Collectors.joining("\n\t\t", "\t\t", ""));
    }

    public static ModList of(List<ModFile> modFiles, List<ModInfo> sortedList)
    {
        INSTANCE = new ModList(modFiles, sortedList);
        return INSTANCE;
    }

    public static ModList get() {
        return INSTANCE;
    }

    private static ForkJoinWorkerThread newForkJoinWorkerThread(ForkJoinPool pool) {
        ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        thread.setName("modloading-worker-" + thread.getPoolIndex());
        // The default sets it to the SystemClassloader, so copy the current one.
        thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        return thread;
    }

    public List<IModFileInfo> getModFiles()
    {
        return modFiles;
    }

    public IModFileInfo getModFileById(String modid)
    {
        return this.fileById.get(modid);
    }

    <T extends Event & IModBusEvent> Function<Executor, CompletableFuture<Void>> futureVisitor(
            final IModStateTransition.EventGenerator<T> eventGenerator,
            final ProgressMeter progressBar,
            final BiFunction<ModLoadingStage, Throwable, ModLoadingStage> stateChange) {
        return executor -> gather(
                this.mods.stream()
                .map(mod -> ModContainer.buildTransitionHandler(mod, eventGenerator, progressBar, stateChange, executor))
                .collect(Collectors.toList()))
            .thenComposeAsync(ModList::completableFutureFromExceptionList, executor);
    }
    static CompletionStage<Void> completableFutureFromExceptionList(List<? extends Map.Entry<?, Throwable>> t) {
        if (t.stream().noneMatch(e->e.getValue()!=null)) {
            return CompletableFuture.completedFuture(null);
        } else {
            final List<Throwable> throwables = t.stream().filter(e -> e.getValue() != null).map(Map.Entry::getValue).collect(Collectors.toList());
            CompletableFuture<Void> cf = new CompletableFuture<>();
            final RuntimeException accumulator = new RuntimeException();
            cf.completeExceptionally(accumulator);
            throwables.forEach(exception -> {
                if (exception instanceof CompletionException) {
                    exception = exception.getCause();
                }
                if (exception.getSuppressed().length!=0) {
                    Arrays.stream(exception.getSuppressed()).forEach(accumulator::addSuppressed);
                } else {
                    accumulator.addSuppressed(exception);
                }
            });
            return cf;
        }
    }

    static <V> CompletableFuture<List<Map.Entry<V, Throwable>>> gather(List<? extends CompletableFuture<? extends V>> futures) {
        List<Map.Entry<V, Throwable>> list = new ArrayList<>(futures.size());
        CompletableFuture<?>[] results = new CompletableFuture[futures.size()];
        futures.forEach(future -> {
            int i = list.size();
            list.add(null);
            results[i] = future.whenComplete((result, exception) -> list.set(i, new AbstractMap.SimpleImmutableEntry<>(result, exception)));
        });
        return CompletableFuture.allOf(results).handle((r, th)->null).thenApply(res -> list);
    }

    void setLoadedMods(final List<ModContainer> modContainers)
    {
        this.mods = modContainers;
        this.sortedContainers = modContainers.stream().sorted(Comparator.comparingInt(c->sortedList.indexOf(c.getModInfo()))).toList();
        this.indexedMods = modContainers.stream().collect(Collectors.toMap(ModContainer::getModId, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getModObjectById(String modId)
    {
        return getModContainerById(modId).map(ModContainer::getMod).map(o -> (T) o);
    }

    public Optional<? extends ModContainer> getModContainerById(String modId)
    {
        return Optional.ofNullable(this.indexedMods.get(modId));
    }

    public Optional<? extends ModContainer> getModContainerByObject(Object obj)
    {
        return mods.stream().filter(mc -> mc.getMod() == obj).findFirst();
    }

    public List<IModInfo> getMods()
    {
        return this.sortedList;
    }

    public boolean isLoaded(String modTarget)
    {
        return this.indexedMods.containsKey(modTarget);
    }

    public int size()
    {
        return mods.size();
    }

    public List<ModFileScanData> getAllScanData()
    {
        if (modFileScanData == null)
        {
            modFileScanData = this.sortedList.stream().
                    map(IModInfo::getOwningFile).
                    filter(Objects::nonNull).
                    map(IModFileInfo::getFile).
                    distinct().
                    map(IModFile::getScanResult).
                    collect(Collectors.toList());
        }
        return modFileScanData;

    }

    public void forEachModFile(Consumer<IModFile> fileConsumer)
    {
        modFiles.stream().map(IModFileInfo::getFile).forEach(fileConsumer);
    }

    public <T> Stream<T> applyForEachModFile(Function<IModFile, T> function) {
        return modFiles.stream().map(IModFileInfo::getFile).map(function);
    }

    public void forEachModContainer(BiConsumer<String, ModContainer> modContainerConsumer) {
        indexedMods.forEach(modContainerConsumer);
    }

    public void forEachModInOrder(Consumer<ModContainer> containerConsumer) {
        this.sortedContainers.forEach(containerConsumer);
    }

    public <T> Stream<T> applyForEachModContainer(Function<ModContainer, T> function) {
        return indexedMods.values().stream().map(function);
    }

    private static class UncaughtModLoadingException extends ModLoadingException {
        public UncaughtModLoadingException(ModLoadingStage stage, Throwable originalException) {
            super(null, stage, "fml.modloading.uncaughterror", originalException);
        }
    }
}
