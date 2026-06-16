/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.fml.loading.progress.ProgressMeter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

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
@NullMarked
public record ModLoadingState(
    String name,
    String previous,
    Supplier<String> message,
    IntSupplier size,
    ModLoadingPhase phase,
    @Nullable Runnable inlineRunnable,
    @Nullable IModStateTransition transition
) implements IModLoadingState {
    @Override
    public @Nullable CompletableFuture<Void> buildTransition(
        final Executor syncExecutor,
        final Executor parallelExecutor,
        final ProgressMeter progressBar,
        final Function<Executor, CompletableFuture<Void>> preSyncTask,
        final Function<Executor, CompletableFuture<Void>> postSyncTask
    ) {
        if (transition == null) return null;
        return transition.build(name, syncExecutor, parallelExecutor, progressBar, preSyncTask, postSyncTask);
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
        return new ModLoadingState(name, previous, () -> "", () -> 0, phase, null, null);
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
                                                 final Supplier<String> message, final ModLoadingPhase phase,
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
                                             final Runnable inline) {
        return of(name, phase).after(previous).withInline(inline);
    }

    public static Builder of(final String name, final ModLoadingPhase phase) {
        return new Builder(name, phase);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ModLoadingState that
                && this.phase == that.phase
                && this.name.equals(that.name)
                && this.previous.equals(that.previous);
    }

    @Override
    public int hashCode() {
        int result = phase.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + previous.hashCode();
        return result;
    }

    public static final class Builder {
        private final String name;
        private final ModLoadingPhase phase;
        private String after = "";
        private @Nullable Supplier<String> message = null;
        private @Nullable IntSupplier size = null;

        private Builder(final String name, final ModLoadingPhase phase) {
            this.name = name;
            this.phase = phase;
        }

        public Builder after(final ModLoadingState value) { return after(value.name()); }
        public Builder after(final String value) {
            this.after = value;
            return this;
        }

        public Builder message(final String value) { return message(() -> value); }
        public Builder message(final Supplier<String> value) {
            this.message = value;
            return this;
        }

        public Builder size(final int value) { return size(() -> value); }
        public Builder size(final IntSupplier value) {
            this.size = value;
            return this;
        }

        public ModLoadingState empty() {
            return new ModLoadingState(name, after, message != null ? message : () -> "", size != null ? size : () -> 0, phase, null, null);
        }

        public ModLoadingState withTransition(final IModStateTransition transition) {
            return new ModLoadingState(name, after, message != null ? message : () -> "Processing transition " + name, size != null ? size : ModList::size, phase, null, transition);
        }

        public ModLoadingState withInline(final Runnable inline) {
            return new ModLoadingState(name, after, message != null ? message : () -> "Processing work " + name, size != null ? size : () -> 0, phase, inline, null);
        }
    }
}
