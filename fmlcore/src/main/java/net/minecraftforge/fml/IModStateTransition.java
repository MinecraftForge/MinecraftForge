/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

import net.minecraftsingularity.fml.event.IModBusEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraftsingularity.fml.loading.progress.ProgressMeter;
import org.jetbrains.annotations.Nullable;

public interface IModStateTransition {
    static IModStateTransition buildNoopTransition() {
        return ModStateTransitionHelper.NOOP;
    }

    default CompletableFuture<Void> build(
        final String name,
        final Executor syncExecutor,
        final Executor parallelExecutor,
        final ProgressMeter progressBar,
        final Function<Executor, CompletableFuture<Void>> preSyncTask,
        final Function<Executor, CompletableFuture<Void>> postSyncTask
    ) {
        return ModStateTransitionHelper.build(this, name, syncExecutor, parallelExecutor, progressBar, preSyncTask, postSyncTask);
    }

    default BiFunction<ModLoadingStage, Throwable, ModLoadingStage> nextModLoadingStage() {
        return ModLoadingStage::nextState;
    }

    @Nullable
    default <T extends IModBusEvent> EventGenerator<T> eventFunction() {
        return null;
    }

    ThreadSelector threadSelector();
    BiFunction<Executor, CompletableFuture<Void>, CompletableFuture<Void>> finalActivityGenerator();

    interface EventGenerator<T extends IModBusEvent> extends Function<ModContainer, T> {
        static <FN extends IModBusEvent> EventGenerator<FN> fromFunction(Function<ModContainer, FN> fn) {
            return fn::apply;
        }
    }
}
