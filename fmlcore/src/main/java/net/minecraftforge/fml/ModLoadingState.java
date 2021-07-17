package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public record ModLoadingState(String name, String previous, Function<ModList, String> message, ModLoadingPhase phase, Optional<Consumer<ModList>> inlineRunnable, Optional<IModStateTransition> transition) implements IModLoadingState {
    @Override
    public <T extends Event & IModBusEvent> Optional<CompletableFuture<List<Throwable>>> buildTransition(final Executor syncExecutor, final Executor parallelExecutor, Function<Executor, CompletableFuture<Void>> preSyncTask, Function<Executor, CompletableFuture<Void>> postSyncTask) {
        return transition.map(t->t.build(syncExecutor, parallelExecutor, preSyncTask, postSyncTask));
    }

    public static ModLoadingState empty(final String name, final String previous, final ModLoadingPhase phase) {
        return new ModLoadingState(name, previous, ml->"", phase, Optional.empty(), Optional.empty());
    }

    public static ModLoadingState withTransition(final String name, final String previous, final ModLoadingPhase phase, IModStateTransition transition) {
        return new ModLoadingState(name, previous, ml->"Processing transition "+name, phase, Optional.empty(), Optional.of(transition));
    }

    public static ModLoadingState withTransition(final String name, final String previous, final Function<ModList, String> message, final ModLoadingPhase phase, IModStateTransition transition) {
        return new ModLoadingState(name, previous, message, phase, Optional.empty(), Optional.of(transition));
    }
    public static ModLoadingState withInline(final String name, final String previous, final ModLoadingPhase phase, Consumer<ModList> inline) {
        return new ModLoadingState(name, previous, ml->"Processing work "+name, phase, Optional.of(inline), Optional.empty());
    }
}