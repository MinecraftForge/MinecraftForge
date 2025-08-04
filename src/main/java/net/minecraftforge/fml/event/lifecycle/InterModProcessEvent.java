/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.Predicate;

/**
 * This is the fourth of four commonly called events during mod core startup.
 *
 * Called after {@link InterModEnqueueEvent}
 *
 * Retrieve {@link net.minecraftforge.fml.InterModComms} {@link net.minecraftforge.fml.InterModComms.IMCMessage} suppliers
 * and process them as you wish with this event.
 *
 * This is a parallel dispatch event.
 *
 * @see #getIMCStream()
 * @see #getIMCStream(Predicate)
 */
public final class InterModProcessEvent extends ParallelDispatchEvent {
    public static EventBus<InterModProcessEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, InterModProcessEvent.class);
    }

    public InterModProcessEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container, stage);
    }
}
