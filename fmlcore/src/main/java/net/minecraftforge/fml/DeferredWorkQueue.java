/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import static net.minecraftforge.fml.Logging.LOADING;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility for running code on the main launch thread at the next available
 * opportunity. There is no guaranteed order that work from various mods will be
 * run, but your own work will be run sequentially.
 * <p>
 * <strong>Use of this class after startup is not possible.</strong> At that
 * point, {@code ReentrantBlockableEventLoop} should be used instead.
 * <p>
 * Exceptions from tasks will be handled gracefully, causing a mod loading
 * error. Tasks that take egregiously long times to run will be logged.
 */
public class DeferredWorkQueue
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<ModLoadingStage, DeferredWorkQueue> workQueues = new HashMap<>();

    private final ConcurrentLinkedDeque<TaskInfo> tasks = new ConcurrentLinkedDeque<>();
    private final ModLoadingStage modLoadingStage;

    public DeferredWorkQueue(ModLoadingStage modLoadingStage) {
        this.modLoadingStage = modLoadingStage;
        workQueues.put(modLoadingStage, this);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<DeferredWorkQueue> lookup(Optional<ModLoadingStage> parallelClass) {
        return Optional.ofNullable(workQueues.get(parallelClass.orElse(null)));
    }

    public void runTasks() {
        if (tasks.isEmpty()) return;
        LOGGER.debug(LOADING, "Dispatching synchronous work for work queue {}: {} jobs", modLoadingStage, tasks.size());
        RuntimeException aggregate = new RuntimeException();
        Stopwatch timer = Stopwatch.createStarted();
        tasks.forEach(t -> makeRunnable(t, Runnable::run, aggregate));
        timer.stop();
        if (aggregate.getSuppressed().length > 0) {
            LOGGER.fatal(
                    LOADING,
                    "Synchronous work queue completed exceptionally in {}, see suppressed exceptions for details:",
                    timer,
                    aggregate
            );
            throw aggregate;
        } else {
            LOGGER.debug(LOADING, "Synchronous work queue completed in {}", timer);
        }
    }

    private static void makeRunnable(TaskInfo ti, Executor executor, RuntimeException aggregate) {
        executor.execute(() -> {
            Stopwatch timer = Stopwatch.createStarted();
            ModLoadingContext.get().setActiveContainer(ti.owner);
            try {
                ti.future.exceptionally(t -> captureException(ti.owner.getModId(), aggregate, t));
                ti.task.run();
            } finally {
                ModLoadingContext.get().setActiveContainer(null);
            }
            timer.stop();
            if (timer.elapsed(TimeUnit.SECONDS) >= 1) {
                LOGGER.warn(LOADING, "Mod '{}' took {} to run a deferred task.", ti.owner.getModId(), timer);
            }
        });
    }

    private static <T> T captureException(String modId, RuntimeException aggregate, Throwable throwable) {
        if (throwable instanceof CompletionException ce) {
            throwable = ce.getCause();
        }
        aggregate.addSuppressed(throwable);
        LOGGER.error("Mod '{}' encountered an error in a deferred task:", modId, throwable);
        return null;
    }

    public CompletableFuture<Void> enqueueWork(final ModContainer modInfo, final Runnable work) {
        return enqueueWork(modInfo, taskInfo -> CompletableFuture.runAsync(work, r -> taskInfo.task = r));
    }

    public <T> CompletableFuture<T> enqueueWork(final ModContainer modInfo, final Supplier<T> work) {
        return enqueueWork(modInfo, taskInfo -> CompletableFuture.supplyAsync(work, r -> taskInfo.task = r));
    }

    private <T> CompletableFuture<T> enqueueWork(final ModContainer modInfo, Function<TaskInfo, CompletableFuture<T>> futureGen) {
        TaskInfo taskInfo = new TaskInfo(modInfo);
        CompletableFuture<T> future = futureGen.apply(taskInfo);
        taskInfo.future = future;
        tasks.add(taskInfo);
        return future;
    }

    private static class TaskInfo {
        private final ModContainer owner;
        private Runnable task;
        private CompletableFuture<?> future;

        private TaskInfo(ModContainer owner) {
            this.owner = owner;
        }
    }
}