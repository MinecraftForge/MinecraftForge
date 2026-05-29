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
 * This is a mostly internal event fired to mod containers that indicates that loading is complete. Mods should not
 * in general override or otherwise attempt to implement this event.
 *
 * @author cpw
 */
public final class FMLLoadCompleteEvent extends ParallelDispatchEvent implements SelfDestructing {
    public static EventBus<FMLLoadCompleteEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, FMLLoadCompleteEvent.class);
    }

    public FMLLoadCompleteEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container, stage);
    }
}
