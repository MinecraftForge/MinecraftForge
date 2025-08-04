/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/** Use {@link net.minecraft.data.tags.EnchantmentTagsProvider Vanilla's EnchantmentTagsProvider} */
@Deprecated(forRemoval = true, since = "1.21.6")
public abstract class EnchantmentTagsProvider extends net.minecraft.data.tags.EnchantmentTagsProvider {
    public EnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }
}
