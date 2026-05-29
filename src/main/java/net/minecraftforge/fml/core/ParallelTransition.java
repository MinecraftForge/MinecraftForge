/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.core;

import net.minecraftsingularity.fml.IModStateTransition;
import net.minecraftsingularity.fml.ModContainer;
import net.minecraftsingularity.fml.ModLoadingStage;
import net.minecraftsingularity.fml.ThreadSelector;
import net.minecraftsingularity.fml.event.IModBusEvent;
import net.minecraftsingularity.fml.event.lifecycle.ParallelDispatchEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

record ParallelTransition(ModLoadingStage stage, BiFunction<ModContainer, ModLoadingStage, ParallelDispatchEvent> event) implements IModStateTransition {
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IModBusEvent> EventGenerator<T> eventFunction() {
        return EventGenerator.fromFunction(mod -> (T)event.apply(mod, stage));
    }

    @Override
    public ThreadSelector threadSelector() {
        return ThreadSelector.PARALLEL;
    }

    @Override
    public BiFunction<Executor, CompletableFuture<Void>, CompletableFuture<Void>> finalActivityGenerator() {
        return (e, prev) -> prev.thenApplyAsync(t -> {
            stage.getDeferredWorkQueue().runTasks();
            return t;
        }, e);
    }
}
