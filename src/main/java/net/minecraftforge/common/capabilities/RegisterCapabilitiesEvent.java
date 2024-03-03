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
 */
public final class RegisterCapabilitiesEvent extends Event implements IModBusEvent
{
    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     *
     * @param type The type to be registered
     */
    public <T> void register(Class<T> type)
    {
        Objects.requireNonNull(type,"Attempted to register a capability with invalid type");
        CapabilityManager.INSTANCE.get(Type.getInternalName(type), true);
    }
}