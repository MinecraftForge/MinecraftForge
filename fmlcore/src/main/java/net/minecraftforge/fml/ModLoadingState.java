/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of the {@link IModLoadingState} interface.
 *
 * @param name           the name of this state
 * @param previous       the name of the state immediately previous to this state
 * @param message        a function returning a human-friendly message for this state
 * @param phase          the mod loading phase this state belongs to
 * @param inlineRunnable an optional runnable, which runs before starting the transition from this state to the next
 * @param transition     optional state transition information
 */
public record ModLoadingState(String name, String previous,
                              Function<ModList, String> message,
                              ModLoadingPhase phase,
                              Optional<Consumer<ModList>> inlineRunnable,
                              Optional<IModStateTransition> transition) implements IModLoadingState {
    @Override
    public <T extends Event & IModBusEvent>
    Optional<CompletableFuture<Void>> buildTransition(final Executor syncExecutor,
                                                      final Executor parallelExecutor,
                                                      final Function<Executor, CompletableFuture<Void>> preSyncTask,
                                                      final Function<Executor, CompletableFuture<Void>> postSyncTask) {
        return transition.map(t -> t.build(syncExecutor, parallelExecutor, preSyncTask, postSyncTask));
    }

    /**
     * {@return an empty mod loading state} The mod loading state has a blank human-readable message, no inline runnable,
     * and no state transition information.
     *
     * @param name     the name of the state
     * @param previous the name of the immediately previous state to this state
     * @param phase    the mod loading phase the state belongs to
     */
    public static ModLoadingState empty(final String name, final String previous, final ModLoadingPhase phase) {
        return new ModLoadingState(name, previous, ml -> "", phase, Optional.empty(), Optional.empty());
    }

    /**
     * Returns a mod loading state with state transition information and a default human-friendly message of
     * {@code Processing transition [name]}.
     *
     * @param name       the name of the state
     * @param previous   the name of the immediately previous state to this state
     * @param phase      the mod loading phase the state belongs to
     * @param transition the state transition information
     * @return a mod loading state with state transition information and a default message
     */
    public static ModLoadingState withTransition(final String name, final String previous, final ModLoadingPhase phase,
                                                 final IModStateTransition transition) {
        return new ModLoadingState(name, previous, ml -> "Processing transition " + name, phase, Optional.empty(), Optional.of(transition));
    }

    /**
     * Returns a mod loading state with state transition information and a custom human-friendly message function.
     *
     * @param name       the name of the state
     * @param previous   the name of the immediately previous state to this state
     * @param message    a function returning a human-friendly message for this state
     * @param phase      the mod loading phase the state belongs to
     * @param transition the state transition information
     * @return a mod loading state with state transition information and a custom message
     */
    public static ModLoadingState withTransition(final String name, final String previous,
                                                 final Function<ModList, String> message, final ModLoadingPhase phase,
                                                 final IModStateTransition transition) {
        return new ModLoadingState(name, previous, message, phase, Optional.empty(), Optional.of(transition));
    }

    /**
     * Returns a mod loading state with an inline runnable and a default human-friendly message of {@code Processing
     * work [name]}.
     *
     * @param name     the name of the state
     * @param previous the name of the immediately previous state to this state
     * @param phase    the mod loading phase the state belongs to
     * @param inline   an optional runnable, which runs before starting the transition from this state to the next
     * @return a mod loading state with an inline runnable and default message
     */
    public static ModLoadingState withInline(final String name, final String previous, final ModLoadingPhase phase,
                                             final Consumer<ModList> inline) {
        return new ModLoadingState(name, previous, ml -> "Processing work " + name, phase, Optional.of(inline), Optional.empty());
    }
}