/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DataPackRegistriesHooks
{
    private static Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> REGISTRY_ACCESS_REGISTRIES_COPY;
    private static Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES = new HashSet<>();

    /** Modders, DO NOT USE. Internal use only */
    @Deprecated
    public static Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> grabBuiltinRegistries(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder)
    {
        REGISTRY_ACCESS_REGISTRIES_COPY = new HashMap<>(builder.build());
        SYNCED_CUSTOM_REGISTRIES = new HashSet<>();
        return Collections.unmodifiableMap(REGISTRY_ACCESS_REGISTRIES_COPY);
    }

    /* Registers a datapack registry codec and folder. Internal forge use only. */
    @Deprecated
    public static <T> void validateAndAddRegistryCodec(@NotNull RegistryAccess.RegistryData<T> data)
    {
        ResourceKey<? extends Registry<T>> registryKey = data.key();
        validateRegistryKey(registryKey);
        REGISTRY_ACCESS_REGISTRIES_COPY.put(registryKey, data);
        if (data.networkCodec() != null)
        {
            SYNCED_CUSTOM_REGISTRIES.add(registryKey);
        }
    }
    
    /**
     * @return unmodifiable view of the set of synced non-vanilla datapack registry IDs.
     * Clients must have each of a server's synced datapack registries to be able to connect to that server;
     * vanilla clients therefore cannot connect if this list is non-empty on the server.
     */
    public static Set<ResourceKey<? extends Registry<?>>> getSyncedCustomRegistries()
    {
        return Collections.unmodifiableSet(SYNCED_CUSTOM_REGISTRIES);
    }

    /* Enforces the modid:modid/folder convention for custom datapack registries, ensuring loading folders are properly namespaced */
    private static void validateRegistryKey(ResourceKey<? extends Registry<?>> registryKey)
    {
        var location = registryKey.location();
        var prefix = location.getNamespace() + '/';

        if (!location.getPath().startsWith(prefix))
        {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Registry path must be prefixed with '%s'", prefix));
        }
    }
}