/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

import net.minecraftsingularity.fml.loading.progress.ProgressMeter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * A mod loading state. During mod loading, the mod loader transitions between states in a defined sorted list of states,
 * grouped into various {@link ModLoadingPhase phases}.
 *
 * @see IModStateProvider
 */
@NullMarked
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
    Supplier<String> message();

    /**
     * @return a function that computes the size of this transition based on the size of the modlist.
     * Used to compute progress.
     */
    default IntSupplier size() {
        return ModList::size;
    }

    /**
     * {@return an optional runnable, which runs before starting the transition from this state to the next}
     * @see #buildTransition(Executor, Executor, ProgressMeter, Function, Function)
     */
    @Nullable Runnable inlineRunnable();

    /**
     * Builds the transition task for this state with a blank pre-sync and post-sync task.
     *
     * @param syncExecutor     a synchronous executor
     * @param parallelExecutor a parallel executor
     * @param progressBar      a progress meter for tracking progress
     * @return a transition task for this state
     * @see #buildTransition(Executor, Executor, ProgressMeter, Function, Function)
     */
    default @Nullable CompletableFuture<Void> buildTransition(
        final Executor syncExecutor,
        final Executor parallelExecutor,
        final ProgressMeter progressBar
    ) {
        return buildTransition(syncExecutor, parallelExecutor, progressBar,
            _ -> CompletableFuture.completedFuture(null),
            _ -> CompletableFuture.completedFuture(null)
        );
    }

    /**
     * Builds the transition task for this state. The pre-sync and post-sync task functions allow the transition builder
     * to run these tasks on the same executor as the actual event dispatch and pre/post hooks.
     *
     * @param syncExecutor     a synchronous executor
     * @param parallelExecutor a parallel executor
     * @param progressBar      a progress meter for tracking progress
     * @param preSyncTask      a function which returns a task to run before event pre-dispatch hook
     * @param postSyncTask     a function which returns a task to run after event post-dispatch hook
     * @return a transition task for this state
     */
    @Nullable CompletableFuture<Void> buildTransition(final Executor syncExecutor,
                                                      final Executor parallelExecutor,
                                                      final ProgressMeter progressBar,
                                                      final Function<Executor, CompletableFuture<Void>> preSyncTask,
                                                      final Function<Executor, CompletableFuture<Void>> postSyncTask);
}
