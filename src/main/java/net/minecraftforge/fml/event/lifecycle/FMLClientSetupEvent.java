/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * This is the second of four commonly called events during mod lifecycle startup.
 *
 * Called before {@link InterModEnqueueEvent}
 * Called after {@link FMLCommonSetupEvent}
 *
 * Called on {@link net.minecraftforge.api.distmarker.Dist#CLIENT} - the game client.
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
