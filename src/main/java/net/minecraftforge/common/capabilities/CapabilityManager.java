/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.IdentityHashMap;
import java.util.List;

import static net.minecraftforge.fml.Logging.CAPABILITIES;

public enum CapabilityManager
{
    INSTANCE;
    static final Logger LOGGER = LogManager.getLogger();


    public static <T> Capability<T> get(CapabilityToken<T> type)
    {
        return INSTANCE.get(type.getType(), false);
    }

    @SuppressWarnings("unchecked")
    <T> Capability<T> get(String realName, boolean registering)
    {
        Capability<T> cap;

        synchronized (providers)
        {
            realName = realName.intern();
            cap = (Capability<T>)providers.computeIfAbsent(realName, Capability::new);

        }


        if (registering)
        {
            synchronized (cap)
            {
                if (cap.isRegistered())
                {
                    LOGGER.error(CAPABILITIES, "Cannot register capability implementation multiple times : {}", realName);
                    throw new IllegalArgumentException("Cannot register a capability implementation multiple times : "+ realName);
                }
                else
                {
                    cap.onRegister();
                }
            }
        }

        return cap;
    }

    // INTERNAL
    private final IdentityHashMap<String, Capability<?>> providers = new IdentityHashMap<>();
    public void injectCapabilities(List<ModFileScanData> data)
    {
        var event = new RegisterCapabilitiesEvent();
        ModLoader.get().postEvent(event);
    }
}
