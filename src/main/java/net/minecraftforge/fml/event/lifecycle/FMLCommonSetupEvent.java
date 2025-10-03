/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.Consumer;

/**
 * This is the first of four commonly called events during mod initialization.
 * <br><br>
 * Called after {@link net.minecraftforge.registries.RegisterEvent} events have been fired and before 
 * {@link FMLClientSetupEvent} or {@link FMLDedicatedServerSetupEvent} during mod startup.
 * <br><br>
 * Either register your listener using {@link net.minecraftforge.fml.javafmlmod.AutomaticEventSubscriber} and
 * {@link net.minecraftforge.eventbus.api.listener.SubscribeEvent} or
 * {@link net.minecraftforge.eventbus.api.bus.EventBus#addListener(Consumer)} in your constructor.
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
