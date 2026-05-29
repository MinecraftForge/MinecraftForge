/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.event.lifecycle;

import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftsingularity.fml.ModContainer;
import net.minecraftsingularity.fml.ModLoadingStage;
import net.minecraftsingularity.fml.event.IModBusEvent;

/**
 * This is the second of four commonly called events during mod lifecycle startup.
 *
 * Called before {@link InterModEnqueueEvent}
 * Called after {@link FMLCommonSetupEvent}
 *
 * Called on {@link net.minecraftsingularity.api.distmarker.Dist#CLIENT} - the game client.
 *
 * Alternative to {@link FMLDedicatedServerSetupEvent}.
 *
 * Do client only setup with this event, such as KeyBindings.
 *
 * This is a parallel dispatch event.
 */
public final class FMLClientSetupEvent extends ParallelDispatchEvent implements SelfDestructing {
    public static EventBus<FMLClientSetupEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, FMLClientSetupEvent.class);
    }

    public FMLClientSetupEvent(ModContainer container, ModLoadingStage stage) {
        super(container, stage);
    }
}
