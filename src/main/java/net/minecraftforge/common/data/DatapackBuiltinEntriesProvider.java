/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DatapackBuiltinEntriesProvider extends RegistriesDatapackGenerator
{
    public DatapackBuiltinEntriesProvider(PackOutput output, Supplier<HolderLookup.Provider> registriesSupplier)
    {
        super(output, CompletableFuture.supplyAsync(registriesSupplier, Util.backgroundExecutor()));
    }

    public DatapackBuiltinEntriesProvider(PackOutput output, RegistrySetBuilder datapackEntriesBuilder)
    {
        this(output, () -> createLookup(datapackEntriesBuilder));
    }

    public static HolderLookup.Provider createLookup(RegistrySetBuilder datapackEntriesBuilder) {
        return datapackEntriesBuilder.build(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    }
}
