/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class DataPackRegistriesHooks {
    private DataPackRegistriesHooks() {} // utility class

    // Mutable views of vanilla registries
    private static final List<RegistryDataLoader.RegistryData<?>> SYNCHRONIZED_REGISTRIES = new ArrayList<>();
    private static final List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES = new ArrayList<>();
    private static final Set<ResourceKey<? extends Registry<?>>> NETWORKABLE_REGISTRIES = new HashSet<>();

    // Out custom registries, things we need to make sure the client has
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCHRONIZED_REGISTRIES_CUSTOM = new HashSet<>();
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCHRONIZED_REGISTRIESD_CUSTOM_VIEW = Collections.unmodifiableSet(SYNCHRONIZED_REGISTRIES_CUSTOM);

    /* Internal forge hook for retaining mutable access to RegistryAccess's codec registry when it bootstraps. */
    public static List<RegistryDataLoader.RegistryData<?>> grabSynchronizedRegistries(RegistryDataLoader.RegistryData<?>... vanilla) {
        if (!StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().equals(RegistryDataLoader.class))
            throw new IllegalCallerException("Attempted to call DataPackRegistriesHooks#grabSynchronizedRegistries!");
        for (var reg : vanilla)
            SYNCHRONIZED_REGISTRIES.add(reg);
        return Collections.unmodifiableList(SYNCHRONIZED_REGISTRIES);
    }

    /* Internal forge method, registers a datapack registry codec and folder. */
    static <T> void addRegistryCodec(DataPackRegistryEvent.DataPackRegistryData<T> data) {
        RegistryDataLoader.RegistryData<T> loaderData = data.loaderData();
        WORLDGEN_REGISTRIES.add(loaderData);
        if (data.networkCodec() != null) {
            NETWORKABLE_REGISTRIES.add(loaderData.key());
            SYNCHRONIZED_REGISTRIES_CUSTOM.add(loaderData.key());
            SYNCHRONIZED_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(loaderData.key(), data.networkCodec(), false));
        }
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
     * Captures a mutable view of {@link RegistrySynchronization#NETWORKABLE_REGISTRIES} so that we can register modded entries during the registry event.
     * All users should use that field instead of this class.
     *
     * @param vanilla Supplier for the default vanilla values
     * @return An unmodifiable set of networksable registry names
     */
    public static Set<ResourceKey<? extends Registry<?>>> grabNetworkableRegistries(Supplier<Set<ResourceKey<? extends Registry<?>>>> vanilla) {
        if (!StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().equals(RegistrySynchronization.class))
            throw new IllegalCallerException("Attempted to call DataPackRegistriesHooks#grabNetworkableRegistries!");

        NETWORKABLE_REGISTRIES.addAll(vanilla.get());
        return Collections.unmodifiableSet(NETWORKABLE_REGISTRIES);
    }

    /**
     * {@return An unmodifiable view of the set of synced non-vanilla datapack registry IDs}
     * Clients must have each of a server's synced datapack registries to be able to connect to that server;
     * vanilla clients therefore cannot connect if this list is non-empty on the server.
     */
    public static Set<ResourceKey<? extends Registry<?>>> getSyncedCustomRegistries() {
        return SYNCHRONIZED_REGISTRIESD_CUSTOM_VIEW;
    }
}
