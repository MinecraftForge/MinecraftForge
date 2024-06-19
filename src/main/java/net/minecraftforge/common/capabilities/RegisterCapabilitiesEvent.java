/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.Objects;

import org.objectweb.asm.Type;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * This event fires when it is time to register your capabilities.
 * @see Capability
 *
 * @deprecated Use {@link AutoRegisterCapability} annotation on your class.
 */
@Deprecated(forRemoval = true, since = "1.21")
public final class RegisterCapabilitiesEvent extends Event implements IModBusEvent {
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