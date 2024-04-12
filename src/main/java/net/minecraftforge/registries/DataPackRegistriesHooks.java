/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class DataPackRegistriesHooks {
    private DataPackRegistriesHooks() {} // utility class

    private static final Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES = new LinkedHashMap<>();
    private static final List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES = new ArrayList<>();
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES = new HashSet<>();
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES_VIEW = Collections.unmodifiableSet(SYNCED_CUSTOM_REGISTRIES);

    /* Internal forge hook for retaining mutable access to RegistryAccess's codec registry when it bootstraps. */
    public static Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> grabNetworkableRegistries(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder) {
        if (!StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().equals(RegistrySynchronization.class))
            throw new IllegalCallerException("Attempted to call DataPackRegistriesHooks#grabNetworkableRegistries!");
        NETWORKABLE_REGISTRIES.forEach(builder::put);
        NETWORKABLE_REGISTRIES.clear();
        NETWORKABLE_REGISTRIES.putAll(builder.build());
        return Collections.unmodifiableMap(NETWORKABLE_REGISTRIES);
    }

    /* Internal forge method, registers a datapack registry codec and folder. */
    static <T> void addRegistryCodec(DataPackRegistryEvent.DataPackRegistryData<T> data) {
        RegistryDataLoader.RegistryData<T> loaderData = data.loaderData();
        WORLDGEN_REGISTRIES.add(loaderData);
        if (data.networkCodec() != null) {
            SYNCED_CUSTOM_REGISTRIES.add(loaderData.key());
            NETWORKABLE_REGISTRIES.put(loaderData.key(), new RegistrySynchronization.NetworkedRegistryData<>(loaderData.key(), data.networkCodec()));
        }
    }

    /**
     * {@return An unmodifiable view of the list of datapack registries}.
     * These registries are loaded from per-world datapacks on server startup.
     */
    @Deprecated // Use RegistryDataLoader#WORLDGEN_REGISTRIES instead
    public static List<RegistryDataLoader.RegistryData<?>> getDataPackRegistries() {
        return Collections.unmodifiableList(WORLDGEN_REGISTRIES);
    }

    @Deprecated // Use RegistryDataLoader#WORLDGEN_REGISTRIES instead
    public static Stream<RegistryDataLoader.RegistryData<?>> getDataPackRegistriesWithDimensions() {
        return WORLDGEN_REGISTRIES.stream();
    }

    /**
     * Captures a mutable view of {@link RegistryDataLoader#WORLDGEN_REGISTRIES} so that we can register modded entries during the registry event.
     * All uses should use that field instead of this class.
     * @param vanilla The vanilla worldgen registries
     * @return An unmodifiable list of worldgen registries
     */
    public static List<RegistryDataLoader.RegistryData<?>> grabWorldgenRegistries(RegistryDataLoader.RegistryData<?>... vanilla) {
        if (!StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().equals(RegistryDataLoader.class))
            throw new IllegalCallerException("Attempted to call DataPackRegistriesHooks#grabWorldgenRegistries!");
        WORLDGEN_REGISTRIES.addAll(Arrays.asList(vanilla));
        return Collections.unmodifiableList(WORLDGEN_REGISTRIES);
    }

    /**
     * {@return An unmodifiable view of the set of synced non-vanilla datapack registry IDs}
     * Clients must have each of a server's synced datapack registries to be able to connect to that server;
     * vanilla clients therefore cannot connect if this list is non-empty on the server.
     */
    public static Set<ResourceKey<? extends Registry<?>>> getSyncedCustomRegistries() {
        return SYNCED_CUSTOM_REGISTRIES_VIEW;
    }
}
