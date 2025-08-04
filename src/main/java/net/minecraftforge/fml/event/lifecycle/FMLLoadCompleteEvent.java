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
