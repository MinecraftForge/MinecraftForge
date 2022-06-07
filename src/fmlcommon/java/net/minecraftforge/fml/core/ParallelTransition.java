/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.core;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ThreadSelector;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

record ParallelTransition(ModLoadingStage stage, Class<? extends ParallelDispatchEvent> event) implements IModStateTransition {
    @Override
    public Supplier<Stream<EventGenerator<?>>> eventFunctionStream() {
        return () -> Stream.of(IModStateTransition.EventGenerator.fromFunction(LamdbaExceptionUtils.rethrowFunction((ModContainer mc) -> event.getConstructor(ModContainer.class, ModLoadingStage.class).newInstance(mc, stage))));
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

    @Override
    public BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<Void>> preDispatchHook() {
        return (t, f) -> CompletableFuture.completedFuture(null);
    }

    @Override
    public BiFunction<Executor, ? extends EventGenerator<?>, CompletableFuture<Void>> postDispatchHook() {
        return (t, f) -> CompletableFuture.completedFuture(null);
    }
}
