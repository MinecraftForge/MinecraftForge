/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.progress.ProgressMeter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public interface IModStateTransition {
    static IModStateTransition buildNoopTransition() {
        return new NoopTransition();
    }

    default <T extends Event & IModBusEvent>
    CompletableFuture<Void> build(final String name,
                                  final Executor syncExecutor,
                                  final Executor parallelExecutor,
                                  final ProgressMeter progressBar,
                                  final Function<Executor, CompletableFuture<Void>> preSyncTask,
                                  final Function<Executor, CompletableFuture<Void>> postSyncTask) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        this.eventFunctionStream().get()
                .map(f -> (EventGenerator<T>) f)
                .reduce((head, tail) -> addCompletableFutureTaskForModDispatch(syncExecutor, parallelExecutor, futures, progressBar, head, ModLoadingStage::currentState, tail))
                .ifPresent(last -> addCompletableFutureTaskForModDispatch(syncExecutor, parallelExecutor, futures, progressBar, last, nextModLoadingStage(), null));

        final CompletableFuture<Void> preSyncTaskCF = preSyncTask.apply(syncExecutor);
        final CompletableFuture<Void> eventDispatchCF = ModList.gather(futures).thenCompose(ModList::completableFutureFromExceptionList);
        final CompletableFuture<Void> postEventDispatchCF = preSyncTaskCF
                .thenApplyAsync(n -> {
                    progressBar.label(progressBar.name() + ": dispatching "+name);
                    return null;
                }, parallelExecutor)
                .thenComposeAsync(n -> eventDispatchCF, parallelExecutor)
                .thenApply(r -> {
                    postSyncTask.apply(syncExecutor);
                    return null;
                });
        return this.finalActivityGenerator().apply(syncExecutor, postEventDispatchCF);
    }

    default BiFunction<ModLoadingStage, Throwable, ModLoadingStage> nextModLoadingStage() {
        return ModLoadingStage::nextState;
    }

    private <T extends Event & IModBusEvent>
    EventGenerator<T> addCompletableFutureTaskForModDispatch(final Executor syncExecutor,
                                                             final Executor parallelExecutor,
                                                             final List<CompletableFuture<Void>> completableFutures,
                                                             final ProgressMeter progressBar,
                                                             final EventGenerator<T> eventGenerator,
                                                             final BiFunction<ModLoadingStage, Throwable, ModLoadingStage> nextState,
                                                             final EventGenerator<T> nextGenerator) {
        final Executor selectedExecutor = threadSelector().apply(syncExecutor, parallelExecutor);

        var preDispatchHook = (BiFunction<Executor, EventGenerator<T>, CompletableFuture<Void>>) preDispatchHook();
        completableFutures.add(preDispatchHook.apply(selectedExecutor, eventGenerator));

        completableFutures.add(ModList.get().futureVisitor(eventGenerator, progressBar, nextState).apply(threadSelector().apply(syncExecutor, parallelExecutor)));

        var postDispatchHook = (BiFunction<Executor, EventGenerator<T>, CompletableFuture<Void>>) postDispatchHook();
        completableFutures.add(postDispatchHook.apply(selectedExecutor, eventGenerator));

        return nextGenerator;
    }

    Supplier<Stream<EventGenerator<?>>> eventFunctionStream();
    ThreadSelector threadSelector();
    BiFunction<Executor, CompletableFuture<Void>, CompletableFuture<Void>> finalActivityGenerator();
    BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<Void>> preDispatchHook();
    BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<Void>> postDispatchHook();

    interface EventGenerator<T extends Event & IModBusEvent> extends Function<ModContainer, T> {
        static <FN extends Event & IModBusEvent> EventGenerator<FN> fromFunction(Function<ModContainer, FN> fn) {
            return fn::apply;
        }
    }
}

record NoopTransition() implements IModStateTransition {

    @Override
    public Supplier<Stream<EventGenerator<?>>> eventFunctionStream() {
        return Stream::of;
    }

    @Override
    public ThreadSelector threadSelector() {
        return ThreadSelector.SYNC;
    }

    @Override
    public BiFunction<Executor, CompletableFuture<Void>, CompletableFuture<Void>> finalActivityGenerator() {
        return (e, t) -> t.thenApplyAsync(Function.identity(), e);
    }

    @Override
    public BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<Void>> preDispatchHook() {
        return (t, f)-> CompletableFuture.completedFuture(null);
    }

    @Override
    public BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<Void>> postDispatchHook() {
        return (t, f)-> CompletableFuture.completedFuture(null);
    }
}