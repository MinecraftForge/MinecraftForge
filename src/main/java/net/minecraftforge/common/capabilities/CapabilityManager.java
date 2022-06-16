/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;

import static net.minecraftforge.fml.Logging.CAPABILITIES;

/**
 * The Capability Manager is responsible for making and 
 * providing {@link CapabilityType} instances.
 */
public enum CapabilityManager
{
    INSTANCE;

	private static final Logger LOGGER = LogManager.getLogger();
    private static final IdentityHashMap<ResourceLocation, CapabilityType<?>> PROVIDERS = new IdentityHashMap<>();	

    /**
     * Retrieve a {@link CapabilityType}.  The returned type may not be registered.
     * @param <T> The supertype of all instances of this capability type.
     * @param id The ID of this capability type.
     * @return A {@link CapabilityType} for the provided key.
     */
    public static <T> @NotNull CapabilityType<T> get(ResourceLocation id)
    {
        return INSTANCE.get(id, false);
    }

    /**
     * Internal API for registering capability types.
     * @see {@link RegisterCapabilitiesEvent}
     */
    @SuppressWarnings("unchecked")
    <T> @NotNull CapabilityType<T> get(ResourceLocation id, boolean registering)
    {
        CapabilityType<T> cap;

        synchronized (PROVIDERS)
        {
            cap = (CapabilityType<T>)PROVIDERS.computeIfAbsent(id, CapabilityType::new);

        }

        if (registering)
        {
            synchronized (cap)
            {
                if (cap.isRegistered())
                {
                    LOGGER.error(CAPABILITIES, "Cannot register capability implementation multiple times : {}", id);
                    throw new IllegalArgumentException("Cannot register a capability implementation multiple times : "+ id);
                }
                else
                {
                    cap.onRegister();
                }
            }
        }

        return cap;
    }
}