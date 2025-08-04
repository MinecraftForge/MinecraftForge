/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.fml.loading.progress.ProgressMeter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

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
public record ModLoadingState(
    String name,
    String previous,
    Function<ModList, String> message,
    ToIntFunction<ModList> size,
    ModLoadingPhase phase,
    Optional<Consumer<ModList>> inlineRunnable,
    Optional<IModStateTransition> transition
) implements IModLoadingState {
    @Override
    public Optional<CompletableFuture<Void>> buildTransition(
        final Executor syncExecutor,
        final Executor parallelExecutor,
        final ProgressMeter progressBar,
        final Function<Executor, CompletableFuture<Void>> preSyncTask,
        final Function<Executor, CompletableFuture<Void>> postSyncTask
    ) {
        var transition = this.transition.orElse(null);
        return transition == null
                ? Optional.empty()
                : Optional.ofNullable(transition.build(name, syncExecutor, parallelExecutor, progressBar, preSyncTask, postSyncTask));
    }

    /**
     * {@return an empty mod loading state} The mod loading state has a blank human-readable message, no inline runnable,
     * and no state transition information.
     *
     * @param name     the name of the state
     * @param previous the name of the immediately previous state to this state
     * @param phase    the mod loading phase the state belongs to
     *
     * @deprecated Use the builder
     */
    @Deprecated(since = "1.21.3", forRemoval = true)
    public static ModLoadingState empty(final String name, final String previous, final ModLoadingPhase phase) {
        return of(name, phase).after(previous).empty();
    }

    /**
     * Returns a mod loading state with state transition information and a default human-friendly message of
     * {@code Processing transition [name]}.
     *
     * @param name       the name of the state
     * @param previous   the name of the immediately previous state to this state
     * @param phase      the mod loading phase the state belongs to
     * @param transition the state transition information
     *
     * @deprecated Use the builder
     */
    @Deprecated(since = "1.21.3", forRemoval = true)
    public static ModLoadingState withTransition(final String name, final String previous, final ModLoadingPhase phase,
                                                 final IModStateTransition transition) {
        return of(name, phase).after(previous).withTransition(transition);
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
     *
     * @deprecated Use the builder
     */
    @Deprecated(since = "1.21.3", forRemoval = true)
    public static ModLoadingState withTransition(final String name, final String previous,
                                                 final Function<ModList, String> message, final ModLoadingPhase phase,
                                                 final IModStateTransition transition) {
        return of(name, phase).after(previous).message(message).withTransition(transition);
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
     *
     * @deprecated Use the builder
     */
    @Deprecated(since = "1.21.3", forRemoval = true)
    public static ModLoadingState withInline(final String name, final String previous, final ModLoadingPhase phase,
                                             final Consumer<ModList> inline) {
        return of(name, phase).after(previous).withInline(inline);
    }

    public static Builder of(final String name, final ModLoadingPhase phase) {
        return new Builder(name, phase);
    }

    public static class Builder {
        private final String name;
        private final ModLoadingPhase phase;
        private String after;
        private Function<ModList, String> message = null;
        private ToIntFunction<ModList> size = null;

        private Builder(final String name, final ModLoadingPhase phase) {
            this.name = name;
            this.phase = phase;
        }

        public Builder after(final ModLoadingState value) { return after(value.name()); }
        public Builder after(final String value) {
            this.after = value;
            return this;
        }

        public Builder message(final String value) { return message(ml -> value); }
        public Builder message(final Function<ModList, String> value) {
            this.message = value;
            return this;
        }

        public Builder size(final int value) { return size(ml -> value); }
        public Builder size(final ToIntFunction<ModList> value) {
            this.size = value;
            return this;
        }

        public ModLoadingState empty() {
            return new ModLoadingState(name, after, message != null ? message : ml -> "", size != null ? size : ml -> 0, phase, Optional.empty(), Optional.empty());
        }

        public ModLoadingState withTransition(final IModStateTransition transition) {
            return new ModLoadingState(name, after, message != null ? message : ml -> "Processing transition " + name, size != null ? size : ModList::size, phase, Optional.empty(), Optional.of(transition));
        }

        public ModLoadingState withInline(final Consumer<ModList> inline) {
            return new ModLoadingState(name, after, message != null ? message : ml -> "Processing work " + name, size != null ? size : ml -> 0, phase, Optional.of(inline), Optional.empty());
        }
    }
}