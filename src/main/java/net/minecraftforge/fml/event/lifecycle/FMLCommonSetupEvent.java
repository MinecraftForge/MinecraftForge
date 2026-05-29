/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.event.lifecycle;

import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftsingularity.fml.ModContainer;
import net.minecraftsingularity.fml.DeferredWorkQueue;
import net.minecraftsingularity.fml.ModLoadingStage;
import net.minecraftsingularity.fml.event.IModBusEvent;

import java.util.function.Consumer;

/**
 * This is the first of four commonly called events during mod initialization.
 * <br><br>
 * Called after {@link net.minecraftsingularity.registries.RegisterEvent} events have been fired and before 
 * {@link FMLClientSetupEvent} or {@link FMLDedicatedServerSetupEvent} during mod startup.
 * <br><br>
 * Either register your listener using {@link net.minecraftsingularity.fml.javafmlmod.AutomaticEventSubscriber} and
 * {@link net.minecraftsingularity.eventbus.api.listener.SubscribeEvent} or
 * {@link net.minecraftsingularity.eventbus.api.bus.EventBus#addListener(Consumer)} in your constructor.
 * <br><br>
 * Most non-specific mod setup will be performed here. Note that this is a parallel dispatched event - you cannot
 * interact with game state in this event.
 *
 * @see DeferredWorkQueue to enqueue work to run on the main game thread after this event has
 * completed dispatch
 */
public final class FMLCommonSetupEvent extends ParallelDispatchEvent implements SelfDestructing {
    public static EventBus<FMLCommonSetupEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, FMLCommonSetupEvent.class);
    }

    public FMLCommonSetupEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container, stage);
    }
}
