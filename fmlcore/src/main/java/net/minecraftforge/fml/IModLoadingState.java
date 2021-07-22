package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IModLoadingState {
    String name();
    String previous();
    ModLoadingPhase phase();
    Function<ModList, String> message();
    Optional<Consumer<ModList>> inlineRunnable();

    default <T extends Event & IModBusEvent> Optional<CompletableFuture<List<Throwable>>> buildTransition(final Executor syncExecutor, final Executor parallelExecutor) {
        return buildTransition(syncExecutor, parallelExecutor, e->CompletableFuture.runAsync(()->{}, e), e->CompletableFuture.runAsync(()->{}, e));
    }

    <T extends Event & IModBusEvent> Optional<CompletableFuture<List<Throwable>>> buildTransition(Executor syncExecutor, Executor parallelExecutor, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask);
}
