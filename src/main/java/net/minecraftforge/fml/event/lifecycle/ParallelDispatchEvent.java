/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.Event;
import net.minecraftforge.eventbus.api.event.MarkerEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@MarkerEvent
public sealed class ParallelDispatchEvent implements ModLifecycleEvent
        permits FMLClientSetupEvent, FMLCommonSetupEvent, FMLConstructModEvent, FMLDedicatedServerSetupEvent,
                FMLLoadCompleteEvent, InterModEnqueueEvent, InterModProcessEvent {
    private final ModContainer container;
    private final ModLoadingStage modLoadingStage;

    public ParallelDispatchEvent(final ModContainer container, final ModLoadingStage stage) {
        this.container = container;
        this.modLoadingStage = stage;
    }

    private Optional<DeferredWorkQueue> getQueue() {
        return DeferredWorkQueue.lookup(Optional.of(modLoadingStage));
    }

    public CompletableFuture<Void> enqueueWork(Runnable work) {
        return getQueue().map(q->q.enqueueWork(container(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
    }

    public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
        return getQueue().map(q->q.enqueueWork(container(), work)).orElseThrow(()->new RuntimeException("No work queue found!"));
    }

    @Override
    public ModContainer container() {
        return container;
    }

    @Override
    public String toString() {
        return description();
    }

    protected static <T extends Event> EventBus<T> busSupplier(BusGroup busGroup, Class<T> eventType) {
        return EventBus.create(busGroup, eventType);
    }
}
