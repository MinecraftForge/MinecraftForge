/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApiStatus.Internal
public final class DataPackRegistriesHooks
{
    private DataPackRegistriesHooks() {} // utility class

    private static final List<RegistryDataLoader.RegistryData<?>> DATA_PACK_REGISTRIES = new ArrayList<>(RegistryDataLoader.WORLDGEN_REGISTRIES);
    private static final List<RegistryDataLoader.RegistryData<?>> DATA_PACK_REGISTRIES_VIEW = Collections.unmodifiableList(DATA_PACK_REGISTRIES);
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES = new HashSet<>();
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES_VIEW = Collections.unmodifiableSet(SYNCED_CUSTOM_REGISTRIES);

    /* Internal forge method, registers a datapack registry codec and folder. */
    static <T> void addRegistryCodec(DataPackRegistryEvent.DataPackRegistryData<T> data)
    {
        RegistryDataLoader.RegistryData<T> loaderData = data.loaderData();
        DATA_PACK_REGISTRIES.add(loaderData);
        if (data.networkCodec() != null)
            SYNCED_CUSTOM_REGISTRIES.add(loaderData.key());
    }

    /**
     * {@return An unmodifiable view of the list of datapack registries}.
     * These registries are loaded from per-world datapacks on server startup.
     */
    public static List<RegistryDataLoader.RegistryData<?>> getDataPackRegistries()
    {
        return DATA_PACK_REGISTRIES_VIEW;
    }

    /**
     * {@return An unmodifiable view of the set of synced non-vanilla datapack registry IDs}
     * Clients must have each of a server's synced datapack registries to be able to connect to that server;
     * vanilla clients therefore cannot connect if this list is non-empty on the server.
     */
    public static Set<ResourceKey<? extends Registry<?>>> getSyncedCustomRegistries()
    {
        return SYNCED_CUSTOM_REGISTRIES_VIEW;
    }
}