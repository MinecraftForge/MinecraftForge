/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.worldgen.features.OreFeatures;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * An extension of the {@link RegistriesDatapackGenerator} which properly handles
 * referencing existing dynamic registry objects within another dynamic registry
 * object.
 */
public class DatapackBuiltinEntriesProvider extends RegistriesDatapackGenerator
{

    private final CompletableFuture<HolderLookup.Provider> fullRegistries;

    /**
     * Constructs a new datapack provider which generates all registry objects
     * from the provided mods using the holder.
     *
     * @param output the target directory of the data generator
     * @param registries a future of patched registries
     * @param modIds a set of mod ids to generate the dynamic registry objects of
     */
    public DatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<RegistrySetBuilder.PatchedRegistries> registries, Set<String> modIds)
    {
        super(output, registries.thenApply(RegistrySetBuilder.PatchedRegistries::patches), modIds);
        this.fullRegistries = registries.thenApply(RegistrySetBuilder.PatchedRegistries::full);
    }

    /**
     * Constructs a new datapack provider which generates all registry objects
     * from the provided mods using the holder. All entries that need to be
     * bootstrapped are provided within the {@link RegistrySetBuilder}.
     *
     * @param output the target directory of the data generator
     * @param registries a future of a lookup for registries and their objects
     * @param registryBuilder a builder containing the dynamic registry objects added by this provider
     * @param modIds a set of mod ids to generate the dynamic registry objects of
     */
    public DatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder registryBuilder, Set<String> modIds)
    {
        this(output, RegistryPatchGenerator.createLookup(registries, registryBuilder), modIds);
    }

    /**
     * Gets the future of the full registry lookup containing all added elements.<br>
     * Example usage:<br>
     * <pre>{@code
     * HolderLookup.Provider provider = this.fullRegistries.join();
     * Holder<?> holder = provider.lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(ResourceKey to a modded feature);
     * // The returned holder can then be used to register a PlacedFeature
     * }</pre>
     * @return the future of the full registry lookup
     */
    public CompletableFuture<HolderLookup.Provider> getFullRegistries()
    {
        return this.fullRegistries;
    }
}
