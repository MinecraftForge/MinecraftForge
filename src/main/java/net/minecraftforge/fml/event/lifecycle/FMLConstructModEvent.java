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
 * Supplied as a param to your mod's constructor to get access
 * to the mod EventBus and various mod-specific objects.
 */
public final class FMLConstructModEvent extends ParallelDispatchEvent implements SelfDestructing {
    public static EventBus<FMLConstructModEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, FMLConstructModEvent.class);
    }

    public FMLConstructModEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container, stage);
    }
}
