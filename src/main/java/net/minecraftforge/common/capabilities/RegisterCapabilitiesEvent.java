/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * This event fires when it is time to register your capabilities.
 * @see CapabilityType
 */
public final class RegisterCapabilitiesEvent extends Event implements IModBusEvent
{
    /**
     * Registers a capability to be consumed by others.
     * APIs that define the capability should call this.
     * To retrieve a {@link CapabilityType}, use {@link CapabilityManager#get(ResourceLocation)}
     *
     * @param type The type to be registered
     */
    public <T> void register(CapabilityType<T> cap)
    {
        Objects.requireNonNull(cap,"Attempted to register a null capability!");
        CapabilityManager.INSTANCE.get(cap.getId(), true);
    }
}