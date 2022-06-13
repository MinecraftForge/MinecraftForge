/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

@ApiStatus.Internal
public final class DataPackRegistriesHooks
{
    private DataPackRegistriesHooks() {} // utility class
    
    private static Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> REGISTRY_ACCESS_REGISTRIES_COPY;
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES = new HashSet<>();
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES_VIEW = Collections.unmodifiableSet(SYNCED_CUSTOM_REGISTRIES); 

    /* Internal forge hook for retaining mutable access to RegistryAccess's codec registry when it bootstraps. */
    public static Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> grabBuiltinRegistries(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder)
    {
        REGISTRY_ACCESS_REGISTRIES_COPY = new HashMap<>(builder.build());
        SYNCED_CUSTOM_REGISTRIES.clear();
        return Collections.unmodifiableMap(REGISTRY_ACCESS_REGISTRIES_COPY);
    }

    /* Internal forge method, registers a datapack registry codec and folder. */
    public static <T> void addRegistryCodec(@NotNull RegistryAccess.RegistryData<T> data)
    {
        ResourceKey<? extends Registry<T>> registryKey = data.key();
        REGISTRY_ACCESS_REGISTRIES_COPY.put(registryKey, data);
        if (data.networkCodec() != null)
        {
            SYNCED_CUSTOM_REGISTRIES.add(registryKey);
        }
    }
    
    /**
     * {@return unmodifiable view of the set of synced non-vanilla datapack registry IDs}
     * Clients must have each of a server's synced datapack registries to be able to connect to that server;
     * vanilla clients therefore cannot connect if this list is non-empty on the server.
     */
    public static Set<ResourceKey<? extends Registry<?>>> getSyncedCustomRegistries()
    {
        return SYNCED_CUSTOM_REGISTRIES_VIEW;
    }
}