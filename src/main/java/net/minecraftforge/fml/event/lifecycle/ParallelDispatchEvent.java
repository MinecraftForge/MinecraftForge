/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModContainer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ParallelDispatchEvent extends ModLifecycleEvent {
    public ParallelDispatchEvent(final ModContainer container) {
        super(container);
    }

    private Optional<DeferredWorkQueue> getQueue() {
        return DeferredWorkQueue.lookup(Optional.of(getClass()));
    }

    public CompletableFuture<Void> enqueueWork(Runnable work) {
        return getQueue().map(q->q.enqueueWork(getContainer().getModInfo(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
    }

    public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
        return getQueue().map(q->q.enqueueWork(getContainer().getModInfo(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
    }
}