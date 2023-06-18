/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.progress.ProgressMeter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * A mod loading state. During mod loading, the mod loader transitions between states in a defined sorted list of states,
 * grouped into various {@link ModLoadingPhase phases}.
 *
 * @see IModStateProvider
 */
public interface IModLoadingState {
    /**
     * {@return the name of this state}
     */
    String name();

    /**
     * {@return the name of the state immediately previous to this state} This may be a blank name, which indicates this
     * is either the first mod loading state or an exceptional mod loading state (such as a situation where errors
     * prevent the loading process from continuing normally).
     */
    String previous();

    /**
     * {@return the mod loading phase this state belongs to} For exceptional mod loading states, this should be
     * {@link ModLoadingPhase#ERROR}.
     */
    ModLoadingPhase phase();

    /**
     * {@return a function returning a human-friendly message for this state}
     */
    Function<ModList, String> message();

    /**
     * @return a function that computes the size of this transition based on the size of the modlist.
     * Used to compute progress.
     */
    ToIntFunction<ModList> size();

    /**
     * {@return an optional runnable, which runs before starting the transition from this state to the next}
     * @see #buildTransition(Executor, Executor, ProgressMeter, Function, Function)
     */
    Optional<Consumer<ModList>> inlineRunnable();

    /**
     * Builds the transition task for this state with a blank pre-sync and post-sync task.
     *
     * @param <T>              a type of event fired on the mod-specific event bus
     * @param syncExecutor     a synchronous executor
     * @param parallelExecutor a parallel executor
     * @param progressBar      a progress meter for tracking progress
     * @return a transition task for this state
     * @see #buildTransition(Executor, Executor, ProgressMeter, Function, Function)
     */
    default <T extends Event & IModBusEvent>
    Optional<CompletableFuture<Void>> buildTransition(final Executor syncExecutor,
                                                      final Executor parallelExecutor,
                                                      final ProgressMeter progressBar) {
        return buildTransition(syncExecutor, parallelExecutor, progressBar,
                e -> CompletableFuture.runAsync(() -> {}, e),
                e -> CompletableFuture.runAsync(() -> {}, e));
    }

    /**
     * Builds the transition task for this state. The pre-sync and post-sync task functions allow the transition builder
     * to run these tasks on the same executor as the actual event dispatch and pre/post hooks.
     *
     * @param <T>              a type of event fired on the mod-specific event bus
     * @param syncExecutor     a synchronous executor
     * @param parallelExecutor a parallel executor
     * @param progressBar      a progress meter for tracking progress
     * @param preSyncTask      a function which returns a task to run before event pre-dispatch hook
     * @param postSyncTask     a function which returns a task to run after event post-dispatch hook
     * @return a transition task for this state
     */
    <T extends Event & IModBusEvent>
    Optional<CompletableFuture<Void>> buildTransition(final Executor syncExecutor,
                                                      final Executor parallelExecutor,
                                                      final ProgressMeter progressBar,
                                                      final Function<Executor, CompletableFuture<Void>> preSyncTask,
                                                      final Function<Executor, CompletableFuture<Void>> postSyncTask);
}
