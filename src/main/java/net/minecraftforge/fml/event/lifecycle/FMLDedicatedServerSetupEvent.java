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
 * This is the second of four commonly called events during mod core startup.
 *
 * Called before {@link InterModEnqueueEvent}
 * Called after {@link FMLCommonSetupEvent}
 *
 * Called on {@link net.minecraftforge.api.distmarker.Dist#DEDICATED_SERVER} - the dedicated game server.
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
