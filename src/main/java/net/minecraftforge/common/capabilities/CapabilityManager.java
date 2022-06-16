/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.IdentityHashMap;

import static net.minecraftforge.fml.Logging.CAPABILITIES;

public enum CapabilityManager
{
    INSTANCE;

	private static final Logger LOGGER = LogManager.getLogger();
    private static final IdentityHashMap<ResourceLocation, CapabilityType<?>> PROVIDERS = new IdentityHashMap<>();	

    public static <T> CapabilityType<T> get(ResourceLocation id)
    {
        return INSTANCE.get(id, false);
    }

    @SuppressWarnings("unchecked")
    <T> CapabilityType<T> get(ResourceLocation id, boolean registering)
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