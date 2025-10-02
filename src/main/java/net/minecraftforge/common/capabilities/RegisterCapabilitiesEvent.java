/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.Objects;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NullMarked;
import org.objectweb.asm.Type;

/**
 * This event fires when it is time to register your capabilities.
 * @see Capability
 *
 * @deprecated Use {@link AutoRegisterCapability} annotation on your class.
 */
@Deprecated(forRemoval = true, since = "1.21")
@NullMarked
public record RegisterCapabilitiesEvent() implements RecordEvent {
    public static final EventBus<RegisterCapabilitiesEvent> BUS = EventBus.create(RegisterCapabilitiesEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<RegisterCapabilitiesEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * This is meant to allow Capability consumers to have soft dependencies on the Capability type.
     * But be automatically notified when the Class actually exists. Meaning it's safe to create their implementations.
     *
     * To retrieve the Capability instance, use the {@link CapabilityManager} gets functions.
     */
    public <T> void register(Class<T> type) {
        Objects.requireNonNull(type, "Attempted to register a capability with invalid type");
        CapabilityManager.get(Type.getInternalName(type), null, true);
    }
}
