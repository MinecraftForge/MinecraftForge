/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.event;

import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import org.jspecify.annotations.NonNull;

/**
 * Marker interface for mod lifecycle events dispatched on the
 * {@linkplain net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext#getModBusGroup() mod BusGroup} instead
 * of the {@linkplain BusGroup#DEFAULT default BusGroup}.
 *
 * @apiNote Each mod gets its own unique mod BusGroup instance, obtained from the mod loading context. Most events
 *          should go on the default BusGroup.
 */
public interface IModBusEvent extends InheritableEvent {
    /**
     * @param modEventBusGroup Obtained from {@link net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext#getModBusGroup()} or your language provider's equivalent.
     * @param eventClass The event type you want to get the associated bus for.
     */
    static <T extends IModBusEvent> @NonNull EventBus<@NonNull T> getBus(@NonNull BusGroup modEventBusGroup, @NonNull Class<T> eventClass) {
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
