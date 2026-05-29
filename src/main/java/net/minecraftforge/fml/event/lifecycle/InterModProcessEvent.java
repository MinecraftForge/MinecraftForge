/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.event.lifecycle;

import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.fml.ModContainer;
import net.minecraftsingularity.fml.ModLoadingStage;
import net.minecraftsingularity.fml.event.IModBusEvent;

import java.util.function.Predicate;

/**
 * This is the fourth of four commonly called events during mod core startup.
 *
 * Called after {@link InterModEnqueueEvent}
 *
 * Retrieve {@link net.minecraftsingularity.fml.InterModComms} {@link net.minecraftsingularity.fml.InterModComms.IMCMessage} suppliers
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
