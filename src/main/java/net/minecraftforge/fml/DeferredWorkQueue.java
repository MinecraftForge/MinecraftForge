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

import static net.minecraftforge.fml.Logging.LOADING;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;

import net.minecraftforge.forgespi.language.IModInfo;

/**
 * Utility for running code on the main launch thread at the next available
 * opportunity. There is no guaranteed order that work from various mods will be
 * run, but your own work will be run sequentially.
 * <p>
 * <strong>Use of this class after startup is not possible.</strong> At that
 * point, {@link IThreadListener} should be used instead.
 * <p>
 * Exceptions from tasks will be handled gracefully, causing a mod loading
 * error. Tasks that take egregiously long times to run will be logged.
 *
 * This is being deprecated in favour of a new interface on loading events, to remove confusion about how it operates. #TODO
 */
@Deprecated
public class DeferredWorkQueue
{
    private static class TaskInfo
    {
        public final IModInfo owner;
        public final Runnable task;

        TaskInfo(IModInfo owner, Runnable task) {
            this.owner = owner;
            this.task = task;
        }
    }

    /**
     * {@link Runnable} except it allows throwing checked exceptions.
     *
     * Is to {@link Runnable} as {@link Callable} is to {@link Supplier}.
     */
    @FunctionalInterface
    public interface CheckedRunnable
    {
        void run() throws Exception;
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private static ThreadLocal<ModContainer> currentOwner = new ThreadLocal<>();
    private static List<ModLoadingException> raisedExceptions = new ArrayList<>();

    private static final ConcurrentLinkedDeque<TaskInfo> taskQueue = new ConcurrentLinkedDeque<>();
    private static final Executor deferredExecutor = r -> taskQueue.add(new TaskInfo(currentOwner.get().getModInfo(), r));

    private static <T> Function<Throwable, T> handleException() {
        final ModContainer owner = currentOwner.get();
        return t -> {
            LogManager.getLogger(DeferredWorkQueue.class).error("Encountered exception executing deferred work", t);
            raisedExceptions.add(new ModLoadingException(owner.getModInfo(), owner.getCurrentState(), "fml.modloading.failedtoprocesswork", t));
            return null;
        };
    }

    /**
     * Run a task on the loading thread at the next available opportunity, i.e.
     * after the current lifecycle event has completed.
     * <p>
     * If the task must throw a checked exception, use
     * {@link #runLaterChecked(CheckedRunnable)}.
     * <p>
     * If the task has a result, use {@link #getLater(Supplier)} or
     * {@link #getLaterChecked(Callable)}.
     *
     * @param workToEnqueue A {@link Runnable} to execute later, on the loading
     *                      thread
     * @return A {@link CompletableFuture} that completes at said time
     */
    public static CompletableFuture<Void> runLater(Runnable workToEnqueue) {
        currentOwner.set(ModLoadingContext.get().getActiveContainer());
        return CompletableFuture.runAsync(workToEnqueue, deferredExecutor).exceptionally(DeferredWorkQueue.handleException());
    }

    /**
     * Run a task on the loading thread at the next available opportunity, i.e.
     * after the current lifecycle event has completed. This variant allows the task
     * to throw a checked exception.
     * <p>
     * If the task does not throw a checked exception, use
     * {@link #runLater(Runnable)}.
     * <p>
     * If the task has a result, use {@link #getLater(Supplier)} or
     * {@link #getLaterChecked(Callable)}.
     *
     * @param workToEnqueue A {@link CheckedRunnable} to execute later, on the
     *                      loading thread
     * @return A {@link CompletableFuture} that completes at said time
     */
    public static CompletableFuture<Void> runLaterChecked(CheckedRunnable workToEnqueue) {
        return runLater(() -> {
            try {
                workToEnqueue.run();
            } catch (Throwable t) {
                throw new CompletionException(t);
            }
        });
    }

    /**
     * Run a task computing a result on the loading thread at the next available
     * opportunity, i.e. after the current lifecycle event has completed.
     * <p>
     * If the task throws a checked exception, use
     * {@link #getLaterChecked(Callable)}.
     * <p>
     * If the task does not have a result, use {@link #runLater(Runnable)} or
     * {@link #runLaterChecked(CheckedRunnable)}.
     *
     * @param               <T> The result type of the task
     * @param workToEnqueue A {@link Supplier} to execute later, on the loading
     *                      thread
     * @return A {@link CompletableFuture} that completes at said time
     */
    public static <T> CompletableFuture<T> getLater(Supplier<T> workToEnqueue) {
        currentOwner.set(ModLoadingContext.get().getActiveContainer());
        return CompletableFuture.supplyAsync(workToEnqueue, deferredExecutor).exceptionally(DeferredWorkQueue.handleException());
    }

    /**
     * Run a task computing a result on the loading thread at the next available
     * opportunity, i.e. after the current lifecycle event has completed. This
     * variant allows the task to throw a checked exception.
     * <p>
     * If the task does not throw a checked exception, use
     * {@link #getLater(Callable)}.
     * <p>
     * If the task does not have a result, use {@link #runLater(Runnable)} or
     * {@link #runLaterChecked(CheckedRunnable)}.
     *
     * @param               <T> The result type of the task
     * @param workToEnqueue A {@link Supplier} to execute later, on the loading
     *                      thread
     * @return A {@link CompletableFuture} that completes at said time
     */
    public static <T> CompletableFuture<T> getLaterChecked(Callable<T> workToEnqueue) {
        return getLater(() -> {
            try {
                return workToEnqueue.call();
            } catch (Throwable t) {
                throw new CompletionException(t);
            }
        });
    }

    static void clear() {
        taskQueue.clear();
    }

    static Executor workExecutor = Runnable::run;

    static void runTasks(ModLoadingStage fromStage, Consumer<List<ModLoadingException>> errorHandler) {
        raisedExceptions.clear();
        if (taskQueue.isEmpty()) return; // Don't log unnecessarily
        LOGGER.info(LOADING, "Dispatching synchronous work after {}: {} jobs", fromStage, taskQueue.size());
        StopWatch globalTimer = StopWatch.createStarted();
        final CompletableFuture<Void> tasks = CompletableFuture.allOf(taskQueue.stream().map(ti -> makeRunnable(ti, workExecutor)).toArray(CompletableFuture[]::new));
        tasks.join();
        LOGGER.info(LOADING, "Synchronous work queue completed in {}", globalTimer);
        errorHandler.accept(raisedExceptions);
    }

    private static CompletableFuture<?> makeRunnable(TaskInfo ti, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            Stopwatch timer = Stopwatch.createStarted();
            ti.task.run();
            timer.stop();
            if (timer.elapsed(TimeUnit.SECONDS) >= 1) {
                LOGGER.warn(LOADING, "Mod '{}' took {} to run a deferred task.", ti.owner.getModId(), timer);
            }
        }, executor);
    }
}
