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

/**
 * The Capability Manager is responsible for managing {@link Capability} instances.
 * @See {@link CapabilityManager#get(String, boolean)} to retrieve an instance.
 */
public enum CapabilityManager
{
    INSTANCE;
    static final Logger LOGGER = LogManager.getLogger();


    /**
     * Acquires the unique {@link Capability} instance for the specified type.<br>
     * Usage: <pre>public static final Capability<IMyInterface> MY_CAP = CapabilityManager.get(new CapabilityToken<>(){});</pre>
     * @param <T> The generic type of the capability.
     * @param type An anonymous {@link CapabilityToken} subclass, created inline.
     * @return A {@link Capability} instance, typed as requested.  Will never be null.
     */
    public static <T> Capability<T> get(CapabilityToken<T> type)
    {
        return INSTANCE.get(type.getType(), false);
    }

    /**
     * Internal API for creating/retrieving {@link Capability} instances.<br>
     * This method is used by {@link RegisterCapabilitiesEvent} to create {@link Capability} instances.
     * @param <T> The generic type of the capability.
     * @param realName The name of the generic type as defined by {@link CapabilityToken}
     * @param registering If true, the {@link Capability} instance will be marked as registered.
     * @return A {@link Capability} instance, typed as requested.  Will never be null.
     */
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

    /**
     * INTERNAL API - Do not call!
     */
    public void injectCapabilities(List<ModFileScanData> data)
    {
        var event = new RegisterCapabilitiesEvent();
        ModLoader.get().postEvent(event);
    }
}
