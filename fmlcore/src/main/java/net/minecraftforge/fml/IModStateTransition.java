package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.ArrayList;
import java.util.Collections;
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

    default <T extends Event & IModBusEvent> CompletableFuture<List<Throwable>> build(Executor syncExecutor, Executor parallelExecutor, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask) {
        List<CompletableFuture<List<Throwable>>> cfs = new ArrayList<>();
        this.eventFunctionStream().get()
                .map(f->(EventGenerator<T>)f)
                .reduce((head, tail)-> addCompletableFutureTaskForModDispatch(syncExecutor, parallelExecutor, cfs, head, ModLoadingStage::currentState, tail))
                .ifPresent(last-> addCompletableFutureTaskForModDispatch(syncExecutor, parallelExecutor, cfs, last, nextModLoadingStage(), null));
        final CompletableFuture<Void> preSyncTaskCF = preSyncTask.apply(syncExecutor);
        final CompletableFuture<List<Throwable>> eventDispatchCF = ModList.gather(cfs).thenCompose(ModList::completableFutureFromExceptionList);
        final CompletableFuture<List<Throwable>> postEventDispatchCF = preSyncTaskCF.thenComposeAsync(n -> eventDispatchCF, parallelExecutor).thenApply(r -> {
            postSyncTask.apply(syncExecutor);
            return r;
        });
        return this.finalActivityGenerator().apply(syncExecutor, postEventDispatchCF);
    }

    default BiFunction<ModLoadingStage, Throwable, ModLoadingStage> nextModLoadingStage() {
        return ModLoadingStage::nextState;
    }

    private <T extends Event & IModBusEvent> EventGenerator<T> addCompletableFutureTaskForModDispatch(final Executor syncExecutor, final Executor parallelExecutor, final List<CompletableFuture<List<Throwable>>> completeableFutures, final EventGenerator<T> eventGenerator, BiFunction<ModLoadingStage, Throwable, ModLoadingStage> nextState, final EventGenerator<T> nextGenerator) {
        completeableFutures.add(((BiFunction<Executor, EventGenerator<T>, CompletableFuture<List<Throwable>>>)preDispatchHook()).apply(threadSelector().apply(syncExecutor, parallelExecutor), eventGenerator));
        completeableFutures.add(ModList.get().futureVisitor(eventGenerator, nextState).apply(threadSelector().apply(syncExecutor, parallelExecutor)));
        completeableFutures.add(((BiFunction<Executor, EventGenerator<T>, CompletableFuture<List<Throwable>>>)postDispatchHook()).apply(threadSelector().apply(syncExecutor, parallelExecutor), eventGenerator));
        return nextGenerator;
    }

    Supplier<Stream<EventGenerator<?>>> eventFunctionStream();
    ThreadSelector threadSelector();
    BiFunction<Executor, CompletableFuture<List<Throwable>>, CompletableFuture<List<Throwable>>> finalActivityGenerator();
    BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<List<Throwable>>> preDispatchHook();
    BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<List<Throwable>>> postDispatchHook();

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
    public BiFunction<Executor, CompletableFuture<List<Throwable>>, CompletableFuture<List<Throwable>>> finalActivityGenerator() {
        return (e, t) -> t.thenApplyAsync(Function.identity(), e);
    }

    @Override
    public BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<List<Throwable>>> preDispatchHook() {
        return (t, f)-> CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<List<Throwable>>> postDispatchHook() {
        return (t, f)-> CompletableFuture.completedFuture(Collections.emptyList());
    }
}