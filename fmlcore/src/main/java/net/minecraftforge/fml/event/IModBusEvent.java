/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;

/**
 * Marker interface for events dispatched on the ModLifecycle event bus instead of the primary event bus
 */
public interface IModBusEvent extends InheritableEvent {
    /**
     * @param modEventBusGroup Obtained from {@link net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext#getModBusGroup()} or your language provider's equivalent.
     * @param eventClass The event type you want to get the associated bus for.
     */
    static <T extends IModBusEvent> EventBus<T> getBus(BusGroup modEventBusGroup, Class<T> eventClass) {
        // Do not copy! Temporary solution until the FML rewrite is complete. May throw in future
        // without prior notice.
        //
        // Mods creating their own events should directly assign the result of the create method to a
        // static final field in their event class, without wrapping or calling multiple times.
        //
        // Mods using existing events should instead use the existing static final BUS field in the
        // event class (for most events) or getBus(BusGroup) method (for mod bus events).
        return EventBus.create(modEventBusGroup, eventClass);
    }
}
