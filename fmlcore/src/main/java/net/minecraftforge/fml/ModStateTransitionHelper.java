/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import net.minecraftforge.fml.IModStateTransition.EventGenerator;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.progress.ProgressMeter;

@ApiStatus.Internal
final class ModStateTransitionHelper {
    static final IModStateTransition NOOP = new NoopTransition();
    record NoopTransition() implements IModStateTransition {
        @Override
        public ThreadSelector threadSelector() {
            return ThreadSelector.SYNC;
        }

        @Override
        public BiFunction<Executor, CompletableFuture<Void>, CompletableFuture<Void>> finalActivityGenerator() {
            return (e, t) -> t.thenApplyAsync(Function.identity(), e);
        }
    }

    static <V> CompletionStage<Void> completableFutureFromExceptionList(List<FutureResult<V>> t) {
        if (t.stream().noneMatch(e -> e.exception() != null)) {
            return CompletableFuture.completedFuture(null);
        } else {
            final var throwables = t.stream().map(FutureResult::exception).filter(Objects::nonNull).toList();
            CompletableFuture<Void> cf = new CompletableFuture<>();
            final RuntimeException accumulator = new RuntimeException();
            cf.completeExceptionally(accumulator);
            for (Throwable exception : throwables) {
                if (exception instanceof CompletionException) {
                    exception = exception.getCause();
                }
                if (exception.getSuppressed().length != 0) {
                    for (Throwable throwable : exception.getSuppressed()) {
                        accumulator.addSuppressed(throwable);
                    }
                } else {
                    accumulator.addSuppressed(exception);
                }
            }
            return cf;
        }
    }

    /// Same as [#completableFutureFromExceptionList(List)] but specialised for a single future
    static <V> CompletionStage<Void> completableFutureFromException(FutureResult<V> t) {
        if (t.exception() == null) {
            return CompletableFuture.completedFuture(null);
        } else {
            var exception = t.exception();
            CompletableFuture<Void> cf = new CompletableFuture<>();
            RuntimeException accumulator = new RuntimeException();
            cf.completeExceptionally(accumulator);
            if (exception instanceof CompletionException) {
                exception = exception.getCause();
            }
            if (exception.getSuppressed().length != 0) {
                for (Throwable throwable : exception.getSuppressed()) {
                    accumulator.addSuppressed(throwable);
                }
            } else {
                accumulator.addSuppressed(exception);
            }
            return cf;
        }
    }

    record FutureResult<V>(V value, Throwable exception) {}

    static <V> CompletableFuture<List<FutureResult<V>>> gather(Collection<? extends CompletableFuture<? extends V>> futures) {
        var list = new ArrayList<FutureResult<V>>(futures.size());
        var results = new CompletableFuture[futures.size()];

        for (var future : futures) {
            int i = list.size();
            list.add(null);
            @SuppressWarnings("unchecked") // Bypass a weird javac error with generic by forcing it to be the exact type we want.
            var raw = ((CompletableFuture<V>)future);
            results[i] = raw.whenComplete((result, exception) -> list.set(i, new FutureResult<>(result, exception)));
        }

        return CompletableFuture.allOf(results).handle((_, _)->null).thenApply(_ -> list);
    }

    private static <T extends IModBusEvent> CompletableFuture<Void> addCompletableFutureTaskForModDispatch(
        final Executor executor,
        final ProgressMeter progressBar,
        final EventGenerator<T> eventGenerator,
        final BiFunction<ModLoadingStage, Throwable, ModLoadingStage> nextState
     ) {
        var modFutures = new LinkedHashMap<String, CompletableFuture<Void>>();
        for (var mod : ModList.getLoadedMods()) {

            CompletableFuture<Void> parent = null;
            if (mod.dependencies.isEmpty()) {
                parent = CompletableFuture.allOf();
            } else {
                var deps = new CompletableFuture[mod.dependencies.size()];
                int idx = 0;
                for (var depContainer : mod.dependencies) {
                    var future = modFutures.get(depContainer.getModId());
                    if (future == null)
                        throw new IllegalStateException("Could not find dependency future " + depContainer.getModId() + " for " + mod.getModId());
                    deps[idx++] = future;
                }
                parent = CompletableFuture.allOf(deps);
            }

            @SuppressWarnings("removal")
            var dispatch = parent
                .thenRunAsync(() -> {
                    ModLoadingContext.get().setActiveContainer(mod);
                    var handler = mod.activityMap.get(mod.modLoadingStage);
                    if (handler != null)
                        handler.run();
                    mod.acceptEvent(eventGenerator.apply(mod));
                }, executor)
                .whenComplete((_, exception) -> {
                    mod.modLoadingStage = nextState.apply(mod.modLoadingStage, exception);
                    progressBar.increment();
                    ModLoadingContext.get().setActiveContainer(null);
                });

            modFutures.put(mod.getModId(), dispatch);
        }

        return gather(modFutures.values()).thenComposeAsync(ModStateTransitionHelper::completableFutureFromExceptionList, executor);
    }

    static <T extends IModBusEvent> CompletableFuture<Void> build(
        final IModStateTransition transition,
        final String name,
        final Executor syncExecutor,
        final Executor parallelExecutor,
        final ProgressMeter progressBar,
        final Function<Executor, CompletableFuture<Void>> preSyncTask,
        final Function<Executor, CompletableFuture<Void>> postSyncTask
    ) {
        CompletableFuture<Void> future;
        final var executor = transition.threadSelector().apply(syncExecutor, parallelExecutor);

        final CompletableFuture<Void> preSyncTaskCF = preSyncTask.apply(syncExecutor);
        CompletableFuture<Void> postEventDispatchCF = preSyncTaskCF
            .thenRunAsync(() -> progressBar.label(progressBar.name() + ": dispatching " + name), parallelExecutor);

        var eventGenerator = transition.eventFunction();
        if (eventGenerator != null) {
            BiFunction<ModLoadingStage, Throwable, ModLoadingStage> state = transition.nextModLoadingStage();
            future = addCompletableFutureTaskForModDispatch(executor, progressBar, eventGenerator, state);

            final CompletableFuture<Void> eventDispatchCF = future.handle(FutureResult::new)
                    .thenCompose(ModStateTransitionHelper::completableFutureFromException);
            postEventDispatchCF = postEventDispatchCF.thenComposeAsync(_ -> eventDispatchCF, parallelExecutor);
        }

        postEventDispatchCF = postEventDispatchCF.thenRun(() -> postSyncTask.apply(syncExecutor));

        return transition.finalActivityGenerator().apply(syncExecutor, postEventDispatchCF);
    }
}
