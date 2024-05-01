/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ThreadSelector;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

record SerialTransition<T extends Event & IModBusEvent>(Supplier<Stream<IModStateTransition.EventGenerator<?>>> eventStream, BiFunction<Executor, ? extends EventGenerator<T>, CompletableFuture<List<Throwable>>> preDispatchHook, BiFunction<Executor, ? extends EventGenerator<T>, CompletableFuture<List<Throwable>>> postDispatchHook, BiFunction<Executor, CompletableFuture<List<Throwable>>, CompletableFuture<List<Throwable>>> finalActivityGenerator) implements IModStateTransition {
    public static <T extends Event & IModBusEvent> SerialTransition<T> of(Supplier<Stream<IModStateTransition.EventGenerator<?>>> eventStream) {
        return new SerialTransition<T>(eventStream, (t,f)->CompletableFuture.completedFuture(Collections.emptyList()), (t, f)->CompletableFuture.completedFuture(Collections.emptyList()), (e, prev) ->prev.thenApplyAsync(Function.identity(), e));
    }
    @Override
    public Supplier<Stream<EventGenerator<?>>> eventFunctionStream() {
        return eventStream;
    }

    @Override
    public ThreadSelector threadSelector() {
        return ThreadSelector.SYNC;
    }

    // These never progress the ModLoadingStage..
    @Override
    public BiFunction<ModLoadingStage, Throwable, ModLoadingStage> nextModLoadingStage() {
        return ModLoadingStage::currentState;
    }
}
