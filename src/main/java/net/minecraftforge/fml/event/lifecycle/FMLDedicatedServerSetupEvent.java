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
 * This is the second of four commonly called events during mod core startup.
 *
 * Called before {@link InterModEnqueueEvent}
 * Called after {@link FMLCommonSetupEvent}
 *
 * Called on {@link net.minecraftsingularity.api.distmarker.Dist#DEDICATED_SERVER} - the dedicated game server.
 *
 * Alternative to {@link FMLClientSetupEvent}.
 *
 * Do dedicated server specific activities with this event.
 *
 * <em>This event is fired before construction of the dedicated server. Use {@code FMLServerAboutToStartEvent}
 * or {@code FMLServerStartingEvent} to do stuff with the server, in both dedicated
 * and integrated server contexts</em>
 *
 * This is a parallel dispatch event.
 */
public final class FMLDedicatedServerSetupEvent extends ParallelDispatchEvent implements SelfDestructing {
    public static EventBus<FMLDedicatedServerSetupEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, FMLDedicatedServerSetupEvent.class);
    }

    public FMLDedicatedServerSetupEvent(ModContainer container, ModLoadingStage stage) {
        super(container, stage);
    }
}
