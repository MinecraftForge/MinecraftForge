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

import java.util.function.Function;

/**
 * Supplied as a param to your mod's constructor to get access
 * to the mod EventBus and various mod-specific objects.
 */
public final class FMLConstructModEvent extends ParallelDispatchEvent implements SelfDestructing {
    public static final Function<BusGroup, EventBus<FMLConstructModEvent>> BUS =
            busGroup -> ParallelDispatchEvent.busSupplier(busGroup, FMLConstructModEvent.class);

    public FMLConstructModEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container, stage);
    }
}
