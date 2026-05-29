/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.event.lifecycle;

import net.minecraftsingularity.fml.DeferredWorkQueue;
import net.minecraftsingularity.fml.ModContainer;
import net.minecraftsingularity.fml.ModLoadingStage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class ParallelDispatchEvent extends ModLifecycleEvent {
    private final ModLoadingStage modLoadingStage;

    protected ParallelDispatchEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container);
        this.modLoadingStage = stage;
    }

    private Optional<DeferredWorkQueue> getQueue() {
        return DeferredWorkQueue.lookup(Optional.of(modLoadingStage));
    }

    public CompletableFuture<Void> enqueueWork(Runnable work) {
        return getQueue().map(q->q.enqueueWork(getContainer(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
    }

    public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
        return getQueue().map(q->q.enqueueWork(getContainer(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
    }
}
